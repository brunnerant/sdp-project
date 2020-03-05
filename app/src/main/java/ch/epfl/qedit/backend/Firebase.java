package ch.epfl.qedit.backend;

import ch.epfl.qedit.frontendBackendInterface.Fallible;
import ch.epfl.qedit.frontendBackendInterface.Request;
import ch.epfl.qedit.frontendBackendInterface.Response;
import ch.epfl.qedit.frontendBackendInterface.Status;

public class Firebase implements Backend, Fallible {
    private Status status;

    public Firebase() {
        status = Status.ok();
    }

    @Override
    public Response sendRequest(Request request) {
        return null;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
