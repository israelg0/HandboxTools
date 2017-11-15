package es.handbox.tools;

import com.google.gson.Gson;

import es.handbox.model.Resultado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @return
 */
@Path("tools")
public class Test {
    public Test() {
        super();
    }


    /**
     * @return
     */
    @GET
    @Produces("application/json")
    @Path("/getsaludo")
    public String getSaludo() {
        Calendar cal = Calendar.getInstance();
        return "{\"saludo\":{\"texto\":\"Hola mundo, son las " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)+"\"}}";
    }

    /**
     * @return
     */
    @GET
    @Produces("application/json")
    @Path("/desbloquear")
    public String desbloquear() {
        Resultado res = Resultado.getResultado();
        res.desbloquear();
        Gson gson = new Gson();
        return gson.toJson(res);
    }


    /**
     * @param nombre
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/setnombre")
    public void setNombre(@QueryParam("nombre") String nombre) {
        System.out.println(nombre);
    }


    /**
     * @param nombre
     * @return
     */
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/gethora")
    public String getHora(@QueryParam("nombre") String nombre) {
        Calendar cal = Calendar.getInstance();
        return "{\"saludo\":{\"texto\":\"Hola "+nombre+", son las " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)+"\"}}";
    }

    @GET
    @Produces("application/json")
    @Path("/getls")
    public void getls() {
 
            try {
            ProcessBuilder probuilder = new ProcessBuilder("/bin/sh", "-c", "ls -l");
            //ProcessBuilder probuilder = new ProcessBuilder("cmd", "/C", "dir");
            Process process = probuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String s = null;

            while ((s = br.readLine()) != null) {

                System.out.println(s);
            }
            try {
                Thread.sleep(3000);
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
