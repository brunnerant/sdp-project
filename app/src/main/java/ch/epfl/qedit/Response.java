package ch.epfl.qedit;

public abstract class Response {
    private Status status;

    public Status getStatus() {
        return status;
    }

    protected void setStatus(Status status) {
        this.status = status;
    }
}
