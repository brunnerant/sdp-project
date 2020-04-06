package ch.epfl.qedit;

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
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.LoginActivity;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

        String currentLanguage = LocaleHelper.getLanguage(testRule.getActivity());

        setLanguage(languageCode);

        onView(withId(R.id.language_selection)).check(matches(withSpinnerText(language)));
        onView(withId(R.id.login_button)).check(matches(withText(loginString)));

        // Test toast
        if (!currentLanguage.equals(languageCode)) {
            onView(withText(languageChangedString))
                    .inRoot(
                            withDecorView(
                                    not(is(testRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }

    public int getIndexOfLanguage(String languageCode) {
        String[] languagesCodes =
                testRule.getActivity().getResources().getStringArray(R.array.languages_codes);

        int pos = -1;

        // Language position
        for (int i = 0; i < languagesCodes.length && pos < 0; ++i) {
            if (languageCode.equals(languagesCodes[i])) {
                pos = i;
            }
        }

        return pos;
    }

    public void setLanguage(String languageCode) {
        onView(withId(R.id.login_button)).perform(closeSoftKeyboard());
        onView(withId(R.id.language_selection)).perform(click());
        onData(anything()).atPosition(getIndexOfLanguage(languageCode)).perform(click());
    }

    @Test
    public void testChangeLanguageToEnglish() {
        setLanguage("fr");

        testChangeLanguage("English", "en", "Log in", "Language changed to English");

        assertEquals("en", Locale.getDefault().getLanguage());
    }

    @Test
    public void testChangeLanguageToFrench() {
        setLanguage("en");
        testChangeLanguage("Français", "fr", "Connexion", "Langue changée en Français");
        assertEquals("fr", Locale.getDefault().getLanguage());
    }

    @Test
    public void testStayOnEnglish() {
        setLanguage("en");
        testChangeLanguage("English", "en", "Log in", null);
        assertEquals("en", Locale.getDefault().getLanguage());
    }

    @Test
    public void testStayOnFrench() {
        setLanguage("fr");
        testChangeLanguage("Français", "fr", "Connexion", null);
        assertEquals("fr", Locale.getDefault().getLanguage());
    }
}
