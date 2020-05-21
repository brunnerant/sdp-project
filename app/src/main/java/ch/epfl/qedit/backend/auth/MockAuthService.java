package ch.epfl.qedit.backend.auth;

import static ch.epfl.qedit.backend.Util.error;

import android.util.Pair;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MockAuthService implements AuthenticationService {

    // public in order to use it in the MockDBService
    public static final String ANTHONY_IOZZIA_ID = "0";
    public static final String COSME_JORDAN_ID = "1";

    private final CountingIdlingResource idlingResource;
    private int idCounter;

    // map of <email, password> to userId
    private Map<Pair<String, String>, String> users;

    public MockAuthService() {
        // increment the counter to get a new id
        idCounter = 1; // (we already have Cosme and Anthony in the database)
        users = new HashMap<>();
        users.put(new Pair<>("anthony@mock.test", "123456"), ANTHONY_IOZZIA_ID);
        users.put(new Pair<>("cosme@mock.test", "tree15"), COSME_JORDAN_ID);
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
                            // sign up fail if email password is already in authentication service
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
