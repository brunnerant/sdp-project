package ch.epfl.qedit.login;

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

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import ch.epfl.qedit.R;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.login.TokenLogInActivity;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TokenLogInActivityLanguageTest {
    @Rule
    public final IntentsTestRule<TokenLogInActivity> testRule =
            new IntentsTestRule<>(TokenLogInActivity.class, false, false);

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
        // We don't use this variable but the call increases the coverage ;)
        @SuppressWarnings("unused")
        String lang = LocaleHelper.getLanguage(testRule.getActivity());

        // Language position
        int pos = -1;
        if (languageCode.equals("en")) {
            pos = 0;
        } else if (languageCode.equals("fr")) {
            pos = 1;
        } else {
            pos = 0;
        }

        onView(ViewMatchers.withId(R.id.login_button)).perform(closeSoftKeyboard());
        onView(withId(R.id.language_selection_log_in_token)).perform(click());
        onData(anything()).atPosition(pos).perform(click());
        onView(withId(R.id.language_selection_log_in_token)).check(matches(withSpinnerText(language)));
        onView(withId(R.id.login_button)).check(matches(withText(loginString)));

        // Test toast
        if (!startupLanguage.equals(languageCode)) {
            onView(withText(languageChangedString))
                    .inRoot(
                            withDecorView(
                                    not(is(testRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void testChangeLanguageEnglish() {
        testChangeLanguage("English", "en", "Log in", "Language changed to English");
    }

    @Test
    public void testChangeLanguageFrench() {
        testChangeLanguage("Français", "fr", "Connexion", "Langue changée en Français");
    }
}
