package es.handbox.model;

import java.util.ArrayList;

public class Resultado {

    public void setPost(ArrayList<String> post) {
        this.post = post;
    }

    public void setOperacionEjecutada(String operacionEjecutada) {
        this.operacionEjecutada = operacionEjecutada;
    }

    public String getOperacionEjecutada() {
        return operacionEjecutada;
    }

    public ArrayList<String> getPost() {
        return post;
    }
    private static Resultado elresultado;
    
    private String operacionEjecutada;
    private MensajeLog mensajelog;
    private ArrayList<User> usuarios;
    private ArrayList<String> post;
    private boolean bloqueado;

    public void bloquear() {
        this.bloqueado = true;
        mensajelog = new MensajeLog();
        usuarios = new ArrayList<User>();
        post = new ArrayList<String>();
        operacionEjecutada = "";
    }
    public void desbloquear() {
        this.bloqueado = false;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }


    public  static Resultado getResultado() {
         if (elresultado==null) {
            elresultado = new Resultado();
         }
         return elresultado;
     }
     
         
    public Resultado() {
        super();
        mensajelog = new MensajeLog();
        usuarios = new ArrayList<User>();
        post = new ArrayList<String>();
    }
    
    public void setMensajelog(MensajeLog mensajelog) {
        this.mensajelog = mensajelog;
    }

    public MensajeLog getMensajelog() {
        return mensajelog;
    }

    public void setUsuarios(ArrayList<User> usuarios) {
        this.usuarios = usuarios;
    }

    public ArrayList<User> getUsuarios() {
        return usuarios;
    }
}
