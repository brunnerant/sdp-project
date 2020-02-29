package ch.epfl.qedit;

public class Status {
    private boolean ok;
    private String message;
    private final String okMessage = "The status is OK";

    public Status() {
        ok = true;
        message = okMessage;
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


}
