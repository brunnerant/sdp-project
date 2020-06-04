package ch.epfl.qedit.backend.auth;

import static ch.epfl.qedit.backend.database.Util.error;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.CompletableFuture;

public class FirebaseAuthService implements AuthenticationService {

    private final FirebaseAuth auth;

    FirebaseAuthService() {
        // Access Firebase authentication service
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public String getUser() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    @Override
    public CompletableFuture<String> signUp(String email, String password) {
        return futureOnResult(auth.createUserWithEmailAndPassword(email, password));
    }

    @Override
    public CompletableFuture<String> logIn(String email, String password) {
        return futureOnResult(auth.signInWithEmailAndPassword(email, password));
    }

    private CompletableFuture<String> futureOnResult(Task<AuthResult> task) {
        CompletableFuture<String> future = new CompletableFuture<>();
        task.addOnSuccessListener(result -> setCurrentUserId(future))
                .addOnFailureListener(e -> error(future, e.getMessage()));
        return future;
    }

    private void setCurrentUserId(CompletableFuture<String> future) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) error(future, "No current user connected");
        else future.complete(user.getUid());
    }
}
