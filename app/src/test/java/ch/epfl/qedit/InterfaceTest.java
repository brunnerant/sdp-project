package ch.epfl.qedit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

import ch.epfl.qedit.backend.Firebase;
import ch.epfl.qedit.frontendBackendInterface.RequestLogin;
import ch.epfl.qedit.frontendBackendInterface.Response;
import ch.epfl.qedit.frontendBackendInterface.ResponseLogin;
import ch.epfl.qedit.frontendBackendInterface.Role;
import ch.epfl.qedit.frontendBackendInterface.Status;
import ch.epfl.qedit.frontendBackendInterface.User;
import org.junit.Test;

public class InterfaceTest {
    @Test
    public void backendAndStatusTest() {
        Firebase firebase = new Firebase();
        Status status = new Status();

        assertEquals(status.isOk(), firebase.getStatus().isOk());
        assertEquals(status.getMessage(), firebase.getStatus().getMessage());

        String errorMessage = "This is a test error";

        status.error(errorMessage);
        firebase.setStatus(status);

        assertEquals(status.isOk(), firebase.getStatus().isOk());
        assertEquals(status.getMessage(), firebase.getStatus().getMessage());

        status.ok();

        assertTrue(status.isOk());
        assertEquals(Status.okMessage, status.getMessage());
    }

    @Test
    public void requestTest() {
        RequestLogin reqLog = new RequestLogin("Test", "pwd");

        Firebase firebase = new Firebase();
        Response resLog = firebase.sendRequest(reqLog);

        assertNull(resLog);

        assertEquals("Test", reqLog.getId());
        assertEquals("pwd", reqLog.getPassword());

        reqLog.setStatus(new Status("Test error"));

        assertFalse(reqLog.getStatus().isOk());
        assertEquals("Test error", reqLog.getStatus().getMessage());
    }

    @Test
    public void responseTest() {
        User user = new User("Name", Role.ADMIN, "pwd");

        assertEquals("Name", user.getName());
        assertEquals("pwd", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());

        user.setName("NewName");
        user.setPassword("newPwd");
        user.setRole(Role.EDITOR);

        ResponseLogin reqLog = new ResponseLogin(user);

        assertEquals(user, reqLog.getUser());

        assertTrue(reqLog.getStatus().isOk());
        assertTrue(reqLog.getStatus().isOk());
    }
}
