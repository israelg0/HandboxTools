package es.handbox.tools.pojo;

import es.handbox.model.Resultado;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;


public class CategorizadorHandbox extends HandboxConnections {
    
    public CategorizadorHandbox() {
        super("feeds2hbx");

    }
    
    public CategorizadorHandbox(int limit, int version) {
        super("feeds2hbx");


        try {
            //Selecciono todos los objetos que tengan la categor�a gen�rica, eso significa que est� recien leido
            if (version==1)
            {
                        String query =
                            "select ID from "+PREFIJOV1+"posts where post_type=\"post\" order by ID desc limit " + limit;
                        
                        ResultSet posts = selectV1(query);
            
                        while (posts.next()) {
                            int idPost = posts.getInt(1);
                            categorizarPostv1(idPost);
                        }
            }
            else{
                if (version==1)
                {
                String query =
                    "select ID from "+PREFIJOV2+"posts where post_type=\"post\" order by ID desc limit " + limit;
                
                ResultSet posts = selectV2(query);
                
                while (posts.next()) {
                    int idPost = posts.getInt(1);
                    categorizarPostv2(idPost);
                    //this.categorizarEdadv2(idPost);
                }
                }
                else {
                    String query =
                        "select ID from "+PREFIJOV2+"posts where post_type=\"post\" order by ID desc limit " + limit;
                    
                    ResultSet posts = selectV2(query);
                    
                    while (posts.next()) {
                        int idPost = posts.getInt(1);
                        
                        this.categorizarEdadv2(idPost);
                    }
                }
            }
            
        } catch (SQLException sqle) {
            // TODO: Add catch code
            
        }

    }
    
