package ch.epfl.qedit;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.LoginActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityLanguageTest {
    @Rule
    public final IntentsTestRule<LoginActivity> testRule =
            new IntentsTestRule<>(LoginActivity.class, false, false);

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        testRule.launchActivity(intent);
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    private void testChangeLanguage(
            String language,
            String languageCode,
            String loginString,
            String languageChangedString) {
        String startupLanguage = Locale.getDefault().getLanguage();
        String lang = LocaleHelper.getLanguage(testRule.getActivity());

        // Language position
        int pos = 0;
        if (languageCode.equals("en")) {
            pos = 0;
        } else if (languageCode.equals("fr")) {
            pos = 1;
        }

        onView(withId(R.id.login_button)).perform(closeSoftKeyboard());
        onView(withId(R.id.language_selection)).perform(click());
        onData(anything()).atPosition(pos).perform(click());
        onView(withId(R.id.language_selection)).check(matches(withSpinnerText(language)));
        onView(withId(R.id.login_button)).check(matches(withText(loginString)));

        // Test toast
        if (startupLanguage != languageCode) {
            onView(withText(languageChangedString))
                    .inRoot(
                            withDecorView(
                                    not(is(testRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void testChangeLanguageEnglish() {
        testChangeLanguage("English", "en", "Login", "Language changed to English");
    }

    @Test
    public void testChangeLanguageFrench() {
        testChangeLanguage("Français", "fr", "Connexion", "Langue changée en Français");
    }
}
