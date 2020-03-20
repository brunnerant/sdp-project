package ch.epfl.qedit.backend.auth;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import java.util.HashMap;

public class MockAuthService implements AuthenticationService {

    private CountingIdlingResource idlingResource;

    public MockAuthService() {
        idlingResource = new CountingIdlingResource("MockAuthService");
    }

    private final HashMap<String, Response<User>> userResponses =
            new HashMap<String, Response<User>>() {
                {
                    //noinspection SpellCheckingInspection
                    put("fjd4ywnzcCcLHaVb7oKg", Response.<User>error(CONNECTION_ERROR));
                    //noinspection SpellCheckingInspection
                    put(
                            "fjd4ywnzXCXLHaVb7oKg",
                            Response.ok(new User("Marcel", "Doe", User.Role.Participant)));
                    //noinspection SpellCheckingInspection,SpellCheckingInspection
                    put(
                            "R4rXRVU3EMkgm5YEW52Q",
                            Response.ok(new User("Cosme", "Jordan", User.Role.Editor)));
                    put(
                            "v5ns9OMqV4hH7jwD8S5w",
                            Response.ok(new User("Anthony", "Iozzia", User.Role.Administrator)));
                }
            };

    @Override
    public void sendRequest(final String token, final Callback<Response<User>> responseCallback) {
        idlingResource.increment();
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
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
                            }
                        })
                .start();
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }
}
