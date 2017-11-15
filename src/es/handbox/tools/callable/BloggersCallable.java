package es.handbox.tools.callable;

import es.handbox.model.Respuesta;
import es.handbox.model.Resultado;
import es.handbox.tools.pojo.Bloggers;

import java.util.concurrent.Callable;

public class BloggersCallable implements Callable {
    
    private String metodo = "";
    
    public BloggersCallable(String _metodo) {
        super();
        metodo = _metodo;
    }

    @Override
    public Object call() throws Exception {
        if (metodo.equals("sincronizarUsuariosHandbox")) {
            Bloggers bloggers = new Bloggers("hbx2feeds");
            bloggers.sincronizarUsuariosHandbox();
        }
        if (metodo.equals("testDeHilos")) {
            Bloggers bloggers = new Bloggers();
            bloggers.testDeHilos();
        }
        Resultado.getResultado().desbloquear();
        return null;
    }
}
