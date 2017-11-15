package es.handbox.model;

import java.util.ArrayList;

public class MensajeLog {
    public void setLinea(ArrayList<String> linea) {
        this.lineas = linea;
    }

    public ArrayList<String> getLinea() {
        return lineas;
    }
    
    public void addLinea(String linea) {
        System.out.println(linea);
        this.lineas.add(linea);
    }
    
    public void lastLinea(String linea) {
        System.out.println(linea);
        this.lineas.set(lineas.size()-1,linea);
    }

    private ArrayList<String> lineas = new ArrayList<String>();
    
    public MensajeLog() {
        super();
    }
}
