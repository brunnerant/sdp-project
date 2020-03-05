package ch.epfl.qedit.frontendBackendInterface;

public class ResponseLogin extends Response {
    private Status status;
    private User user;

    public ResponseLogin(User user) {
        status = Status.ok();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
