package es.handbox.tools.comandline;

import es.handbox.tools.pojo.Bloggers;
import es.handbox.tools.pojo.CategorizadorHandbox;
import es.handbox.tools.pojo.SincronizarPostsHandbox;

public class Hbxtools {
    public Hbxtools() {
        super();
    }

    public static void main(String[] args) {
        args=new String[2];
        args[0]="CategorizarVideo";
        args[1]=""; //Sentido v2tobeta,feedstov2, beta, v2...
        args[2]=""; //Numero de posts a tratar
        args[3]="";//offset de categorizar todos.
        String opcion = args[0];
        
        if (opcion.equalsIgnoreCase("SincronizarUsuarios")) {
            Bloggers blogger = new Bloggers("v2tobeta");
            blogger.sincronizarUsuariosBeta(0);
        }
        
        if (opcion.equalsIgnoreCase("SincronizarPosts")) {
            SincronizarPostsHandbox posts = new SincronizarPostsHandbox("v2tobeta");
            posts.sincronizarPosts("20");
        }
        
        if (opcion.equalsIgnoreCase("CategorizarPost")) {
            int idPost = 334447;
            CategorizadorHandbox categorizador = new CategorizadorHandbox("beta");
            categorizador.categorizarPostv1(idPost);
        }
        
        if (opcion.equalsIgnoreCase("CategorizarTodos")) {
            int limite = 10;
            int idInicial = 334441;
            CategorizadorHandbox categorizador = new CategorizadorHandbox("beta");
            categorizador.categorizarTodos(idInicial, limite);
        }
        
        if (opcion.equalsIgnoreCase("CategorizarVideo")) {
                  CategorizadorHandbox cat = new CategorizadorHandbox("beta");
                
                  cat.establecercategoríadevideo();
              }
        
    }
}
