package ch.epfl.qedit.backend.auth;

import static ch.epfl.qedit.backend.Util.error;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.CompletableFuture;

public class FirebaseAuthService implements AuthenticationService {

    private final FirebaseAuth auth;

    public FirebaseAuthService() {
        // Access Firebase authentication service
        auth = FirebaseAuth.getInstance();
    }

    private void setCurrentUserId(CompletableFuture<String> future) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) error(future, "No current user connected");
        else future.complete(user.getUid());
    }

    @Override
    public CompletableFuture<String> signUp(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> setCurrentUserId(future))
                .addOnFailureListener(e -> error(future, e.getMessage()));

        return future;
    }

    @Override
    public CompletableFuture<String> logIn(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> setCurrentUserId(future))
                .addOnFailureListener(e -> error(future, e.getMessage()));

        return future;
    }
}
