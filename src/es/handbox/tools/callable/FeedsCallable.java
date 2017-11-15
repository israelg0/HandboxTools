package es.handbox.tools.callable;

import es.handbox.model.Resultado;
import es.handbox.tools.pojo.Bloggers;

import es.handbox.tools.pojo.SincronizarPostsHandbox;

import java.util.concurrent.Callable;

public class FeedsCallable implements Callable {
    
    private String metodo = "";
    private String limite;
    
    public FeedsCallable(String _metodo) {
        super();
        metodo = _metodo;
        limite = "";
    }
    
    public FeedsCallable(String _metodo, String _limite) {
        super();
        metodo = _metodo;
        limite = _limite;
    }
    
    @Override
    public Object call() throws Exception {
        if (metodo.equals("bloggersSinFeed")) {
            Bloggers bloggers = new Bloggers("feeds2hbx");
            bloggers.bloggersSinFeed();
        }
        if (metodo.equals("sincronizarEntradas")) {
            SincronizarPostsHandbox sph = new SincronizarPostsHandbox();
            if (limite.equals("")) limite = "5";
            sph.sincronizarPosts(limite);
        }
        Resultado.getResultado().desbloquear();
        return null;
    }
}
