package ch.epfl.qedit.model;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Server {
    private FirebaseAuth firebaseAuth = null;
    private static Server server = null;
    private boolean isOut = false;
    private FirebaseUser firebaseUser;

    private Server() {}

    public void initServer() {
        // Initialize FirebaseAuth
        try {
            firebaseAuth = FirebaseAuth.getInstance();
        } catch (Error e) {
            System.err.println("Can't connect to server");
        }
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }

        return server;
    }

    public boolean isOut() {
        return isOut;
    }

    public void signOut() {
        try {
            firebaseAuth.signOut();
        } catch (Exception e) {
        }

        isOut = true;
    }

    //    public void signUpNewUser(User user) {
    //    }

    //    public void child() {
    //
    //    }

    // TODO change with user
    public boolean signIn(String email, String password) {

        try {
            return firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {}
                            })
                    .isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    // TODO change with user
    public boolean createUser(String email, String password) {
        try {
            return firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {}
                            })
                    .isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    // TODO: replace void return with User
    //    public void getUserProfile() {
    //
    //    }
}
