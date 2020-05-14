package ch.epfl.qedit.backend.auth;

import static ch.epfl.qedit.backend.Util.error;

import android.util.Pair;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MockAuthService implements AuthenticationService {

    private final CountingIdlingResource idlingResource;
    private int idCounter;

    // map of <email, password> to userId
    private Map<Pair<String, String>, String> users;

    public MockAuthService() {
        idCounter = 0;
        users = new HashMap<>();
        idlingResource = new CountingIdlingResource("MockAuthService");
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    /** Simply make the current thread wait 2 second */
    private static void wait2second() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<String> signUp(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            wait2second();
                            Pair<String, String> info = new Pair<>(email, password);
                            if (users.containsKey(info)) error(future, "Sign up fail");
                            else {
                                String newId = Integer.toString(++idCounter);
                                users.put(info, newId);
                                future.complete(newId);
                            }
                            idlingResource.decrement();
                        })
                .run();

        return future;
    }

    @Override
    public CompletableFuture<String> logIn(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            wait2second();
                            Pair<String, String> info = new Pair<>(email, password);
                            String id = users.get(info);
                            if (id == null) error(future, "Authentication fail");
                            else future.complete(id);
                            idlingResource.decrement();
                        })
                .run();

        return future;
    }
}
