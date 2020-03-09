package ch.epfl.qedit.backend;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import java.util.HashMap;

public class MockAuthService implements AuthenticationService {

    private final HashMap<String, LoginResponse> userResponses =
            new HashMap<String, LoginResponse>() {
                {
                    put("nicolas", LoginResponse.error(LoginResponse.Error.ConnectionError));
                    put(
                            "nathan",
                            LoginResponse.ok(new User("nathan", "greslin", User.Role.Participant)));
                    put(
                            "anthony",
                            LoginResponse.ok(new User("anthony", "iozzia", User.Role.Editor)));
                    put(
                            "antoine",
                            LoginResponse.ok(
                                    new User("antoine", "brunner", User.Role.Administrator)));
                }
            };

    @Override
    public void sendRequest(
            final String token, final Callback<LoginResponse> responseCallback) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                LoginResponse response;
                                if (!userResponses.containsKey(token))
                                    response =
                                            LoginResponse.error(
                                                    LoginResponse.Error.WrongToken);
                                else response = userResponses.get(token);

                                responseCallback.onReceive(response);
                            }
                        })
                .start();
    }
}
