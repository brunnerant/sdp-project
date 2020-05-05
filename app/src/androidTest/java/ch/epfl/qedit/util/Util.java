package ch.epfl.qedit.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import ch.epfl.qedit.R;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import java.util.Arrays;

public final class Util {

    private Util() {};

    public static Quiz createMockQuiz(String title) {
        return new Quiz(
                title,
                Arrays.asList(
                        new Question(
                                "The matches problem",
                                "How many matches can fit in a shoe of size 43?",
                                "matrix3x3"),
                        new Question(
                                "Pigeons",
                                "How many pigeons are there on Earth? (Hint: do not count yourself)",
                                "matrix1x1"),
                        new Question("KitchenBu", "Oyster", "matrix1x1"),
                        new Question(
                                "Everything",
                                "What is the answer to life the universe and everything?",
                                "matrix3x3"),
                        new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1")));
    }

    public static void isDisplayed(int id, boolean scrollTo) {
        if (scrollTo)
            onView(withId(id)).perform(scrollTo()).check(matches(ViewMatchers.isDisplayed()));
        else onView(withId(id)).check(matches(ViewMatchers.isDisplayed()));
    }

    public static void clickOn(int id, boolean scrollTo) {
        if (scrollTo) onView(withId(id)).perform(scrollTo()).perform(click());
        else onView(withId(id)).perform(click());
    }

    public static ViewInteraction onDialog(int id) {
        return onView(withId(id)).inRoot(isDialog());
    }

    public static void inputSolutionText(String sol) {
        onDialog(R.id.field_solution).perform(typeText(sol));
        Espresso.closeSoftKeyboard();
    }

    public static void inputText(int viewId, String str) {
        onView(withId(viewId))
                .perform(scrollTo())
                .perform(typeText(str))
                .perform(closeSoftKeyboard());
    }
}
