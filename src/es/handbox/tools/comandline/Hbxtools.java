package es.handbox.tools.comandline;

import es.handbox.tools.pojo.Bloggers;
import es.handbox.tools.pojo.SincronizarPostsHandbox;

public class Hbxtools {
    public Hbxtools() {
        super();
    }

    public static void main(String[] args) {
        args=new String[1];
        args[0]="SincronizarPosts";
        
        String opcion = args[0];
        
        if (opcion.equalsIgnoreCase("SincronizarUsuarios")) {
            Bloggers blogger = new Bloggers("v2tobeta");
            blogger.sincronizarUsuariosBeta(0);
        }
        
        if (opcion.equalsIgnoreCase("SincronizarPosts")) {
            SincronizarPostsHandbox posts = new SincronizarPostsHandbox("v2tobeta");
            posts.sincronizarPosts("10");
        }
        
        
        
    }
}
