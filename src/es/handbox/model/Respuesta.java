package es.handbox.model;

import com.google.gson.Gson;

public class Respuesta {
    
    private String codigo;
    
    private String mensaje;
    
    public Respuesta() {
        super();
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
    
    
}
