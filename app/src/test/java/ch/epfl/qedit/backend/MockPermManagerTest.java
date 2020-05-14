package ch.epfl.qedit.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import ch.epfl.qedit.backend.permission.MockPermManager;
import ch.epfl.qedit.backend.permission.PermManagerFactory;
import ch.epfl.qedit.backend.permission.PermissionManager;
import org.junit.Test;

public class MockPermManagerTest {
    // Note that in all tests, we pass null when the activity is needed. This is because the
    // activity is only needed for the real permission manager, and I was not able to decouple
    // the permission manager and the activity, because of how the permission system works.

    // Sets some dummy permissions to test for
    private void setDummyPermissions(MockPermManager manager) {
        manager.grantPermissions("granted"); // this one is already granted
        manager.resetPermissions("accepted"); // this one is not granted, but should be accepted
        manager.refusePermissions("refused"); // this one is not granted and should not be accepted
        manager.denyPermissions("denied"); // this one is not and will not be granted
    }

    @Test
    public void testThatGrantedPermissionIsGranted() {
        MockPermManager manager = new MockPermManager();

        assertFalse(manager.checkPermission(null, "granted"));
        assertFalse(manager.checkPermission(null, "accepted"));
        assertFalse(manager.checkPermission(null, "refused"));
        assertFalse(manager.checkPermission(null, "denied"));

        setDummyPermissions(manager);

        assertTrue(manager.checkPermission(null, "granted"));
        assertFalse(manager.checkPermission(null, "accepted"));
        assertFalse(manager.checkPermission(null, "refused"));
        assertFalse(manager.checkPermission(null, "denied"));
    }

    @Test
    public void testPermissionRequests() {
        MockPermManager manager = new MockPermManager();
        PermissionManager.OnPermissionResult callback =
                mock(PermissionManager.OnPermissionResult.class);
        setDummyPermissions(manager);

        manager.requestPermissions(null, callback, "granted", "accepted", "refused", "denied");
        verify(callback, timeout(1000).times(1))
                .handle(aryEq(new boolean[] {true, true, false, false}));
    }

    @Test
    public void testShouldAskAgain() {
        MockPermManager manager = new MockPermManager();
        PermissionManager.OnPermissionResult callback =
                mock(PermissionManager.OnPermissionResult.class);
        setDummyPermissions(manager);

        assertFalse(manager.shouldAskAgain(null, "granted"));
        assertTrue(manager.shouldAskAgain(null, "accepted"));
        assertTrue(manager.shouldAskAgain(null, "refused"));
        assertFalse(manager.shouldAskAgain(null, "denied"));
    }

    @Test
    public void testThatFactoryCanSetInstance() {
        MockPermManager manager = new MockPermManager();
        assertNotNull(PermManagerFactory.getInstance());
        PermManagerFactory.setInstance(manager);
        assertEquals(manager, PermManagerFactory.getInstance());
    }
}
