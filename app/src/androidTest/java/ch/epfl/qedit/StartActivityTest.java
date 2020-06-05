package ch.epfl.qedit;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static ch.epfl.qedit.backend.auth.MockAuthService.ANTHONY_IOZZIA_ID;
import static ch.epfl.qedit.backend.database.MockDBService.createAnthony;
import static org.hamcrest.core.AllOf.allOf;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class StartActivityTest {

    @Rule
    public final ActivityTestRule<StartActivity> testRule =
            new ActivityTestRule<>(StartActivity.class, false, false);

    private MockAuthService authService;
    private MockDBService dbService;

    public void init(String user) {
        authService = new MockAuthService();
        authService.setUser(user);
        dbService = new MockDBService();

        AuthenticationFactory.setInstance(authService);
        DatabaseFactory.setInstance(context -> dbService);

        IdlingRegistry.getInstance().register(authService.getIdlingResource());
        IdlingRegistry.getInstance().register(dbService.getIdlingResource());

        Intents.init();
        testRule.launchActivity(null);
    }

    @After
    public void cleanup() {
        testRule.finishActivity();
        Intents.release();

        IdlingRegistry.getInstance().unregister(authService.getIdlingResource());
        IdlingRegistry.getInstance().unregister(dbService.getIdlingResource());
    }

    @Test
    public void testLaunchesHomeActivityIfAlreadyLoggedIn() {
        init(ANTHONY_IOZZIA_ID);
        intended(
                allOf(
                        hasComponent(HomeActivity.class.getName()),
                        hasExtra(HomeActivity.USER, createAnthony())));
    }

    @Test
    public void testLaunchesLoginActivityIfNotLoggedIn() {
        init(null);
        intended(hasComponent(LogInActivity.class.getName()));
    }
}
