package ch.epfl.qedit.backend;

import ch.epfl.qedit.frontendBackendInterface.Request;
import ch.epfl.qedit.frontendBackendInterface.Response;

public interface Backend {
    public Response sendRequest(Request request);
}
