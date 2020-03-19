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
                    put("nicolas", Response.<User>error(CONNECTION_ERROR));
                    put("nathan", Response.ok(createNathan()));
                    put("anthony", Response.ok(new User("anthony", "iozzia", User.Role.Editor)));
                    put("antoine", Response.ok(createAntoine()));
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

    private User createNathan() {
        User nathan = new User("nathan", "greslin", User.Role.Participant);
        nathan.addQuiz("quiz0", "Qualification EPFL");

        return nathan;
    }

    private User createAntoine() {
        User antoine = new User("antoine", "brunner", User.Role.Administrator);
        antoine.addQuiz("quiz1", "Quiz 1");
        antoine.addQuiz("quiz2", "Quiz 2");
        antoine.addQuiz("quiz3", "Quiz 3");

        return antoine;
    }
}
