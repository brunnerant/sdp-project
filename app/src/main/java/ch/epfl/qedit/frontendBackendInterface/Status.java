package ch.epfl.qedit.frontendBackendInterface;

public class Status {
    private final boolean ok;
    private final String message;

    private Status(Boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public static Status ok() {
        return new Status(true, "The status is OK");
    }

    public static Status error(String message) {
        return new Status(false, message);
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
