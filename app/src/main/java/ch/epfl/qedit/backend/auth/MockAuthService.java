package ch.epfl.qedit.backend.auth;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import java.util.concurrent.CompletableFuture;

public class MockAuthService implements AuthenticationService {

    private final CountingIdlingResource idlingResource;

    private String currentUserId;

    public MockAuthService() {
        currentUserId = null;
        idlingResource = new CountingIdlingResource("MockAuthService");
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    @Override
    public CompletableFuture<String> signUp(String email, String password) {
        return null;
    }

    @Override
    public CompletableFuture<String> logIn(String email, String password) {
        return null;
    }

    /**
     * private final HashMap<String, Response<User>> userResponses = new HashMap<String,
     * Response<User>>() { { //noinspection SpellCheckingInspection put("fjd4ywnzcCcLHaVb7oKg",
     * Response.<User>error(CONNECTION_ERROR)); //noinspection SpellCheckingInspection
     * put("fjd4ywnzXCXLHaVb7oKg", Response.ok(createMarcel())); //noinspection
     * SpellCheckingInspection put("R4rXRVU3EMkgm5YEW52Q", Response.ok(createCosme()));
     * put("v5ns9OMqV4hH7jwD8S5w", Response.ok(createAnthony())); } }; @Override public void
     * sendRequest(final String token, final Callback<Response<User>> responseCallback) {
     * idlingResource.increment(); new Thread( new Runnable() { @Override public void run() { try {
     * Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
     *
     * <p>Response<User> response; if (!userResponses.containsKey(token)) response =
     * Response.error(WRONG_TOKEN); else response = userResponses.get(token);
     *
     * <p>idlingResource.decrement(); responseCallback.onReceive(response); } }) .start(); }
     *
     * <p>public IdlingResource getIdlingResource() { return idlingResource; }
     *
     * <p>private User createMarcel() { User marcel = new User("Marcel", "Doe");
     * marcel.addQuiz("quiz0", "Qualification EPFL");
     *
     * <p>return marcel; }
     *
     * <p>private User createCosme() { User cosme = new User("Cosme", "Jordan");
     * cosme.addQuiz("quiz0", "Qualification EPFL");
     *
     * <p>return cosme; }
     *
     * <p>private User createAnthony() { User anthony = new User("Anthony", "Iozzia");
     * anthony.addQuiz("quiz0", "Quiz 0"); anthony.addQuiz("quiz1", "Quiz 1");
     * anthony.addQuiz("quiz2", "Quiz 2"); anthony.addQuiz("quiz3", "Quiz 3");
     *
     * <p>return anthony; } *
     */
}
