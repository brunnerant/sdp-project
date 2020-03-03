package ch.epfl.qedit.frontendBackendInterface;

public class ResponseLogin extends Response {
    private User user;

    public ResponseLogin(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
