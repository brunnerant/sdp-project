package ch.epfl.qedit.login;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.login.LogInActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LogInActivityLanguageTest {

    @Rule
    public final IntentsTestRule<LogInActivity> testRule =
            new IntentsTestRule<>(LogInActivity.class, false, false);

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        testRule.launchActivity(intent);
        Espresso.closeSoftKeyboard();
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    private void testChangeLanguage(String language, String languageCode, String loginString) {
        @SuppressWarnings("unused")
        String lang = LocaleHelper.getLanguage(testRule.getActivity());

        // Language position
        int pos;
        if (languageCode.equals("en")) {
            pos = 0;
        } else if (languageCode.equals("fr")) {
            pos = 1;
        } else {
            pos = 0;
        }

        clickOn(R.id.spinner_language_selection, true);
        onData(anything()).atPosition(1 - pos).perform(click());

        onView(withId(R.id.button_log_in)).perform(closeSoftKeyboard());
        clickOn(R.id.spinner_language_selection, true);
        onData(anything()).atPosition(pos).perform(click());
        onView(withId(R.id.spinner_language_selection)).check(matches(withSpinnerText(language)));
        onView(withId(R.id.button_log_in)).check(matches(withText(loginString)));
    }

    @Test
    public void testChangeLanguageEnglish() {
        testChangeLanguage("English", "en", "Log in");
    }

    @Test
    public void testChangeLanguageFrench() {
        testChangeLanguage("Français", "fr", "Connexion");
    }
}
