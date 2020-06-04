package ch.epfl.qedit.backend.auth;

import static ch.epfl.qedit.backend.database.Util.error;

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

    // Id of the currently logged-in user
    private String currentUser;

    public MockAuthService() {
        // increment the counter to get a new id
        idCounter = 1; // (we already have Cosme and Anthony in the database)
        users = new HashMap<>();
        users.put(new Pair<>("anthony@mock.test", "123456"), ANTHONY_IOZZIA_ID);
        users.put(new Pair<>("cosme@mock.test", "tree15"), COSME_JORDAN_ID);
        idlingResource = new CountingIdlingResource("MockAuthService");
        currentUser = null;
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    /** Simply make the current thread wait 0.5 seconds, to do as if the request takes time */
    private static void fakeWait() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** This allows the test cases to fake that a user was already logged-in */
    public void setUser(String user) {
        this.currentUser = user;
    }

    @Override
    public String getUser() {
        return currentUser;
    }

    @Override
    public CompletableFuture<String> signUp(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            fakeWait();
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
                .start();

        return future;
    }

    @Override
    public CompletableFuture<String> logIn(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            fakeWait();
                            Pair<String, String> info = new Pair<>(email, password);
                            String id = users.get(info);
                            if (id == null) error(future, "Authentication fail");
                            else {
                                currentUser = id;
                                future.complete(id);
                            }

                            idlingResource.decrement();
                        })
                .start();

        return future;
    }
}
