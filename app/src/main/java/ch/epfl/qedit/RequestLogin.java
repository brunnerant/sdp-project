package ch.epfl.qedit;

public class RequestLogin extends Request {
    private User user;
    private String password;

    public RequestLogin(User user, String password) {
        this.user = user;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
