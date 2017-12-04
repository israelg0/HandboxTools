package es.handbox.tools.callable;

import es.handbox.model.Resultado;
import es.handbox.tools.pojo.Bloggers;

import es.handbox.tools.pojo.CategorizadorHandbox;
import es.handbox.tools.pojo.SincronizarPostsHandbox;

import java.util.concurrent.Callable;

public class FeedsCallable implements Callable {
    
    private String metodo = "";
    private String limite;
    private String sentido = "";
    private String idEntrada = "";
    
    public FeedsCallable(String _metodo) {
        super();
        metodo = _metodo;
        limite = "";
    }
    
    public FeedsCallable(String _metodo, String _sentido) {
        super();
        metodo = _metodo;
        sentido = _sentido;
    }
    
    public FeedsCallable(String _metodo, String _sentido, String _limite) {
        super();
        
    
        if (_metodo.equalsIgnoreCase("categorizarEntrada"))
        {
            metodo = _metodo;
            sentido = _sentido;
            idEntrada = _limite;
        }else {
            metodo = _metodo;
            limite = _limite;
            sentido = _sentido;
        }
    }
    public FeedsCallable(String _metodo, String _sentido, String _limite, String _idEntrada) {
        super();
        limite = _limite;
        metodo = _metodo;
        sentido = _sentido;
        idEntrada = _idEntrada;
    }
    
    @Override
    public Object call() throws Exception {
        if (metodo.equals("sincronizarUsuariosHandbox")) {
            Bloggers bloggers = new Bloggers(sentido);
            bloggers.sincronizarUsuariosHandbox();
        }
        if (metodo.equals("bloggersSinFeed")) {
            Bloggers bloggers = new Bloggers(sentido);
            bloggers.bloggersSinFeed();
        }
        if (metodo.equals("sincronizarEntradas")) {
            SincronizarPostsHandbox sph = new SincronizarPostsHandbox();
            if (limite.equals("")) limite = "5";
            sph.sincronizarPosts(limite);
        }
        if (metodo.equals("categorizarEntrada")) {
            CategorizadorHandbox cat = new CategorizadorHandbox(sentido);
            cat.categorizarPostv1(Integer.parseInt(idEntrada));
        }
        if (metodo.equals("categorizarTodas")) {
            CategorizadorHandbox cat = new CategorizadorHandbox(sentido);
            cat.categorizarTodos(Integer.parseInt(idEntrada), Integer.parseInt(limite));
        }
        Resultado.getResultado().desbloquear();
        return null;
    }
}
