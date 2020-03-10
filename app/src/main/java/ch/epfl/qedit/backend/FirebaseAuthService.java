package ch.epfl.qedit.backend;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthService implements AuthenticationService {

    // Access a Cloud Firestore instance
    private FirebaseFirestore db;

    public FirebaseAuthService() {
        db = FirebaseFirestore.getInstance();
    }

    private User getUserFromDocument(DocumentSnapshot document){
        return new User(
                document.get("first_name", String.class),
                document.get("last_name", String.class),
                getRole(document.get("role", String.class)));
    }

    @Override
    public void sendRequest(String token, final Callback<LoginResponse> responseCallback) {

        db.collection("users")
                .document(token)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        responseCallback.onReceive(
                                                LoginResponse.ok(
                                                        getUserFromDocument(document)));
                                    } else {
                                        responseCallback.onReceive(
                                                LoginResponse.error(
                                                        LoginResponse.Error.WrongToken));
                                    }
                                } else {
                                    //task.getException().printStackTrace();
                                    responseCallback.onReceive(
                                            LoginResponse.error(
                                                    LoginResponse.Error.ConnectionError));
                                }
                            }
                        });
    }

    private User.Role getRole(String role) {
        if (role.equals(null)) return null;
        if (role.equals("admin")) return User.Role.Administrator;
        if (role.equals("participant")) return User.Role.Participant;
        if (role.equals("editor")) return User.Role.Editor;
        else return null;
    }
}
