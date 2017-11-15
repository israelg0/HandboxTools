package es.handbox.tools.comandline;

import es.handbox.tools.pojo.Bloggers;

public class Hbxtools {
    public Hbxtools() {
        super();
    }
    public Hbxtools(int numUsuarios) {
        super();
        
        Bloggers blogger = new Bloggers("v2tobeta");
        blogger.sincronizarUsuariosBeta(numUsuarios);
    }

    public static void main(String[] args) {
        Hbxtools hbxtools = new Hbxtools();
        if (args.length < 1)
            hbxtools = new Hbxtools(0);
        else
            hbxtools = new Hbxtools(Integer.parseInt(args[0]));
        
    }
}
