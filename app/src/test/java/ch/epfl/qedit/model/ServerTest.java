package ch.epfl.qedit.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ServerTest {

    @Test
    public void test() {
        Server server = Server.getInstance();
        assertTrue(!server.isOut());
        server.initServer();
        server.signOut();
        assertTrue(server.isOut());
    }

    @Test
    public void test2() {
        Server server = Server.getInstance();
        server.initServer();

        assertFalse(server.createUser("", ""));

        assertTrue(!server.signIn("", ""));
    }
}
