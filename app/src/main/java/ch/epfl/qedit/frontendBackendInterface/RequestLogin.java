package ch.epfl.qedit.frontendBackendInterface;

public class RequestLogin extends Request {
    private Status status;
    private String id;
    private String password;

    public RequestLogin(String id, String password) {
        status = Status.ok();
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
