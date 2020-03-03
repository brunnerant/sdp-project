package ch.epfl.qedit.frontendBackendInterface;

public class RequestLogin extends Request {
    private String id;
    private String password;

    public RequestLogin(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
