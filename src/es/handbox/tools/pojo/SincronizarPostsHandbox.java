package es.handbox.tools.pojo;

import es.handbox.model.MensajeLog;

import es.handbox.model.Resultado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.ResultSet;

import java.util.Calendar;

public class SincronizarPostsHandbox extends HandboxConnections {
    String maxPosts;
    String sentido;

    public SincronizarPostsHandbox() {
        super("feeds2hbx");
    }
    
    public SincronizarPostsHandbox(String sentido_) {
        super(sentido_);
        sentido = sentido_;
    }

    public MensajeLog sincronizarPosts(String limite) {
        maxPosts = limite;
        Resultado res = Resultado.getResultado();
        
        try {
            
            res.getMensajelog().addLinea("Buscar el ultimo post comun");
            //1.- Ultimo post en V2
            String queryLastPostV2 =
                "select ID,post_author,post_title,post_name from " + PREFIJOV2 +
                "posts where id = (select max(id) from " + PREFIJOV2 +
                "posts where post_type=\"post\" and post_title!='Borrador autom�tico' and post_author!=1 and post_author!=2 and post_author!=5 and post_status='publish')";
            ResultSet rsLastPostv2 = selectV2(queryLastPostV2);
            
            if (rsLastPostv2.next()) {
                res.getMensajelog().addLinea("Comienza sincronizacion ");
                res.getMensajelog().addLinea("Sincronizando imagenes ");
                //Ejecuta el rsync, en pruebas
                sincronizarImagenes();
                
                //Muestro el post_title y el post_name
                res.getMensajelog().addLinea(rsLastPostv2.getString(3).replaceAll("\"", "\\\\\'").replaceAll("\'", "\\\\\'"));
                res.getMensajelog().addLinea(rsLastPostv2.getString(4).replaceAll("\"", "\\\\\'").replaceAll("\'", "\\\\\'"));
                
                //Obtengo el ID del post que coincida con el anterior, los posts a actualizar serán los posteriores a este.
                String queryLastPostV1 =
                    "Select ID from " + PREFIJOV1 + "posts where post_status='publish' and post_author='" +
                    rsLastPostv2.getString(2) + "' and post_name='" +
                    rsLastPostv2.getString(4).replaceAll("\"", "\\\\\'").replaceAll("\'", "\\\'") + "'";
                ResultSet rsLastPostv1 = selectV1(queryLastPostV1);
                
                if (rsLastPostv1.next()) {
                    res.getMensajelog().addLinea("Comienza sincronizacion de bbdd ");
                    //2.- Posts nuevos en V1
                    String queryNuevosPosts =
                        "select ID,post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,post_status,comment_status,ping_status,post_password,post_name,to_ping,pinged,post_modified,post_modified_gmt,post_content_filtered,post_parent,guid,menu_order,post_type,post_mime_type,comment_count from " +
                        PREFIJOV1 + "posts where post_type=\"post\" and ID>" + rsLastPostv1.getInt(1) +
                        " and post_status='publish' order by ID asc limit " + maxPosts;
                
                    res.getMensajelog().addLinea("Consultado nuevos posts");
                    ResultSet rsNuevosPostsv1 = selectV1(queryNuevosPosts);
                    int total = 0;
                    while (rsNuevosPostsv1.next()) {
                        total++;
                        int IDv1 = rsNuevosPostsv1.getInt(1);

                        boolean saltar = false;
                        ///Por si acaso ese post ya se dió de alta, compruebo que no exista.
                        String queryMiraSiExiste =
                            "select id from " + PREFIJOV2 + "posts where post_type=\"post\" and post_name='" +
                            rsNuevosPostsv1.getString(12).replace("'", "\"") + "' and post_author='" +
                            rsNuevosPostsv1.getInt(2) + "'";
                        ResultSet rsExistePostv2 = selectV2(queryMiraSiExiste);
                        if (rsExistePostv2.next()) {
                            saltar = true; //Lo marco para saltar
                            total--; //No lo cuento en el total de posts a sincronizar
                        }

                        //Aqui controlare si quiero o no que se copie el post. Segun titulo.
                        CategorizadorHandbox categorizadorHandbox = new CategorizadorHandbox(sentido);

                        //Si no esta repe el post y no nos lo saltamos.
                        if (!saltar)
                        {
                            saltar = categorizadorHandbox.contienePalabra(IDv1, HandboxConnections.palabrasProhibidas);
                            if (saltar)
                                res.getMensajelog().addLinea("No se evaluará por palabra prohibida. ");
                        }
                        if (!saltar) {
                            //Categorizo el post de la V1 (feeds original)
                            
                            categorizadorHandbox.categorizarPostv1(IDv1);
                            //categorizadorHandbox.categorizarVideo(IDv1);
                            res.getMensajelog().addLinea("Actualizando " + IDv1);
                            //1.- Inserto el nuevo post y obtengo el nuevo ID del post
                            String insertNuevoPost =
                                "INSERT INTO `" + PREFIJOV2 +
                                "posts` ( post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,post_status,comment_status,ping_status,post_password,post_name,to_ping,pinged,post_modified,post_modified_gmt,post_content_filtered,post_parent,guid,menu_order,post_type,post_mime_type,comment_count ) " +
                                " VALUES " + "(" + rsNuevosPostsv1.getInt(2) + ",'" +
                                rsNuevosPostsv1.getString(3).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(4).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(5).replace("'", "\"").replace("http://feeds.", "http://") +
                                "','" + rsNuevosPostsv1.getString(6).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(7).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(8).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(9).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(10).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(11).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(12).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(13).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(14).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(15).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(16).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(17).replace("'", "\"") + "'," + rsNuevosPostsv1.getInt(18) +
                                ",'" +
                                rsNuevosPostsv1.getString(19).replace("'", "\"").replace("http://feeds.", "http://") +
                                "'," + rsNuevosPostsv1.getInt(20) + ",'" +
                                rsNuevosPostsv1.getString(21).replace("'", "\"") + "','" +
                                rsNuevosPostsv1.getString(22).replace("'", "\"") + "'," + rsNuevosPostsv1.getInt(23) +
                                ")";
                            //Obtengo el nuevo Id
                            insertV2(insertNuevoPost);
                            rsLastPostv2 = selectV2(queryLastPostV2);
                            if (rsLastPostv2.next()) {
                                int IDv2 = rsLastPostv2.getInt(1);
                                //Categorizo el post de la V1 (original)
                                categorizadorHandbox = new CategorizadorHandbox(sentido);
                                categorizadorHandbox.categorizarPostv2(IDv2);
                                res.getMensajelog().addLinea("Nuevo ID para el post " + IDv2 + " -----------------");
                                //2.- Obtengo todos sus elementos cuyo post-parent=postID
                                String queryElementos =
                                    "select ID,post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,post_status,comment_status,ping_status,post_password,post_name,to_ping,pinged,post_modified,post_modified_gmt,post_content_filtered,post_parent,guid,menu_order,post_type,post_mime_type,comment_count from " +
                                    PREFIJOV1 + "posts where post_parent=" + IDv1 + " order by ID asc";
                                res.getMensajelog().addLinea("Actualizando elementos adicionales para el post");
                                ResultSet rsElementosv1 = selectV1(queryElementos);
                                //3.- Inserto los nuevos elementos (OJO, inserto el elemento en wp_posts y tambien su propio meta en wp_postmeta
                                while (rsElementosv1.next()) {
                                    res.getPost().add(rsElementosv1.getString(6).replace("'", "\""));
                                    //Inserto el elemento en la tabla de posts
                                    String insertNuevoElemento =
                                        "INSERT INTO `" + PREFIJOV2 +
                                        "posts` ( post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,post_status,comment_status,ping_status,post_password,post_name,to_ping,pinged,post_modified,post_modified_gmt,post_content_filtered,post_parent,guid,menu_order,post_type,post_mime_type,comment_count ) " +
                                        " VALUES " + "(" + rsElementosv1.getInt(2) + ",'" +
                                        rsElementosv1.getString(3).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(4).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(5).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(6).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(7).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(8).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(9).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(10).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(11).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(12).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(13).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(14).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(15).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(16).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(17).replace("'", "\"") + "'," + IDv2 + ",'" +
                                        rsElementosv1.getString(19).replace("'", "\"") + "'," +
                                        rsElementosv1.getInt(20) + ",'" +
                                        rsElementosv1.getString(21).replace("'", "\"") + "','" +
                                        rsElementosv1.getString(22).replace("'", "\"") + "'," +
                                        rsElementosv1.getInt(23) + ")";
                                    insertV2(insertNuevoElemento);
                                    //obtengo el id del elemento nuevo
                                    String queryLastElementV2 =
                                        "select max(id) from " + PREFIJOV2 +
                                        "posts where post_type=\"attachment\" and post_parent=" + IDv2;
                                    ResultSet rsLastElementv2 = selectV2(queryLastElementV2);
                                    if (rsLastElementv2.next()) {
                                        int IDElementov2 = rsLastElementv2.getInt(1);

                                        //Obtengo los meta especificos del elemento
                                        String queryMetasImg =
                                            "Select meta_id,post_id,meta_key,meta_value from " + PREFIJOV1 +
                                            "postmeta where post_id=" + rsElementosv1.getInt(1);
                                        ResultSet rsMetasImgv1 = selectV1(queryMetasImg);
                                        while (rsMetasImgv1.next()) {
                                            String insertMetas =
                                                "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                                "values " + "(" + IDElementov2 + ",'" +
                                                rsMetasImgv1.getString(3).replace("'", "\"") + "','" +
                                                rsMetasImgv1.getString(4).replace("'", "\"") + "')";
                                            insertV2(insertMetas);
                                        }
                                        rsMetasImgv1.close();
                                    }
                                }
                                //4.- Obtengo todos los metas.
                                String queryMetas =
                                    "Select meta_id,post_id,meta_key,meta_value from " + PREFIJOV1 +
                                    "postmeta where post_id=" + IDv1 + " and meta_key not like '%thumbnail%'";
                                res.getMensajelog().addLinea("Actualizando Metas para el post");
                                ResultSet rsMetasv1 = selectV1(queryMetas);
                                while (rsMetasv1.next()) {
                                    String insertMetas =
                                        "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                        "values " + "(" + IDv2 + ",'" + rsMetasv1.getString(3).replace("'", "\"") +
                                        "','" + rsMetasv1.getString(4).replace("'", "\"") + "')";
                                    insertV2(insertMetas);
                                }
                                rsMetasv1.close();
                                //5.- Obtengo todos los comentarios.
                                res.getMensajelog().addLinea("Actualizando Comentarios para el post");
                                String queryComments =
                                    "select comment_ID, comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id from " +
                                    PREFIJOV1 + "comments where comment_post_ID=" + IDv1;
                                ResultSet rsCommentsv1 = selectV1(queryComments);
                                while (rsCommentsv1.next()) {
                                    String insertComments =
                                        "INSERT INTO " + PREFIJOV2 +
                                        "comments (comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id)" +
                                        "values " + "(" + IDv2 + ",'" + rsCommentsv1.getString(3) + "','" +
                                        rsCommentsv1.getString(4).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(5).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(6).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(7).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(8).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(9).replace("'", "\"") + "'," + rsCommentsv1.getInt(10) +
                                        ",'" + rsCommentsv1.getString(11).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(12).replace("'", "\"") + "','" +
                                        rsCommentsv1.getString(13).replace("'", "\"") + "'," + rsCommentsv1.getInt(14) +
                                        "," + rsCommentsv1.getInt(15) + ")";
                                    insertV2(insertComments);

                                    //Obtengo el id del comentatio recien insertado
                                    String ultimoComentario = "select max(comment_ID) from " + PREFIJOV2 + "comments";
                                    ResultSet rsUltimoComentv2 = selectV2(ultimoComentario);
                                    if (rsUltimoComentv2.next()) {
                                        String queryCommentMetas =
                                            "Select meta_id,comment_id,meta_key,meta_value from " + PREFIJOV1 +
                                            "commentmeta where comment_id=" + rsCommentsv1.getInt(1);
                                        ResultSet rsCommentMetasv1 = selectV1(queryCommentMetas);
                                        while (rsCommentMetasv1.next()) {
                                            String insertCommentMetas =
                                                "Insert into " + PREFIJOV2 +
                                                "commentmeta(comment_id,meta_key,meta_value) " + "values " + "(" +
                                                rsUltimoComentv2.getInt(1) + ",'" +
                                                rsCommentMetasv1.getString(3).replace("'", "\"") + "','" +
                                                rsCommentMetasv1.getString(4).replace("'", "\"") + "')";
                                            insertV2(insertCommentMetas);
                                        }
                                        rsCommentMetasv1.close();
                                    }
                                    rsUltimoComentv2.close();


                                }
                                rsCommentsv1.close();


                                //6.- Establezco el Thumbnail
                                res.getMensajelog().addLinea("Actualizando Imagen de Portada");
                                String queryThumbnail =
                                    "select post_name from " + PREFIJOV1 + "posts where ID in (select meta_value from "+ PREFIJOV1 + "postmeta where post_id=" +
                                    IDv1 + " and meta_key='_thumbnail_id')";

                                ResultSet rsThumbnailv1 = selectV1(queryThumbnail);
                                while (rsThumbnailv1.next()) {
                                    String selectThmbnailv2 =
                                        "select ID from " + PREFIJOV2 + "posts where post_name='" +
                                        rsThumbnailv1.getString(1) + "' and post_parent=" + IDv2;
                                    ResultSet rsThumbnailv2 = selectV2(selectThmbnailv2);
                                    if (rsThumbnailv2.next()) {
                                        //Inserto el thumbnail apt_pro_thumbnail
                                        String insertMetas =
                                            "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                            "values " + "(" + IDv2 + ",'_thumbnail_id','" + rsThumbnailv2.getString(1) +
                                            "')";
                                        insertV2(insertMetas);

                                        insertMetas =
                                            "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                            "values " + "(" + IDv2 + ",'apt_pro_thumbnail','" +
                                            rsThumbnailv2.getString(1) + "')";
                                        insertV2(insertMetas);
                                    } else {
                                        //Inserto el de por defecto
                                        String insertMetas =
                                            "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                            "values " + "(" + IDv2 + ",'_thumbnail_id','239962')";
                                        insertV2(insertMetas);
                                        insertMetas =
                                            "Insert into " + PREFIJOV2 + "postmeta(post_id,meta_key,meta_value) " +
                                            "values " + "(" + IDv2 + ",'_thumbnail_id','239962')";
                                        insertV2(insertMetas);
                                    }


                                    rsThumbnailv2.close();
                                }
                                rsThumbnailv1.close();


                            }


                            //A�ado las categorias y las etiquetas
                            String queryV1terms =
                                "Select term_taxonomy_id from " + PREFIJOV1 + "term_relationships where object_id = " +
                                rsNuevosPostsv1.getInt(1);
                            ResultSet v1terms = selectV1(queryV1terms);
                            String queryV2Post =
                                "Select ID from " + PREFIJOV2 + "posts where post_type='post' and post_title='" +
                                rsNuevosPostsv1.getString(6).replaceAll("\"", "\\\\\'").replaceAll("\'", "\\\'") + "'";
                            ResultSet v2Post = selectV2(queryV2Post);
                            if (v2Post.next()) {
                                res.getMensajelog().addLinea("Actualizando Terms para el post");
                                //OJO QUE YA SE CATEGORIZA, COMPROBAR QUE NO ESTA REPE. ISRA.
                                while (v1terms.next()) {
                                    String insertV2Terms =
                                        "insert into " + PREFIJOV2 +
                                        "term_relationships(object_id,term_taxonomy_id,term_order)" + "VALUES " + "(" +
                                        v2Post.getInt(1) + ", " + v1terms.getInt(1) + ", 0)";
                                    insertV2(insertV2Terms);
                                }
                            }
                            v1terms.close();
                            v2Post.close();
                            String updateURLS =
                                "UPDATE " + PREFIJOV2 + "posts\n" +
                                "SET guid = REPLACE(guid, 'http://feeds.handbox', 'http://handbox')\n" +
                                "WHERE guid LIKE ('http://feeds.handbox.es%')";
                            insertV2(updateURLS);
                        }
                        Thread.sleep(3000);
                    }
                    res.getMensajelog().addLinea(total + " actualizados");
                }
                rsLastPostv1.close();
            }
            rsLastPostv2.close();
            connectionv1.close();
            connectionv2.close();
        } catch (Exception e) {
            // TODO: Add catch code
            e.printStackTrace();
        }
        return res.getMensajelog();
    }


    /**
     * Ejecuta el RSYNC, todavia no está fino, por eso aún no se utiliza.
     */
    public void sincronizarImagenes() {
            Resultado res = Resultado.getResultado();
            if ((RSYNCv2!=null)&&(!RSYNCv2.equals("")))
            {
                try {
                Calendar cal = Calendar.getInstance();    
                int year = cal.get(Calendar.YEAR); 
                    String lhostv2 = HOSTv2;
                if ((UPLOADSHOSTv2!=null)&&(!UPLOADSHOSTv2.equals(""))) {
                    lhostv2 = UPLOADSHOSTv2;
                }
                ProcessBuilder probuilder = new ProcessBuilder("/bin/sh", "-c", "rsync -azp "+UPLOADSDIRv1+year+"/* "+RSYNCv2+"@"+lhostv2+":"+UPLOADSDIRv2+year);
                res.getMensajelog().addLinea("Sincronizando imagenes ");
                res.getMensajelog().addLinea("rsync -azp "+UPLOADSDIRv1+year+"/* "+RSYNCv2+"@"+lhostv2+":"+UPLOADSDIRv2+year);
                Process process = probuilder.start();
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    // TODO: Add catch code
                    ie.printStackTrace();
                }
                } catch (IOException ioe) {
                    // TODO: Add catch code
                    ioe.printStackTrace();
                }
            }
        
    }
}
