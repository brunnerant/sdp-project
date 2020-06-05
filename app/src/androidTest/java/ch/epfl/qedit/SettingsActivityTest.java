package ch.epfl.qedit;

import androidx.test.espresso.Espresso;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.SettingsActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.qedit.util.Util.clickOn;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SettingsActivityTest {

    @Rule
    public final ActivityTestRule<SettingsActivity> testRule =
            new ActivityTestRule<>(SettingsActivity.class, false, false);

    @Before
    public void launchActivity() {
        testRule.launchActivity(null);
        Espresso.closeSoftKeyboard();
    }

    @After
    public void finishActivity() {
        testRule.finishActivity();
    }

    private void testChangeLanguage(String language, String languageCode, String buttonString) {
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

        clickOn(R.id.spinner_language_selection, false);
        onData(anything()).atPosition(1 - pos).perform(click());

        onView(withId(R.id.button_save)).perform(closeSoftKeyboard());
        clickOn(R.id.spinner_language_selection, false);
        onData(anything()).atPosition(pos).perform(click());
        onView(withId(R.id.spinner_language_selection))
                .check(matches(withSpinnerText(language)));
        onView(withId(R.id.button_save))
                .check(matches(withText(buttonString)));
    }

    @Test
    public void testChangeLanguageEnglish() {
        testChangeLanguage("English", "en", "Save");
    }

    @Test
    public void testChangeLanguageFrench() {
        testChangeLanguage("Français", "fr", "Enregistrer");
    }
}
