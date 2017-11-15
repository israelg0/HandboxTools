package es.handbox.tools;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("resources")
public class GenericApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(BloggerTools.class);
        classes.add(Test.class);
        classes.add(FeedTools.class);

        // Register provider classes.

        return classes;
    }
}
