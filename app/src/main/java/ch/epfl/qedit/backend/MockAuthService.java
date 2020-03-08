package ch.epfl.qedit.backend;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;

public class MockAuthService implements AuthenticationService {
    @Override
    public void sendRequest(LoginRequest request, final Callback<LoginResponse> responseCallback) {
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                responseCallback.onReceive(
                                        LoginResponse.ok(
                                                new User("John", "Doe", User.Role.Participant)));
                            }
                        })
                .start();
    }
}
