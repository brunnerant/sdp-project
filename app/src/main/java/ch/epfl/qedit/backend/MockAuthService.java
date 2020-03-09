package ch.epfl.qedit.backend;

import java.util.HashMap;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;

public class MockAuthService implements AuthenticationService {

    final private HashMap<String, String> usersPsw = new HashMap<String, String>(){{
        put("John", "1234");
        put("Jenna", "motdepasse");
    }};

    @Override
    public void sendRequest(final LoginRequest request, final Callback<LoginResponse> responseCallback) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(! usersPsw.containsKey(request.getUsername()))
                                    responseCallback.onReceive(
                                            LoginResponse.error(LoginResponse.Error.UserDoesNotExist));
                                else if (! usersPsw.get(request.getUsername()).equals(request.getPassword()))
                                    responseCallback.onReceive(
                                            LoginResponse.error(LoginResponse.Error.WrongPassword));
                                else
                                    responseCallback.onReceive(
                                        LoginResponse.ok(
                                                new User(request.getUsername(), "Doe", User.Role.Participant)));
                            }
                        })
                .start();
    }
}
