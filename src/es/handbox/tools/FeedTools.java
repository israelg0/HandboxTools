package es.handbox.tools;

import com.google.gson.Gson;

import es.handbox.model.Respuesta;
import es.handbox.model.Resultado;
import es.handbox.tools.callable.FeedsCallable;
import es.handbox.tools.pojo.Bloggers;

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
    @Produces("application/json ;charset=utf-8")
    @Path("/sincronizarusuarios")
    @SuppressWarnings("unchecked")
    public String sincronizarUsuariosHandbox(@QueryParam("sentido") String sentido) {
        
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            try {
                if (sentido!=null)
                {
                    res.bloquear();
                    res.setOperacionEjecutada("sincronizarUsuariosHandbox");
                    res.getMensajelog().addLinea("El sentido es " + sentido);
                    ExecutorService servicio = Executors.newFixedThreadPool(1);
                    servicio.submit(new FeedsCallable("sincronizarUsuariosHandbox",sentido));
                    respuesta.setCodigo("200");
                    respuesta.setMensaje("Sincronizacion en curso");
                }else{
                     respuesta.setCodigo("701");
                     respuesta.setMensaje("Parámetros incorrectos, necesarios sentido y limite ");
                 }
                
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
    @Produces("application/json ;charset=utf-8")
    @Path("/sincronizarusuariosbeta")
    @SuppressWarnings("unchecked")
    public String sincronizarUsuariosHandbox(@QueryParam("sentido") String sentido,@QueryParam("limite") String limite) {
        
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            try {
                res.bloquear();
                res.setOperacionEjecutada("sincronizarUsuariosBeta");
                res.getMensajelog().addLinea("El sentido es " + sentido);
                if ((sentido!=null)&&(limite!=null))
                {
                    ExecutorService servicio = Executors.newFixedThreadPool(1);
                    servicio.submit(new FeedsCallable("sincronizarUsuariosBeta",sentido,limite));
                    respuesta.setCodigo("200");
                    respuesta.setMensaje("Sincronizacion en curso");
                }else{
                     respuesta.setCodigo("701");
                     respuesta.setMensaje("Parámetros incorrectos, necesarios sentido y limite ");
                 }
    
                
                
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
    public String numBloggers(@QueryParam("entorno") String entorno) {
        Respuesta respuesta  = new Respuesta();
        if (entorno!=null)
        {
           Bloggers bloggers = new Bloggers(entorno);
           String resultado = bloggers.getNumBloggers();
           respuesta.setCodigo("200"); 
           respuesta.setMensaje("Respuesta:"+resultado);
        }else{
             respuesta.setCodigo("701");
             respuesta.setMensaje("Parámetros incorrectos, necesario entorno ");
         }
        Gson gson = new Gson();
        return gson.toJson(respuesta);
    }

    @GET
    @Produces("application/json")
    @Path("/bloggerssinfeed")
    public String bloggersSinFeed(@QueryParam("entorno") String entorno) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            if (entorno!=null)
            {
                res.bloquear();
                res.setOperacionEjecutada("bloggersSinFeed " + entorno);
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new FeedsCallable("bloggersSinFeed", entorno));
                respuesta.setCodigo("200");
                respuesta.setMensaje("Obteniendo Bloggers sin Feed");
            }else{
                 respuesta.setCodigo("701");
                 respuesta.setMensaje("Parámetros incorrectos, necesario entorno ");
             }
             
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
    public String sincronizarEntradas(@QueryParam("sentido") String sentido, @QueryParam("limite") String limite) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            
            if ((sentido!=null)&&(limite!=null))
            {
                res.bloquear();            
                res.setOperacionEjecutada("sincronizarEntradas " + sentido + " " + limite);
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new FeedsCallable("sincronizarEntradas", sentido,  limite));
                respuesta.setCodigo("200");
                respuesta.setMensaje("Sincronización en curso");
                res.desbloquear();
            }else{
                 respuesta.setCodigo("701");
                 respuesta.setMensaje("Parámetros incorrectos, necesarios sentido y limite ");
             }
            
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se está ejecutando algún proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/categorizarentrada")
    public String categorizarEntrada(@QueryParam("entorno") String entorno, @QueryParam("identrada") String identrada) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            
            if ((entorno!=null)&&(identrada!=null))
            {
                res.bloquear();            
                res.setOperacionEjecutada("categorizarEntrada " + entorno + " " + identrada);
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new FeedsCallable("categorizarEntrada", entorno, identrada));
                respuesta.setCodigo("200");
                respuesta.setMensaje("Categorización en curso");
                res.desbloquear();
            }else{
                 respuesta.setCodigo("701");
                 respuesta.setMensaje("Parámetros incorrectos, necesarios entorno y identrada");
             }
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se está ejecutando algún proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }
    
    @GET
    @Produces("application/json")
    @Path("/categorizartodas")
    public String categorizarTodos(@QueryParam("entorno") String entorno,@QueryParam("limite") String limite, @QueryParam("identrada") String identrada) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
            if ((entorno!=null)&&(identrada!=null)&&(limite!=null))
            {
                res.bloquear();            
                res.setOperacionEjecutada("categorizarTodas " + entorno + " " + limite + " " + identrada);
                ExecutorService servicio = Executors.newFixedThreadPool(1);
                servicio.submit(new FeedsCallable("categorizarTodas", entorno,limite, identrada));
                respuesta.setCodigo("200");
                respuesta.setMensaje("Categorización en curso");
                res.desbloquear();
            }else{
                 respuesta.setCodigo("701");
                 respuesta.setMensaje("Parámetros incorrectos, necesarios entorno, identrada y limite");
             }
        }else {
            respuesta.setCodigo("601");
            respuesta.setMensaje("Ya se está ejecutando algún proceso. Por favor, espera unos minutos para volver a intentarlo");
        }
       
       Gson gson = new Gson();
       return gson.toJson(respuesta);
    }
    
    @GET
    @Produces("application/json")
    @Path("/CategorizarVideo")
    public String categorizarVideo(@QueryParam("entorno") String entorno ) {
        Respuesta respuesta  = new Respuesta();
        Resultado res = Resultado.getResultado();
        if (!res.isBloqueado())
        {
                if (entorno!=null)
                {
                    res.bloquear();            
                    res.setOperacionEjecutada("categorizarVideo " + entorno);
                    ExecutorService servicio = Executors.newFixedThreadPool(1);
                    servicio.submit(new FeedsCallable("categorizarVideo", entorno));
                    respuesta.setCodigo("200");
                    respuesta.setMensaje("Sincronización en curso");
                    res.desbloquear();
                }else {
                    respuesta.setCodigo("701");
                    respuesta.setMensaje("Parámetros incorrectos, necesario entorno");
                }
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