    public void categorizarPostv2(int idPost) {
        
        try {
            
            int dificultadFacil = 0;
            int paraPeques = 0;
            //Como a d�a de hoy no me traigo los tags, me recorro el t�tulo (de momento) para a�adir tags
            String query = "SELECT  post_title,post_content FROM " + PREFIJOV2 + "posts where ID='" + idPost + "'";
            //Me traigo t�tulo y contenido del post para extraer posibles tags y categor�as
            ResultSet post = selectV2(query);
            if (post.next()) {
                Resultado.getResultado().getMensajelog().addLinea("Categorizando "+post.getString(1));
                //Del texto, sacamos las palabras, las contamos, si estan n veces repetidas miramos si son categoria.
                //Si tienen mas de 3 letras pero no se repite lo comparo con las etiquetas unicamente.
                String[] palabras =
                    post.getString(2).replaceAll("\\<.*?>",
                                                 "").split("[[ ]*|[,]*|[\"]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                HashMap words = new HashMap();
                for (int i = 0; i < palabras.length; i++) {

                    if ((palabras[i].length() > 3) && (!palabras[i].contains(")")) && (!palabras[i].contains("(")) &&
                        (!palabras[i].contains("<")) && (!palabras[i].contains(">")) &&
                        (!palabras[i].contains("handbox")))

                    {
                        int repeticiones = 1;
                        if (words.get(palabras[i]) != null) {
                            repeticiones = Integer.parseInt((String) words.get(palabras[i]));
                            repeticiones++;
                        }
                        words.put(palabras[i], repeticiones + "");
                        if (palabras[i].matches("f[a|�]cil[es]") || palabras[i].matches("sencillo[s]") ||
                            palabras[i].matches("tirado")) {
                            dificultadFacil++;
                        }
                        if (palabras[i].matches("ni�[o|a][s]") || palabras[i].matches("peque[s]") ||
                            palabras[i].matches("nen[e|a][s]")) {
                            paraPeques++;
                        }
                    }
                }
                ArrayList<String> keys = new ArrayList<String>(words.keySet());
                for (String key : keys) {

                    if ((Integer.parseInt((String) words.get(key)) > 1) && (key.length() > 3)) {
                        query =
                            "select x.term_taxonomy_id from " + PREFIJOV2 + "terms t, " + PREFIJOV2 +
                            "term_taxonomy x " + "where x.taxonomy='category' and x.term_id = t.term_id " +
                            "and (t.name = '" + key + "' or t.slug='" + key + "')";

                        ResultSet tags = selectV2(query);
                        while (tags.next()) {
                            //Lo a�ado como tag o categoria
                            String insert =
                                "INSERT INTO " + PREFIJOV2 +
                                "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " + idPost +
                                ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                            insertV2(insert);
                            Resultado.getResultado().getMensajelog().addLinea(key + ": " + words.get(key));
                            Resultado.getResultado().getMensajelog().addLinea(" cate");
                        }
                    } else {
                        if ((Integer.parseInt((String) words.get(key)) > 1) &&
                            (((String) words.get(key)).length() > 2)) {
                            query =
                                "select x.term_taxonomy_id from " + PREFIJOV2 + "terms t, " + PREFIJOV2 +
                                "term_taxonomy x " + "where x.taxonomy='post_tag' and x.term_id = t.term_id " +
                                "and (t.name = '" + key + "' or t.slug='" + key + "')";

                            try {
                                ResultSet tags = selectV2(query);
                                while (tags.next()) {
                                    //Lo a�ado como tag o categoria
                                    String insert =
                                        "INSERT INTO " + PREFIJOV2 +
                                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                        idPost + ", " + tags.getInt(1) +
                                        ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                    insertV2(insert);
                                    Resultado.getResultado().getMensajelog().addLinea(key + ": " + words.get(key));
                                    Resultado.getResultado().getMensajelog().addLinea(" tag");
                                }
                            } catch (SQLException sqle) {
                                // TODO: Add catch code
                                
                            }
                        }
                    }
                }
                //Del titulo saco categorias o etiquetas
                palabras = post.getString(1).split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                for (int i = 0; i < palabras.length; i++) {

                    if (palabras[i].length() > 3) {
                        query =
                            "select x.term_taxonomy_id from " + PREFIJOV2 + "terms t, " + PREFIJOV2 +
                            "term_taxonomy x " +
                            "where (x.taxonomy='category' or x.taxonomy='post_tag') and x.term_id = t.term_id " +
                            "and (t.name = '" + palabras[i] + "' or t.slug='" + palabras[i] + "')";

                        try {
                            ResultSet tags = selectV2(query);
                            while (tags.next()) {
                                //Lo a�ado como tag o categoria
                                String insert =
                                    "INSERT INTO " + PREFIJOV2 +
                                    "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                    idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                insertV2(insert);
                            }
                        } catch (Exception sqle) {
                            // TODO: Add catch code

                        }
                    }

                }


            }

            //Me traigo los tags para ver si alg�n tag es categor�a.
            query =
                "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV2 + "term_relationships r, " +
                PREFIJOV2 + "term_taxonomy t, " + PREFIJOV2 + "terms w " + "where r.object_id =" + idPost + " " +
                "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                "and t.taxonomy='post_tag'";
            ResultSet terminos = selectV2(query);
            //Si existe categor�a con ese tag, le asigno categor�a.
            while (terminos.next()) {

                query =
                    "select t.term_id from " + PREFIJOV2 + "terms t, " + PREFIJOV2 + "term_taxonomy x " +
                    "where x.taxonomy='category' and x.term_id = t.term_id " + "and (t.name = '" +
                    terminos.getString(2) + "' or t.slug='" + terminos.getString(2) + "')";

                try {
                    ResultSet tags = selectV2(query);
                    while (tags.next()) {
                        String insert =
                            "INSERT INTO " + PREFIJOV2 +
                            "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                            idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                        insertV2(insert);
                    }
                } catch (SQLException sqle) {
                    // TODO: Add catch code
                    
                }

            }

            //Reviso por �ltima vez las categorias, si no tiene las categorias 100 ni 44, le pongo por defecto la 44
            //41  77  87  94,   le pongo por defecto la 87

            query =
                "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV2 + "term_relationships r, " +
                PREFIJOV2 + "term_taxonomy t, " + PREFIJOV2 + "terms w " + "where r.object_id =" + idPost + " " +
                "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                "and t.taxonomy='category'";

            ResultSet categoriasFinales = selectV2(query);


            int tienedificultad = 0;

            while (categoriasFinales.next()) {
                if (categoriasFinales.getInt(1) == 41 || categoriasFinales.getInt(1) == 77 ||
                    categoriasFinales.getInt(1) == 87 || categoriasFinales.getInt(1) == 94) {

                    tienedificultad++;
                }
            }

            if (tienedificultad == 0) {
                String idDificultad = "87";
                if (dificultadFacil > 0)
                    idDificultad = "77";
                String insert =
                    "INSERT INTO " + PREFIJOV2 +
                    "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                    idDificultad + ", 0 )";
                insertV2(insert);
            }
            if (paraPeques > 0)
            {
                     String  idPeques = "100";
                    try {
                    String insert =
                        "INSERT INTO " + PREFIJOV1 +
                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                        idPeques + ", 0 )";
                        insertV1(insert);
                     } catch (Exception e) {
                    // TODO: Add catch code
                     }
            }

            //Borro categor�as principales

            String delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 40";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 56";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 70";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 72";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 96";
            insertV2(delete);
        } catch (SQLException sqle) {
            // TODO: Add catch code
            
        } catch (NumberFormatException nfe) {
            // TODO: Add catch code
            
        }
    }
    
    
    public void categorizarEdadv2(int idPost) {
        
        try {
            int yatienePeques = 0;
            int paraPeques = 0;
            //Como a d�a de hoy no me traigo los tags, me recorro el t�tulo (de momento) para a�adir tags
            String query = "SELECT  post_title,post_content FROM " + PREFIJOV2 + "posts where ID='" + idPost + "'";
            //Me traigo t�tulo y contenido del post para extraer posibles tags y categor�as
            ResultSet post = selectV2(query);
            if (post.next()) {
                Resultado.getResultado().getMensajelog().addLinea("categorizaredad " + post.getString(1));
                //Del texto, sacamos las palabras, las contamos, si estan n veces repetidas miramos si son categoria.
                //Si tienen mas de 3 letras pero no se repite lo comparo con las etiquetas unicamente.
                String[] palabras =
                    post.getString(2).replaceAll("\\<.*?>",
                                                 "").split("[[ ]*|[,]*|[\"]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                HashMap words = new HashMap();
                for (int i = 0; i < palabras.length; i++) {

                    if ((palabras[i].length() > 3) && (!palabras[i].contains(")")) && (!palabras[i].contains("(")) &&
                        (!palabras[i].contains("<")) && (!palabras[i].contains(">")) &&
                        (!palabras[i].contains("handbox")))

                    {
                        if (palabras[i].matches("ni�[o|a][s]") || palabras[i].matches("peque[s]") ||
                            palabras[i].matches("nen[e|a][s]")) {
                            paraPeques++;
                        }
                    }
                }
                
                                
            }

            //Compruebo que el post no tiene ya la categoria de ni�os.
            query = "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV2 + "term_relationships r, " +
                PREFIJOV2 + "term_taxonomy t, " + PREFIJOV2 + "terms w " + "where r.object_id =" + idPost + " " +
                "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                "and t.taxonomy='category'";;
            ResultSet categorias = selectV2(query);
            //Si existe categor�a con ese tag, le asigno categor�a.
            while (categorias.next()) {
                if (categorias.getInt(1)==100)
                    yatienePeques++;
            }

            //Reviso por �ltima vez las categorias, si no tiene las categorias 100 ni 44, le pongo por defecto la 44
            //41  77  87  94,   le pongo por defecto la 87
            if (paraPeques > 0)
            {
                     String  idPeques = "100";
                    try {
                    String insert =
                        "INSERT INTO " + PREFIJOV2 +
                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                        idPeques + ", 0 )";
                        insertV2(insert);
                        Resultado.getResultado().getMensajelog().addLinea("Post " + idPost+ "  de ni�os");
                        //Si tiene la categor�a adultos la borro
                        String delete =
                            "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                            " and term_taxonomy_id = 44";
                        insertV2(delete);
                        
                     } catch (Exception e) {
                    // TODO: Add catch code
                     }
            }

            //Borro categor�as principales

            String delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 40";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 56";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 70";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 72";
            insertV2(delete);
            delete =
                "delete from " + PREFIJOV2 + "term_relationships where object_id = " + idPost +
                " and term_taxonomy_id = 96";
            insertV2(delete);
        } catch (SQLException sqle) {
            // TODO: Add catch code
            
        } catch (NumberFormatException nfe) {
            // TODO: Add catch code
            
        }
    }
    
    /**
     *
     *
     * @param idPost
     * @param palabrasReservadas
     * @return
     */
    
    public boolean contienePalabra(int idPost, ArrayList<String> palabrasReservadas) {
            try {
            //De momento solo compruebo el título
            String query = "SELECT  post_title FROM " + PREFIJOV1 + "posts where ID='" + idPost + "'";
            //Me traigo t�tulo y contenido del post para extraer posibles tags y categor�as
            ResultSet post = selectV1(query);
            if (post.next()) {
                Resultado.getResultado().getMensajelog().addLinea(post.getString(1) + " �Palabras prohibidas?");
                //Del texto, sacamos las palabras, 
                String[] palabras =
                    post.getString(1).replaceAll("\\<.*?>",
                                                 "").split("[[ ]*|[,]*|[\"]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                //Si contiene alguna palabra reservada devuelve "true"
                for (int i = 0; i < palabras.length; i++) {
                    for (int j = 0; j < palabrasReservadas.size(); j++) {

                        if (palabras[i].equalsIgnoreCase(palabrasReservadas.get(j))) {
                            Resultado.getResultado().getMensajelog().addLinea("SI");
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        }
        Resultado.getResultado().getMensajelog().addLinea("NO");
            return false;
    }
    
    /***********************/
    private boolean esPalabraCategorizable(String palabra) {
         return (palabra.length() > 3) && 
                (!palabra.contains(")")) && 
                (!palabra.contains("(")) &&
                (!palabra.contains("<")) && 
                (!palabra.contains(">")) &&
                (!palabra.contains("handbox")) &&
                (!palabra.startsWith("-"));
                        }
    
    /*************************/
    
    
    /**
     * 
     * @param idPost
     */
    public void categorizarPostv1(int idPost) {
            
            try {
                
                //Como a d�a de hoy no me traigo los tags, me recorro el t�tulo (de momento) para a�adir tags
                String query = "SELECT  post_title,post_content FROM " + PREFIJOV1 + "posts where ID='" + idPost + "'";
                //Me traigo t�tulo y contenido del post para extraer posibles tags y categor�as
                //NOTA: Añadir las palabras del titulo (ya veremos como eliminamos las no influyentes) como tags
                ResultSet post = selectV1(query);
                if (post.next()) {
                    Resultado.getResultado().getMensajelog().addLinea(post.getString(1));
                    //Del texto, sacamos las palabras, las contamos, si estan 2 veces repetidas miramos si son categoria.
                    //Si tienen mas de 3 letras pero no se repite lo comparo con las etiquetas unicamente.
                    String[] palabras =
                        post.getString(2).replaceAll("\\<.*?>",
                                                     "").split("[[ ]*|[,]*|[\"]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                    HashMap words = new HashMap();
                    for (int i = 0; i < palabras.length; i++) {

                        if (esPalabraCategorizable(palabras[i]))
                        {
                            int repeticiones = 1;
                            if (words.get(palabras[i]) != null) {
                                repeticiones = Integer.parseInt((String) words.get(palabras[i]));
                                repeticiones++;
                            }
                            words.put(palabras[i], repeticiones + "");
                            
                        }
                    }
                    //En este punto tengo las palabras del tutorial con el numero de repeticiones.
                    
                    ArrayList<String> keys = new ArrayList<String>(words.keySet());
                    for (String key : keys) {
                        //Si es mayor que 1 y la palabra es mayor de 3 (¿no me lo puedo ahorrar esto ultimo?)
                        if ((Integer.parseInt((String) words.get(key)) > 1) && (((String) words.get(key)).length() > 3)) {
                            query =
                                "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                "term_taxonomy x " + "where x.taxonomy='category' and x.term_id = t.term_id " +
                                "and (t.name = '" + key + "' or t.slug='" + key + "')";

                            ResultSet tags = selectV1(query);
                            //compruebo si hay alguna categoria que coincida con la palabra.
                            
                            while (tags.next()) {
                                //Lo añado como categoria o como tag al post, lo que sea.
                                String insert =
                                    "INSERT INTO " + PREFIJOV1 +
                                    "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " + idPost +
                                    ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                insertV1(insert);
                                Resultado.getResultado().getMensajelog().addLinea(key + ": " + words.get(key) + " cate");
                                
                            }
                        } /* else {
                            //OJOOOOO!!!!
                            //Esto no me gusta, mejor si la palabra muy repetida, mas de 4 letras, se repite 2 veces, doy de alta un tag
                            if ((Integer.parseInt((String) words.get(key)) > 0) && (((String) words.get(key)).length() > 2)) {
                                query =
                                    "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                    "term_taxonomy x " + "where x.taxonomy='post_tag' and x.term_id = t.term_id " +
                                    "and (t.name = '" + key + "' or t.slug='" + key + "')";

                                try {
                                    ResultSet tags = selectV1(query);
                                    while (tags.next()) {
                                        //Lo a�ado como tag o categoria
                                        String insert =
                                            "INSERT INTO " + PREFIJOV1 +
                                            "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                            idPost + ", " + tags.getInt(1) +
                                            ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                        insertV1(insert);
                                        System.out.print(key + ": " + words.get(key));
                                        Resultado.getResultado().getMensajelog().addLinea(" tag");
                                    }
                                } catch (SQLException sqle) {
                                    // TODO: Add catch code
                                    
                                }
                            }
                        }*/
                    }
                    //Del titulo saco categorias o etiquetas
                    palabras = post.getString(1).split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                    for (int i = 0; i < palabras.length; i++) {

                        if (palabras[i].length() > 3) {
                            query =
                                "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                "term_taxonomy x " +
                                "where (x.taxonomy='category' or x.taxonomy='post_tag') and x.term_id = t.term_id " +
                                "and (t.name = '" + palabras[i] + "' or t.slug='" + palabras[i] + "')";

                            try {
                                ResultSet tags = selectV1(query);
                                while (tags.next()) {
                                    //Lo a�ado como tag o categoria
                                    String insert =
                                        "INSERT INTO " + PREFIJOV1 +
                                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                        idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                    insertV1(insert);
                                }
                                //OJO, AQUI METERIA LAS PALABRAS DE MAS DE 4 LETRAS QUE NO LO ESTEN YA COMO TAGS.
                                
                                
                            } catch (Exception sqle) {
                                // TODO: Add catch code

                            }
                        }

                    }


                }

                //Me traigo los tags para ver si alg�n tag es categor�a. OJO!!! LA CONSULTA DEBE SER MAS LAXA
                query =
                    "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV1 + "term_relationships r, " +
                    PREFIJOV1 + "term_taxonomy t, " + PREFIJOV1 + "terms w " + "where r.object_id =" + idPost + " " +
                    "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                    "and t.taxonomy='post_tag'";
                ResultSet terminos = selectV1(query);
                //Si existe categor�a con ese tag, le asigno categor�a.
                while (terminos.next()) {

                    query =
                        "select t.term_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 + "term_taxonomy x " +
                        "where x.taxonomy='category' and x.term_id = t.term_id " + "and (t.name = '" +
                        terminos.getString(2) + "' or t.slug='" + terminos.getString(2) + "')";

                    try {
                        ResultSet tags = selectV1(query);
                        while (tags.next()) {
                            String insert =
                                "INSERT INTO " + PREFIJOV1 +
                                "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                            insertV1(insert);
                        }
                    } catch (SQLException sqle) {
                        // TODO: Add catch code
                       
                    }

                }

                //Reviso por �ltima vez las categorias, si no tiene las categorias 100 ni 44, le pongo por defecto la 44
                //41  77  87  94,   le pongo por defecto la 87

                query =
                    "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV1 + "term_relationships r, " +
                    PREFIJOV1 + "term_taxonomy t, " + PREFIJOV1 + "terms w " + "where r.object_id =" + idPost + " " +
                    "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                    "and t.taxonomy='category'";

                ResultSet categoriasFinales = selectV1(query);

/* ESTO YA NO TIENE SENTIDO. ¿PUEDO APROVECHAR LA QUERY PARA ALGO?
                int tienedificultad = 0;

                while (categoriasFinales.next()) {
                    if (categoriasFinales.getInt(1) == 41 || categoriasFinales.getInt(1) == 77 ||
                        categoriasFinales.getInt(1) == 87 || categoriasFinales.getInt(1) == 94) {

                        tienedificultad++;
                    }
                }

                if (tienedificultad == 0) {
                    String idDificultad = "87";
                    if (dificultadFacil > 0)
                        idDificultad = "77";
                    try {
                    String insert =
                        "INSERT INTO " + PREFIJOV1 +
                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                        idDificultad + ", 0 )";
                    insertV1(insert);
                } catch (Exception e) {
                    // TODO: Add catch code
                   
                }
                }

                
                    
                    if (paraPeques > 0)
                    {
                             String  idPeques = "100";
                            try {
                            String insert =
                                "INSERT INTO " + PREFIJOV1 +
                                "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                                idPeques + ", 0 )";
                                insertV1(insert);
                             } catch (Exception e) {
                            // TODO: Add catch code
                             }
                    }
*/
                //Borro categor�as principales

                String delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 40";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 56";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 70";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 72";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 96";
                insertV1(delete);
            } catch (SQLException sqle) {
                // TODO: Add catch code
                
            } catch (NumberFormatException nfe) {
                // TODO: Add catch code
                
            }
        }

    public static void main(String[] args) {
        if (args.length>0) {
            Resultado.getResultado().getMensajelog().addLinea("Maximo de posts a categorizar " + args[0]);
            String maxPosts = args[0];
            Resultado.getResultado().getMensajelog().addLinea("Base de datos  a categorizar " + args[1]);
            String base = args[1];
            CategorizadorHandbox categorizadorHandbox = new CategorizadorHandbox(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        }else{
            Resultado.getResultado().getMensajelog().addLinea("Pon un limite de posts y el origen (1) o el destino (2), Cero (0) para categorizar edad en V2");
        }
        //CategorizadorHandbox categorizadorHandbox = new CategorizadorHandbox(100,2);
        
    }
    
    
    
    
    /*
     *     public void categorizarPostv1(int idPost) {
            
            try {
                int dificultadFacil = 0;
                int paraPeques = 0;
                //Como a d�a de hoy no me traigo los tags, me recorro el t�tulo (de momento) para a�adir tags
                String query = "SELECT  post_title,post_content FROM " + PREFIJOV1 + "posts where ID='" + idPost + "'";
                //Me traigo t�tulo y contenido del post para extraer posibles tags y categor�as
                ResultSet post = selectV1(query);
                if (post.next()) {
                    Resultado.getResultado().getMensajelog().addLinea(post.getString(1));
                    //Del texto, sacamos las palabras, las contamos, si estan n veces repetidas miramos si son categoria.
                    //Si tienen mas de 3 letras pero no se repite lo comparo con las etiquetas unicamente.
                    String[] palabras =
                        post.getString(2).replaceAll("\\<.*?>",
                                                     "").split("[[ ]*|[,]*|[\"]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                    HashMap words = new HashMap();
                    for (int i = 0; i < palabras.length; i++) {

                        if ((palabras[i].length() > 3) && (!palabras[i].contains(")")) && (!palabras[i].contains("(")) &&
                            (!palabras[i].contains("<")) && (!palabras[i].contains(">")) &&
                            (!palabras[i].contains("handbox")))

                        {
                            int repeticiones = 1;
                            if (words.get(palabras[i]) != null) {
                                repeticiones = Integer.parseInt((String) words.get(palabras[i]));
                                repeticiones++;
                            }
                            words.put(palabras[i], repeticiones + "");
                            if (palabras[i].matches("f[a|�]cil[es]") || palabras[i].matches("sencillo[s]") ||
                                palabras[i].matches("tirado")) {
                                dificultadFacil++;
                            }
                            if (palabras[i].matches("ni�[o|a][s]") || palabras[i].matches("peque[s]") ||
                                palabras[i].matches("nen[e|a][s]")) {
                                paraPeques++;
                            }
                        }
                    }
                    ArrayList<String> keys = new ArrayList<String>(words.keySet());
                    for (String key : keys) {

                        if ((Integer.parseInt((String) words.get(key)) > 1) && (((String) words.get(key)).length() > 3)) {
                            query =
                                "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                "term_taxonomy x " + "where x.taxonomy='category' and x.term_id = t.term_id " +
                                "and (t.name = '" + key + "' or t.slug='" + key + "')";

                            ResultSet tags = selectV1(query);
                            while (tags.next()) {
                                //Lo a�ado como tag o categoria
                                String insert =
                                    "INSERT INTO " + PREFIJOV1 +
                                    "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " + idPost +
                                    ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                insertV1(insert);
                                System.out.print(key + ": " + words.get(key));
                                Resultado.getResultado().getMensajelog().addLinea(" cate");
                            }
                        } else {
                            if ((Integer.parseInt((String) words.get(key)) > 0) &&
                                (((String) words.get(key)).length() > 2)) {
                                query =
                                    "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                    "term_taxonomy x " + "where x.taxonomy='post_tag' and x.term_id = t.term_id " +
                                    "and (t.name = '" + key + "' or t.slug='" + key + "')";

                                try {
                                    ResultSet tags = selectV1(query);
                                    while (tags.next()) {
                                        //Lo a�ado como tag o categoria
                                        String insert =
                                            "INSERT INTO " + PREFIJOV1 +
                                            "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                            idPost + ", " + tags.getInt(1) +
                                            ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                        insertV1(insert);
                                        System.out.print(key + ": " + words.get(key));
                                        Resultado.getResultado().getMensajelog().addLinea(" tag");
                                    }
                                } catch (SQLException sqle) {
                                    // TODO: Add catch code
                                    
                                }
                            }
                        }
                    }
                    //Del titulo saco categorias o etiquetas
                    palabras = post.getString(1).split("[[ ]*|[,]*|[\\.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+");
                    for (int i = 0; i < palabras.length; i++) {

                        if (palabras[i].length() > 3) {
                            query =
                                "select x.term_taxonomy_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 +
                                "term_taxonomy x " +
                                "where (x.taxonomy='category' or x.taxonomy='post_tag') and x.term_id = t.term_id " +
                                "and (t.name = '" + palabras[i] + "' or t.slug='" + palabras[i] + "')";

                            try {
                                ResultSet tags = selectV1(query);
                                while (tags.next()) {
                                    //Lo a�ado como tag o categoria
                                    String insert =
                                        "INSERT INTO " + PREFIJOV1 +
                                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                        idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                                    insertV1(insert);
                                }
                            } catch (Exception sqle) {
                                // TODO: Add catch code

                            }
                        }

                    }


                }

                //Me traigo los tags para ver si alg�n tag es categor�a.
                query =
                    "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV1 + "term_relationships r, " +
                    PREFIJOV1 + "term_taxonomy t, " + PREFIJOV1 + "terms w " + "where r.object_id =" + idPost + " " +
                    "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                    "and t.taxonomy='post_tag'";
                ResultSet terminos = selectV1(query);
                //Si existe categor�a con ese tag, le asigno categor�a.
                while (terminos.next()) {

                    query =
                        "select t.term_id from " + PREFIJOV1 + "terms t, " + PREFIJOV1 + "term_taxonomy x " +
                        "where x.taxonomy='category' and x.term_id = t.term_id " + "and (t.name = '" +
                        terminos.getString(2) + "' or t.slug='" + terminos.getString(2) + "')";

                    try {
                        ResultSet tags = selectV1(query);
                        while (tags.next()) {
                            String insert =
                                "INSERT INTO " + PREFIJOV1 +
                                "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES ( " +
                                idPost + ", " + tags.getInt(1) + ", 0 )"; //,$res->object_id, $i->term_id, 0 ) );
                            insertV1(insert);
                        }
                    } catch (SQLException sqle) {
                        // TODO: Add catch code
                       
                    }

                }

                //Reviso por �ltima vez las categorias, si no tiene las categorias 100 ni 44, le pongo por defecto la 44
                //41  77  87  94,   le pongo por defecto la 87

                query =
                    "select r.term_taxonomy_id id , w.name nombre from " + PREFIJOV1 + "term_relationships r, " +
                    PREFIJOV1 + "term_taxonomy t, " + PREFIJOV1 + "terms w " + "where r.object_id =" + idPost + " " +
                    "and r.term_taxonomy_id = t.term_taxonomy_id " + "and w.term_id = t.term_id " +
                    "and t.taxonomy='category'";

                ResultSet categoriasFinales = selectV1(query);


                int tienedificultad = 0;

                while (categoriasFinales.next()) {
                    if (categoriasFinales.getInt(1) == 41 || categoriasFinales.getInt(1) == 77 ||
                        categoriasFinales.getInt(1) == 87 || categoriasFinales.getInt(1) == 94) {

                        tienedificultad++;
                    }
                }

                if (tienedificultad == 0) {
                    String idDificultad = "87";
                    if (dificultadFacil > 0)
                        idDificultad = "77";
                    try {
                    String insert =
                        "INSERT INTO " + PREFIJOV1 +
                        "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                        idDificultad + ", 0 )";
                    insertV1(insert);
                } catch (Exception e) {
                    // TODO: Add catch code
                   
                }
                }

                
                    
                    if (paraPeques > 0)
                    {
                             String  idPeques = "100";
                            try {
                            String insert =
                                "INSERT INTO " + PREFIJOV1 +
                                "term_relationships ( object_id, term_taxonomy_id, term_order ) VALUES (" + idPost + ", " +
                                idPeques + ", 0 )";
                                insertV1(insert);
                             } catch (Exception e) {
                            // TODO: Add catch code
                             }
                    }
                //Borro categor�as principales

                String delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 40";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 56";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 70";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 72";
                insertV1(delete);
                delete =
                    "delete from " + PREFIJOV1 + "term_relationships where object_id = " + idPost +
                    " and term_taxonomy_id = 96";
                insertV1(delete);
            } catch (SQLException sqle) {
                // TODO: Add catch code
                
            } catch (NumberFormatException nfe) {
                // TODO: Add catch code
                
            }
        }
     */
}
