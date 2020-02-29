package ch.epfl.qedit;

public class ResponseLogin extends Response {
    private User user;
    private Role role;

    public ResponseLogin(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }
}
