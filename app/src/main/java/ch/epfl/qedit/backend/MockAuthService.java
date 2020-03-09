package ch.epfl.qedit.backend;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import java.util.HashMap;

public class MockAuthService implements AuthenticationService {

    private final HashMap<String, String> userPasswords =
            new HashMap<String, String>() {
                {
                    put("nicolas", "pwd");
                    put("nathan", "wanaga");
                    put("anthony", "jus");
                    put("antoine", "oui");
                }
            };

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
            final LoginRequest request, final Callback<LoginResponse> responseCallback) {
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
                                if (!userPasswords.containsKey(request.getUsername()))
                                    response =
                                            LoginResponse.error(
                                                    LoginResponse.Error.UserDoesNotExist);
                                else if (!userPasswords
                                        .get(request.getUsername())
                                        .equals(request.getPassword()))
                                    response =
                                            LoginResponse.error(LoginResponse.Error.WrongPassword);
                                else response = userResponses.get(request.getUsername());

                                responseCallback.onReceive(response);
                            }
                        })
                .start();
    }
}
