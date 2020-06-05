package ch.epfl.qedit.login;

import androidx.test.espresso.IdlingRegistry;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;

public class LoginHelper {

    protected MockAuthService authService;
    protected MockDBService dbService;

    protected void init() {
        authService = new MockAuthService();
        AuthenticationFactory.setInstance(authService);
        IdlingRegistry.getInstance().register(authService.getIdlingResource());

        dbService = new MockDBService();
        DatabaseFactory.setInstance(context -> dbService);
        IdlingRegistry.getInstance().register(dbService.getIdlingResource());
    }

    protected void cleanup() {
        IdlingRegistry.getInstance().unregister(authService.getIdlingResource());
        IdlingRegistry.getInstance().unregister(dbService.getIdlingResource());
    }
}
