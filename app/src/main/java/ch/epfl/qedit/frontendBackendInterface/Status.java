package ch.epfl.qedit.frontendBackendInterface;

public class Status {
    private boolean ok;
    private String message;

    public static final String okMessage = "The status is OK";

    public Status() {
        ok = true;
        message = okMessage;
    }

    public Status(String errorMessage) {
        ok = false;
        message = errorMessage;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public void error(String message) {
        ok = false;
        this.message = message;
    }

    public void ok() {
        ok = true;
        message = okMessage;
    }
}
