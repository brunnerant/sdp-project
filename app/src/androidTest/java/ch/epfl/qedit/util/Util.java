package ch.epfl.qedit.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.qedit.model.StringPool.TITLE_ID;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.MockDBService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import java.util.HashMap;

public final class Util {

    private Util() {}

    public static Quiz createTestQuiz() {
        Quiz.Builder builder = new Quiz.Builder();
        builder.append(new Question("q1_title", "q1_text", "matrix3x3"))
                .append(new Question("q2_title", "q2_text", "matrix1x1"))
                .append(new Question("q3_title", "q3_text", "matrix1x1"))
                .append(new Question("q4_title", "q4_text", "matrix3x3"))
                .append(new Question("q5_title", "q5_text", "matrix1x1"));

        return builder.build();
    }

    public static StringPool createTestStringPool(String title) {
        HashMap<String, String> stringPool = new HashMap<>();
        stringPool.put(TITLE_ID, title);
        stringPool.put("q1_title", "The matches problem");
        stringPool.put("q1_text", "How many matches can fit in a shoe of size 43?");
        stringPool.put("q2_title", "Pigeons");
        stringPool.put(
                "q2_text", "How many pigeons are there on Earth? (Hint: do not count yourself)");
        stringPool.put("q3_title", "KitchenBu");
        stringPool.put("q3_text", "Oyster");
        stringPool.put("q4_title", "Everything");
        stringPool.put("q4_text", "What is the answer to life the universe and everything?");
        stringPool.put("q5_title", "Bananas");
        stringPool.put("q5_text", "How many bananas are there?");

        return new StringPool(stringPool);
    }

    /** Init a test rule activity with access to authentication and database services */
    public static IdlingResource initActivityWithMockAuthAndDBService(ActivityTestRule testRule) {
        MockAuthService authService = new MockAuthService();
        MockDBService dbService = new MockDBService();
        IdlingResource idlingResource = authService.getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        AuthenticationFactory.setInstance(authService);
        DatabaseFactory.setInstance(dbService);
        testRule.launchActivity(null);
        Intents.init();
        Espresso.closeSoftKeyboard();
        return idlingResource;
    }

    public static void cleanup(ActivityTestRule testRule, IdlingResource idlingResource) {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idlingResource);
        testRule.finishActivity();
    }

    public static Quiz createInstantiatedTestQuiz(String title) {
        return createTestQuiz().instantiateLanguage(createTestStringPool(title));
    }

    public static void isDisplayed(int id, boolean scrollTo) {
        if (scrollTo)
            onView(withId(id)).perform(scrollTo()).check(matches(ViewMatchers.isDisplayed()));
        else onView(withId(id)).check(matches(ViewMatchers.isDisplayed()));
    }

    public static void clickOn(int id, boolean scrollTo) {
        if (scrollTo) onScrollView(id).perform(click());
        else onView(withId(id)).perform(click());
    }

    public static ViewInteraction onDialog(int id) {
        return onView(withId(id)).inRoot(isDialog());
    }

    public static void inputSolutionText(String sol) {
        onDialog(R.id.field_solution).perform(typeText(sol));
        Espresso.closeSoftKeyboard();
    }

    public static ViewInteraction onScrollView(int id) {
        return onView(withId(id)).perform(scrollTo());
    }

    public static void inputText(int viewId, String str) {
        onScrollView(viewId).perform(typeText(str)).perform(closeSoftKeyboard());
    }
}
