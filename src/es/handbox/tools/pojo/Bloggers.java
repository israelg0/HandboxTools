package es.handbox.tools.pojo;

import com.google.gson.Gson;

import es.handbox.model.MensajeLog;
import es.handbox.model.Resultado;
import es.handbox.model.User;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;
import java.util.Map;

import org.lorecraft.phparser.SerializedPhpParser;

public class Bloggers extends HandboxConnections {
    public static String GETALLBLOGGERS = "Select u.ID, u.display_name,u.user_url,u.user_nicename from handbox_users u, handbox_usermeta m where u.user_url!='' and u.ID=m.`user_id` and m.`meta_key`='handbox_capabilities' and m.`meta_value` like '%contributor%' and u.display_name!='israperfil' order by id desc";
    public static String GETFEEDSFROMAUTOBLOG = "select feed_meta from feed_autoblog order by feed_id desc";
    public Bloggers() {
        super("hbx2feeds");
    }
    
    public Bloggers(String sentido) {
        super(sentido);
    }
    public ArrayList<User> bloggersSinFeed() {
        
       Resultado res = Resultado.getResultado();
        //1.- Obtener todos los bloggers.
        String bloggers = GETALLBLOGGERS;            
        try{ 
            
        ResultSet  rsq1 = selectV2(bloggers);
        while (rsq1.next()) {
            res.getMensajelog().addLinea(rsq1.getString(1) + " - " + rsq1.getString(2) + " - " + rsq1.getString(3));
            
            String blogs = GETFEEDSFROMAUTOBLOG;
            ResultSet rsq0 = selectV1(blogs);
                int i = 0;
            while (rsq0.next()) {
               
                try {
                    SerializedPhpParser serializedPhpParser = new SerializedPhpParser(rsq0.getString(1));
                    Map results = (Map) serializedPhpParser.parse();
                    if (rsq1.getString(1).equals(results.get("author"))) {
                        i++;
                    }
                } catch (SQLException sqle) {
                    // TODO: Add catch code
                    //sqle.printStackTrace();
                }
             }
              if (i == 0) {
                    User bloggerSinFeed = new User();
                    bloggerSinFeed.setId(rsq1.getString(1));
                    bloggerSinFeed.setDisplayName(rsq1.getString(2));
                    bloggerSinFeed.setUserUrl(rsq1.getString(3));
                    bloggerSinFeed.setUserNicename(rsq1.getString(4));
                    
                    if (tieneFeed(rsq1.getString(3),"/feed/atom/"))
                        bloggerSinFeed.setFeedUrl(rsq1.getString(3)+"/feed/atom/");
                    else
                        if (tieneFeed(rsq1.getString(3),"/feeds/posts/default"))
                            bloggerSinFeed.setFeedUrl(rsq1.getString(3)+"/feeds/posts/default");
                        else
                            System.out.println(" ");
                    
                    res.getUsuarios().add(bloggerSinFeed);
                }
                else{
                    existeFeed("rsq1.getString(3)");
                }
                    
            rsq0.close();
        }
        rsq1.close();
        connectionv1.close();
        connectionv2.close();
        } catch (SQLException sqle) {
        // TODO: Add catch code
        //sqle.printStackTrace();
        }

        return res.getUsuarios();
    }
    
    protected boolean tieneFeed(String url_, String feed_) {
        try {
                 URL url = new URL(url_+feed_);
                 URLConnection conexion = url.openConnection();
                 conexion.connect();
                 // Lectura
                 InputStream is = conexion.getInputStream();
                 //Aqui sigue si hay feed.
                 return true;
                 
              } catch (MalformedURLException e) {
                 // TODO Auto-generated catch block
                
              } catch (IOException e) {
                 // TODO Auto-generated catch block
                 
              }
        return false;
    }
    
