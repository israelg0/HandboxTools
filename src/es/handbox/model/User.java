package es.handbox.model;

public class User {
    private String id;

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    private String displayName;
    private String userUrl;
    private String userNicename;
    
    private String tipoUsuario;

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }
    private String feedUrl;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserNicename(String userNicename) {
        this.userNicename = userNicename;
    }

    public String getUserNicename() {
        return userNicename;
    }

    public User() {
        super();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

   
}
