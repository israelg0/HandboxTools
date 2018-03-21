package es.handbox.tools.comandline;

import es.handbox.tools.pojo.Bloggers;
import es.handbox.tools.pojo.CategorizadorHandbox;
import es.handbox.tools.pojo.SincronizarPostsHandbox;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Hbxtools {
    public Hbxtools() {
        super();
    }

    public static void main(String[] args) {
        //args=new String[2];
        //args[0]="";
        //args[1]=""; //Sentido v2tobeta,feedstov2, beta, v2...
        //args[2]=""; //Numero de posts a tratar
        //args[3]="";//offset de categorizar todos.
        String opcion = args[0];
        
        if (opcion.equalsIgnoreCase("SincronizarUsuariosBeta")) {
            
            if (args.length<2)
                System.out.println("USO: SincronizarUsuariosBeta Sentido(v2toBeta)");
            else {
                Bloggers blogger = new Bloggers(args[1]);  //v2tobeta
                blogger.sincronizarUsuariosBeta(0);
            }
       }
        
        if (opcion.equalsIgnoreCase("SincronizarUsuarios")) {
            
            if (args.length<2)
                System.out.println("USO: SincronizarUsuarios Sentido(v2toFeeds)");
            else {
                Bloggers blogger = new Bloggers(args[1]);  //v2tofeeds
                blogger.sincronizarUsuariosHandbox();
            }
        }
        
        if (opcion.equalsIgnoreCase("SincronizarPosts")) {
            if (args.length<3)
                System.out.println("USO: SincronizarPosts Sentido(v2tobeta) numMaxPosts");
            else {
                SincronizarPostsHandbox posts = new SincronizarPostsHandbox(args[1]);
                posts.sincronizarPosts(args[2]);
            }
        }
        
        if (opcion.equalsIgnoreCase("SincronizarImagenes")) {
            if (args.length<2)
                System.out.println("USO: SincronizarImagenes Sentido(v2tobeta)");
            else {
                SincronizarPostsHandbox posts = new SincronizarPostsHandbox(args[1]);
                posts.sincronizarImagenes();
            }
        }
        
        if (opcion.equalsIgnoreCase("CategorizarPost")) {
            if (args.length<3)
                System.out.println("USO: CategorizarPost Entorno(v2|beta) idPost");
            else {
                int idPost = Integer.parseInt(args[2]);
                CategorizadorHandbox categorizador = new CategorizadorHandbox(args[1]);
                categorizador.categorizarPostv1(idPost);
            }
        }
        
        if (opcion.equalsIgnoreCase("CategorizarTodos")) {
            
            if (args.length<4)
                System.out.println("USO: CategorizarTodos Entorno(v2|beta) limite idInicial");
            else {
            int limite =  Integer.parseInt(args[2]);
            int idInicial =  Integer.parseInt(args[3]);
            CategorizadorHandbox categorizador = new CategorizadorHandbox(args[1]);
            categorizador.categorizarTodos(idInicial, limite);
            }
        }
        
        if (opcion.equalsIgnoreCase("CategorizarVideo")) {
                  if (args.length<2)
                      System.out.println("USO: CategorizarVideo Entorno(v2|beta)");  
                  else
                  {
                    CategorizadorHandbox cat = new CategorizadorHandbox("beta");
                    cat.establecercategoriadevideo();
                  }
              }
        if (opcion.equalsIgnoreCase("NumBloggers")) {
            if (args.length<2)
                System.out.println("USO: NumBloggers Sentido(feeds2v2)");  
                else
                {
            Bloggers bloggers = new Bloggers(args[1]);
            System.out.println(bloggers.getNumBloggers());
                }
        }   
        
        if (opcion.equalsIgnoreCase("Hola")) {
            Calendar calendario = new GregorianCalendar();
            int hora =calendario.get(Calendar.HOUR_OF_DAY);
            String saludo = "Buenos días";
            if ((hora>12)&&(hora<21))
                saludo = "Buenas tardes";
            if ((hora>20)&&(hora<6))
                saludo = "Buenas noches";
            System.out.println(saludo + ", todo preparado");
            System.out.println("Comandos:");
            System.out.println("SincronizarUsuariosBeta Sentido");
            System.out.println("SincronizarUsuarios Sentido");
            System.out.println("SincronizarPosts Sentido numMaxPosts");
            System.out.println("SincronizarImagenes Sentido");
            System.out.println("CategorizarPost Entorno(v2|beta) idPost");
            System.out.println("CategorizarTodos Entorno(v2|beta) limite idInicial");
            System.out.println("CategorizarVideo Entorno(v2|beta)");   
            System.out.println("NumBloggers");
        
        
        
        
        
        }  
        
    }
}
