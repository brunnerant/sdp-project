package ch.epfl.qedit.model;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Server {
    private boolean isSignInSuccessful = false;
    private boolean isCreateSuccessful = false;
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
            firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    isSignInSuccessful = task.isSuccessful();
                                }
                            });
        } catch (Exception e) {
            isSignInSuccessful = false;
        }

        return isSignInSuccessful;
    }

    // TODO change with user
    public boolean createUser(String email, String password) {
        try {
            firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    isCreateSuccessful = task.isSuccessful();
                                }
                            });
        } catch (Exception e) {
            isCreateSuccessful = false;
        }
        return isCreateSuccessful;
    }

    public void setIsSignInSuccessful(boolean set) {
        isSignInSuccessful = set;
    }

    public void setIsCreateSuccessful(boolean set) {
        isCreateSuccessful = set;
    }

    public boolean getIsSignInSuccessful() {
        return isSignInSuccessful;
    }

    public boolean getIsCreateSuccessful() {
        return isCreateSuccessful;
    }

    // TODO: replace void return with User
    //    public void getUserProfile() {
    //
    //    }
}
