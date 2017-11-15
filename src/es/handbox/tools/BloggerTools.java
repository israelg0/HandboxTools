package es.handbox.tools;

import com.google.gson.Gson;

import es.handbox.model.Respuesta;
import es.handbox.model.Resultado;
import es.handbox.tools.callable.BloggersCallable;
import es.handbox.tools.pojo.Bloggers;
import es.handbox.tools.pojo.HandboxConnections;

import es.handbox.tools.pojo.SincronizarPostsHandbox;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.Future;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("blogger")
public class BloggerTools{
    public BloggerTools() {
        super();
    }

    @GET
    @Produces("application/json ;charset=utf-8")
    @Path("/sincronizarusuarios")
    @SuppressWarnings("unchecked")
    public String sincronizarUsuariosHandbox() {
        
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            try {
                res.bloquear();
                res.setOperacionEjecutada("sincronizarUsuariosHandbox");
                res.getMensajelog().addLinea("El sentido es de Handbox a Feeds");
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new BloggersCallable("sincronizarUsuariosHandbox"));
    
                respuesta.setCodigo("200");
                respuesta.setMensaje("Sincronizacion en curso");
                
            } catch (Exception e) {
                // TODO: Add catch code
                res.desbloquear();
                e.printStackTrace();
            }
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se esta ejecutando algun proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/numbloggers")
    public String numBloggers() {
       Bloggers bloggers = new Bloggers();
       String resultado = bloggers.getNumBloggers();
       return "{\"Respuesta\":{"+resultado+"}}";
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/verresultados")
    public String verResultados() {
        Resultado res = Resultado.getResultado();
        Gson gson = new Gson();
        return gson.toJson(res);
    }

    @GET
    @Produces("application/json")
    @Path("/testdehilos")
    @SuppressWarnings("unchecked")
    public String testDeHilos() {
        
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            try {
                res.bloquear();
                res.setOperacionEjecutada("testDeHilos");
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new BloggersCallable("testDeHilos"));
    
                respuesta.setCodigo("200");
                respuesta.setMensaje("testDeHilos en curso");
                
            } catch (Exception e) {
                // TODO: Add catch code
                res.desbloquear();
                e.printStackTrace();
            }
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se esta ejecutando algun proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }
    
}
