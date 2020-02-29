package ch.epfl.qedit.backend;

import ch.epfl.qedit.Request;
import ch.epfl.qedit.Response;
import ch.epfl.qedit.Status;

public abstract class Backend {
    private Status status;

    public Backend() {
        status = new Status();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract Response sendRequest(Request request);
}
