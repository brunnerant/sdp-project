package ch.epfl.qedit.frontendBackendInterface;

public abstract class FallibleEntity {
    private Status status;

    public FallibleEntity() {
        status = new Status();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
