package es.handbox.tools;

import com.google.gson.Gson;

import es.handbox.model.Respuesta;
import es.handbox.model.Resultado;
import es.handbox.tools.callable.BloggersCallable;
import es.handbox.tools.callable.FeedsCallable;
import es.handbox.tools.pojo.Bloggers;
import es.handbox.tools.pojo.SincronizarPostsHandbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("feed")
public class FeedTools  {
    Gson gson = new Gson();
    public FeedTools() {
        super();
    }

    @GET
    @Produces("application/json")
    @Path("/bloggerssinfeed")
    public String bloggersSinFeed() {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            res.bloquear();
            res.setOperacionEjecutada("bloggersSinFeed");
            ExecutorService servicio = Executors.newFixedThreadPool(1);
            servicio.submit(new FeedsCallable("bloggersSinFeed"));
            respuesta.setCodigo("200");
            respuesta.setMensaje("Obteniendo Bloggers sin Feed");
           
            
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se está ejecutando algún proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }

    @GET
    @Produces("application/json")
    @Path("/sincronizarentradas")
    public String sincronizarEntradas(@QueryParam("limite") int limite) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            res.bloquear();            
            res.setOperacionEjecutada("sincronizarEntradas " + limite);
            ExecutorService servicio = Executors.newFixedThreadPool(1);
            servicio.submit(new FeedsCallable("sincronizarEntradas", limite+""));
            respuesta.setCodigo("200");
            respuesta.setMensaje("Sincronización en curso");
            res.desbloquear();
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se está ejecutando algún proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }


    @GET
    @Produces("application/json")
    @Path("/verresultados")
    public String verResultados() {
        Resultado res = Resultado.getResultado();
        Gson gson = new Gson();
        return gson.toJson(res);
    }
   
}
