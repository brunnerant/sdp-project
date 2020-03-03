package ch.epfl.qedit.backend;

import ch.epfl.qedit.frontendBackendInterface.FallibleEntity;
import ch.epfl.qedit.frontendBackendInterface.Request;
import ch.epfl.qedit.frontendBackendInterface.Response;

public abstract class Backend extends FallibleEntity {
    public Backend() {
        super();
    }

    public abstract Response sendRequest(Request request);
}