    protected void existeFeed(String url_) {

        try {
            URL url = new URL(url_);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();
            
            if (conexion.getResponseCode()!=200) {
                Resultado.getResultado().getMensajelog().addLinea("NO " + conexion.getResponseCode());
            }else{
                Resultado.getResultado().getMensajelog().addLinea("SI");
            }
            
        } catch (MalformedURLException murle) {
            // TODO: Add catch code
            //murle.printStackTrace();
        } catch (IOException ioe) {
            // TODO: Add catch code
            //ioe.printStackTrace();
        }catch (Exception e) {
            // TODO: Add catch code
            //ioe.printStackTrace();
           Resultado.getResultado().getMensajelog().addLinea("??");
        }
        
        
    }
    
    
    public ArrayList<User> sincronizarUsuariosHandbox() {
        Resultado res = Resultado.getResultado();
        
        try {
          
           //Para obtener los usuarios nuevos. Primero obtengo al mayor id
            ResultSet rsq1 = selectV1("select max(id) from "+PREFIJOV1+"users order by id asc");
            ResultSet rsq2 = selectV2("select max(id) from "+PREFIJOV2+"users order by id asc");

            if (rsq1.next() && rsq2.next()) {
                //SI HAY USUARIOS NUEVOS ACTUALIZAMOS
                res.getMensajelog().addLinea("Usuarios en " +PREFIJOV1+ " " + rsq1.getInt(1) + " / Usuarios en " +PREFIJOV2+ " " + rsq2.getInt(1)+ "");
               
                if (rsq1.getInt(1) > rsq2.getInt(1)) {
                    res.getMensajelog().addLinea("Actualizando usuarios");
                   
                    for (int i = rsq2.getInt(1) + 1; i <= rsq1.getInt(1);
                         i++) {
                        //PARA CADA USUARIO NUEVO
                        User usuario = new User();
                        
                        ResultSet rs =
                                       selectV1("SELECT * FROM "+PREFIJOV1+"users WHERE id=" +
                                                i + " order by id asc");

                        if (rs.next()) {
                            //DAMOS DE ALTA AL MISMO EN LA V2
                            usuario.setId(rs.getString(1));
                            usuario.setDisplayName(rs.getString(10));
                            usuario.setUserNicename(rs.getString(4));
           
                            res.getMensajelog().addLinea(("Usuario " + rs.getString(4)));
                            String newUser =
                                "INSERT INTO `"+PREFIJOV2+"users` ( `ID`, `user_login`,`user_pass`,`user_nicename`,`user_email`,`user_url`,`user_registered`,`user_activation_key`,`user_status`,`display_name` ) " +
                                "VALUES " + "(" + rs.getInt(1) + ",'" + rs.getString(4) + "','" + rs.getString(3) +
                                "','" + rs.getString(4) + "','" + rs.getString(5) + "','" + rs.getString(6) + "','" +
                                rs.getString(7) + "','" + rs.getString(8) + "'," + rs.getInt(9) + ",'" +
                                rs.getString(10) + "')";

                            insertV2(newUser);

                            //AÑADIMOS LOS METADATOS
                            String[] campos = getMetadata();
    res.getMensajelog().addLinea("Añadiendo Metadata " + campos.length + " campos");
                            for (int c = 0; c < campos.length; c++) {
                                ResultSet rs2 =
                                    selectV1("SELECT * FROM "+PREFIJOV1+"usermeta` WHERE user_id=" + i +
                                             " and meta_key='" + campos[c] + "' order by user_id asc");
                                String metadatos = "";
                                
                                if (rs2.next()) {
                                    metadatos =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs2.getInt(2) + ", '" + rs2.getString(3) + "', '" +
                                        rs2.getString(4) + "');";

                                } else {
                                    metadatos =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) + ", '" + campos[c] + "', '');";
                                }
                                insertV2(metadatos);
                                rs2.close();
                                System.out.print(".");
                                res.getMensajelog().lastLinea(c+"");
                            }
    res.getMensajelog().addLinea("OK");

                            /*SUSCRIPTOR O CONTRIBUTOR*/
                            ResultSet rs3 =
                                selectV1("SELECT * FROM "+PREFIJOV1+"usermeta` WHERE user_id=" + i +
                                         " and meta_key='"+PREFIJOV1+"capabilities' order by user_id asc");
                            
                            
                            
                            String setAs = "";
                            String role = "";
                            if (rs3.next() && rs3.getString(4).equals("a:1:{s:8:\"customer\";b:1;}")) {
                                setAs =
                                    "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                    "VALUES " + "(" + rs.getInt(1) +
                                    ", '"+PREFIJOV2+"capabilities', 'a:1:{s:8:\"customer\";b:1;}');";
                                role =
                                    "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                    "VALUES " + "(" + rs.getInt(1) + ", 'role', 'customer');";

                                usuario.setTipoUsuario("customer");
                            } else {
                                if (!rs.getString(6).equals("")) {
                                    setAs =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) +
                                        ", '"+PREFIJOV2+"capabilities', 'a:1:{s:11:\"contributor\";b:1;}');";
                                    role =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) + ", 'role', 'contributor');";

                                    usuario.setTipoUsuario("contributor");
                                } else {
                                    setAs =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) +
                                        ", '"+PREFIJOV2+"capabilities', 'a:1:{s:10:\"subscriber\";b:1;}');";
                                    role =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) + ", 'role', 'subscriber');";
                                    usuario.setTipoUsuario("subscriber");
                                }
                            }
                            insertV2(setAs);
                            insertV2(role);
                            rs3.close();
                            //HIDES
                            insertHides(rs.getInt(1));
                        }
                       res.getUsuarios().add(usuario);
                    }
                }
            }
            connectionv1.close();
            connectionv2.close();
            
            
            
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        }

        return res.getUsuarios();
    }
    
    public ArrayList<User> sincronizarUsuariosBeta(int numUsuarios) {
        Resultado res = Resultado.getResultado();
        
        try {
            
           //Para obtener los usuarios nuevos. Primero obtengo al mayor id
            ResultSet rsq1 = selectV1("select max(id) from "+PREFIJOV1+"users order by id asc"); //V1
            ResultSet rsq2 = selectV2("select max(id) from "+PREFIJOV2+"users order by id asc"); //BETA

            if (rsq1.next() && rsq2.next()) {
                //SI HAY USUARIOS NUEVOS ACTUALIZAMOS
                res.getMensajelog().addLinea("Usuarios en " +PREFIJOV1+ " " + rsq1.getInt(1) + " / Usuarios en " +PREFIJOV2+ " " + rsq2.getInt(1)+ "");
               
                if (rsq1.getInt(1) > rsq2.getInt(1)) {
                    res.getMensajelog().addLinea("Actualizando usuarios");
                    int total = (rsq1.getInt(1)-rsq2.getInt(1));
                    if ((numUsuarios>0)&&(numUsuarios<total)) 
                        total = rsq2.getInt(1)+numUsuarios;
                    for (int i = rsq2.getInt(1) + 1; i <= rsq1.getInt(1);
                         i++) {
                        //PARA CADA USUARIO NUEVO
                        User usuario = new User();
                        
                        ResultSet rs =
                                       selectV1("SELECT * FROM "+PREFIJOV1+"users WHERE id=" +
                                                i + " order by id asc");

                        if (rs.next()) {
                            //DAMOS DE ALTA AL MISMO EN BETA
                            usuario.setId(rs.getString(1));
                            usuario.setDisplayName(rs.getString(10));
                            usuario.setUserNicename(rs.getString(4));
           
                            res.getMensajelog().addLinea(("Usuario " + rs.getString(4)));
                            String newUser =
                                "INSERT INTO `"+PREFIJOV2+"users` ( `ID`, `user_login`,`user_pass`,`user_nicename`,`user_email`,`user_url`,`user_registered`,`user_activation_key`,`user_status`,`display_name` ) " +
                                "VALUES " + "(" + rs.getInt(1) + ",'" + rs.getString(4) + "','" + rs.getString(3) +
                                "','" + rs.getString(4) + "','" + rs.getString(5) + "','" + rs.getString(6) + "','" +
                                rs.getString(7) + "','" + rs.getString(8) + "'," + rs.getInt(9) + ",'" +
                                rs.getString(10) + "')";

                            insertV2(newUser);

                            //AÑADIMOS LOS METADATOS
                            String[] campos = getMetadata();
                            res.getMensajelog().addLinea("Añadiendo Metadata " + campos.length + " campos");
                            for (int c = 0; c < campos.length; c++) {
                                ResultSet rs2 =
                                    selectV1("SELECT * FROM "+PREFIJOV1+"usermeta` WHERE user_id=" + i +
                                             " and meta_key='" + campos[c] + "' order by user_id asc");
                                String metadatos = "";
                                
                                if (rs2.next()) {
                                    metadatos =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs2.getInt(2) + ", '" + rs2.getString(3) + "', '" +
                                        rs2.getString(4) + "');";

                                } else {
                                    metadatos =
                                        "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                        "VALUES " + "(" + rs.getInt(1) + ", '" + campos[c] + "', '');";
                                }
                                insertV2(metadatos);
                                rs2.close();
                                System.out.print(".");
                                res.getMensajelog().lastLinea(c+"");
                            }
                            res.getMensajelog().addLinea("OK");

                            /*SUSCRIPTOR/CONTRIBUTOR/CUSTOMER*/
                            ResultSet rs3 =
                                selectV1("SELECT * FROM "+PREFIJOV1+"usermeta` WHERE user_id=" + i +
                                         " and meta_key='"+PREFIJOV1+"capabilities' order by user_id asc");
                            
                            
                            
                            String setAs = "";
                            String role = "";
                            if (rs3.next() && rs3.getString(4).equals("a:1:{s:13:\"administrator\";b:1;}") ||
                                rs3.next() && rs3.getString(4).equals("a:2:{s:13:\"administrator\";b:1;s:19:\"adpress_client_menu\";b:1;}")) {
                                
                                setAs =
                                    "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                    "VALUES " + "(" + rs.getInt(1) +
                                    ", '"+PREFIJOV2+"capabilities', 'aa:1:{s:13:\"administrator\";b:1;}');";
                                role =
                                    "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                    "VALUES " + "(" + rs.getInt(1) + ", 'role', 'administrator');";
                                
                                usuario.setTipoUsuario("administrator");
                                
                            }
                            else{
                                
                            
                                    if (rs3.next() && rs3.getString(4).equals("a:1:{s:8:\"customer\";b:1;}")) {
                                        setAs =
                                            "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                            "VALUES " + "(" + rs.getInt(1) +
                                            ", '"+PREFIJOV2+"capabilities', 'a:1:{s:8:\"customer\";b:1;}');";
                                        role =
                                            "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                            "VALUES " + "(" + rs.getInt(1) + ", 'role', 'customer');";
        
                                        usuario.setTipoUsuario("customer");
                                    } else {
                                        if (!rs.getString(6).equals("")) {
                                            setAs =
                                                "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                                "VALUES " + "(" + rs.getInt(1) +
                                                ", '"+PREFIJOV2+"capabilities', 'a:1:{s:11:\"contributor\";b:1;}');";
                                            role =
                                                "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                                "VALUES " + "(" + rs.getInt(1) + ", 'role', 'contributor');";
        
                                            usuario.setTipoUsuario("contributor");
                                        } else {
                                            
                                            
                                            setAs =
                                                "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                                "VALUES " + "(" + rs.getInt(1) +", '"+PREFIJOV2+"capabilities', 'a:1:{s:10:\"subscriber\";b:1;}');";
                                            role =
                                                "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " +
                                                "VALUES " + "(" + rs.getInt(1) + ", 'role', 'subscriber');";
                                            usuario.setTipoUsuario("subscriber");
                                        }
                                    }
                            }
                            insertV2(setAs);
                            insertV2(role);
                            rs3.close();
                            //HIDES

                        }
                       res.getUsuarios().add(usuario);
                    }
                }
            }
            connectionv1.close();
            connectionv2.close();
            
            
            
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        }

        return res.getUsuarios();
    }

    private String[] getMetadata() {
        String[] res = {
            "action", "admin_color", "aim", "comment_shortcuts", "country", "description", "custom_profile_bg",
            "description", "dismissed_"+PREFIJOV1+"pointers", "display_name", "facebook", "first_name", "google_plus",
            "form_role", "group", "jabber", "last_name", "nickname", "Pinterest", "profilepicture", "redirect_uri",
            "rch_editing", "shortcode", "show_admin_bar_front", "template", "terms", "unique_id", "up_username",
            "user_email", "user_login", "user_url", "use_ssl", ""+PREFIJOV1+"user_level", "yim", ""+PREFIJOV1+"user_level", "yim",
            "_account_status", "_myuserpro_nonce", "_"+PREFIJOV1+"http_referer"
        };
        return res;
    }
    
    private String[] getMetadataBeta() {
        String[] res = {
            "admin_color", 
            "comment_shortcuts", 
            "country", 
            "description", 
            "custom_profile_bg",
            "display_name", 
            "facebook", 
            "first_name", 
            "google_plus",
            "group", 
            "last_name", 
            "nickname", 
            "Pinterest", 
            "profilepicture",  
            "show_admin_bar_front", 
            "user_email", 
            "user_login", 
            "user_url"
        };
        return res;
    }

    

    private void insertHides(int idUsuario) {
        String insertCampos =
            "INSERT INTO `"+PREFIJOV2+"usermeta` ( `user_id`, `meta_key`, `meta_value`) " + "VALUES " + "(" + idUsuario +
            ", 'hide_action', '0'), " + "(" + idUsuario + ", 'hide_country', '0'), " + "(" + idUsuario +
            ", 'hide_custom_profile_bg', '0'), " + "(" + idUsuario + ", 'hide_display_name', '0'), " + "(" + idUsuario +
            ", 'hide_facebook', '0'), " + "(" + idUsuario + ", 'hide_gender', '0'), " + "(" + idUsuario +
            ", 'hide_google_plus', '0'), " + "(" + idUsuario + ", 'hide_group', '0'), " + "(" + idUsuario +
            ", 'hide_instagram', '0'), " + "(" + idUsuario + ", 'hide_Pinterest', '0'), " + "(" + idUsuario +
            ", 'hide_profilepicture', '0'), " + "(" + idUsuario + ", 'hide_redirect_uri', '0'), " + "(" + idUsuario +
            ", 'hide_role', '0'), " + "(" + idUsuario + ", 'hide_shortcode', '0'), " + "(" + idUsuario +
            ", 'hide_template', '0'), " + "(" + idUsuario + ", 'hide_terms', '0'), " + "(" + idUsuario +
            ", 'hide_twitter', '0'), " + "(" + idUsuario + ", 'hide_unique_id', '0'), " + "(" + idUsuario +
            ", 'hide_up_username', '0'), " + "(" + idUsuario + ", 'hide_user_email', '0'), " + "(" + idUsuario +
            ", 'hide_user_login', '0'), " + "(" + idUsuario + ", 'hide_user_pass', '0'), " + "(" + idUsuario +
            ", 'hide_user_pass_confirm', '0'), " + "(" + idUsuario + ", 'hide_user_url', '0'), " + "(" + idUsuario +
            ", 'hide_myuserpro_nonce', '0'), " + "(" + idUsuario + ", 'hide_"+PREFIJOV1+"http_referer', '0'); ";
        try {
            Statement sv2 = connectionv2.createStatement();
            sv2.execute(insertCampos);
            Resultado.getResultado().getMensajelog().addLinea("Hide's OK " + idUsuario);
        } catch (SQLException sqle) {
            // TODO: Add catch code
            Resultado.getResultado().getMensajelog().addLinea("Error con hide " + idUsuario);
        }
    }
    
    
    public String getNumBloggers() {
        String res = "";
        try {
            ResultSet rsq1 = selectV1("select max(id) from "+PREFIJOV1+"users");
            ResultSet rsq2 = selectV2("select max(id) from "+PREFIJOV2+"users");

            if (rsq1.next() && rsq2.next()) {
                //SI HAY USUARIOS NUEVOS ACTUALIZAMOS
                
                res += "\"Usuarios "+HOSTv1+" \":\""+rsq1.getInt(1)+"\",\n";
                
                res += "\"Usuarios "+HOSTv2+" \":\""+rsq2.getInt(1)+"\"\n";
                

            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        }
        return res;
    }
    
    
    public ArrayList<User> testDeHilos() {
        Resultado res = Resultado.getResultado();
        res.getMensajelog().addLinea("Antes del bucle");
        for (int i = 0; i < 5; i++) {
            try {
                res.setOperacionEjecutada("Test");
                res.getMensajelog().addLinea("Iteración " + 1);
                Thread.sleep(2000);
                res.getMensajelog().addLinea("Usuario " + i);
                User user = new User();
                user.setDisplayName("Usuario " + i);
                user.setFeedUrl("http://la_url_que_mola/" + i + "/");
                user.setId(i + "");
                user.setTipoUsuario("tipo " + i);
                user.setUserNicename("userNicename" + i);
                user.setUserUrl("userUrl" + i);
                Thread.sleep(1000);
                res.getUsuarios().add(user);
                res.getMensajelog().addLinea("Usuario " + i + " Añadido");
                Thread.sleep(5000);
                
            } catch (InterruptedException ie) {
                // TODO: Add catch code
                ie.printStackTrace();
            }
            
            
        }
        
        return res.getUsuarios();
        
    }
}
