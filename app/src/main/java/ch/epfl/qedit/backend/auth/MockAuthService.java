package ch.epfl.qedit.backend.auth;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import java.util.HashMap;

public class MockAuthService implements AuthenticationService {

    private final CountingIdlingResource idlingResource;

    public MockAuthService() {
        idlingResource = new CountingIdlingResource("MockAuthService");
    }

    private final HashMap<String, Response<User>> userResponses =
            new HashMap<String, Response<User>>() {
                {
                    //noinspection SpellCheckingInspection
                    put("fjd4ywnzcCcLHaVb7oKg", Response.error(CONNECTION_ERROR));
                    //noinspection SpellCheckingInspection
                    put("fjd4ywnzXCXLHaVb7oKg", Response.ok(createMarcel()));
                    //noinspection SpellCheckingInspection
                    put("R4rXRVU3EMkgm5YEW52Q", Response.ok(createCosme()));
                    put("v5ns9OMqV4hH7jwD8S5w", Response.ok(createAnthony()));
                }
            };

    @Override
    public void sendRequest(final String token, final Callback<Response<User>> responseCallback) {
        idlingResource.increment();
        new Thread(
                        () -> {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Response<User> response;
                            if (!userResponses.containsKey(token))
                                response = Response.error(WRONG_TOKEN);
                            else response = userResponses.get(token);

                            idlingResource.decrement();
                            responseCallback.onReceive(response);
                        })
                .start();
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    private User createMarcel() {
        User marcel = new User("Marcel", "Doe");
        marcel.addQuiz("quiz0", "I am a Mock Quiz!");

        return marcel;
    }

    private User createCosme() {
        User cosme = new User("Cosme", "Jordan");
        cosme.addQuiz("quiz0", "I am a Mock Quiz!");

        return cosme;
    }

    private User createAnthony() {
        User anthony = new User("Anthony", "Iozzia");
        anthony.addQuiz("quiz0", "I am a Mock Quiz!");
        anthony.addQuiz("quiz1", "An other Quiz");
        anthony.addQuiz("quiz2", "An other Quiz");
        anthony.addQuiz("quiz3", "An other Quiz");

        return anthony;
    }
}
