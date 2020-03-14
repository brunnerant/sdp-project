package ch.epfl.qedit.backend.auth;

import androidx.annotation.NonNull;

import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthService implements AuthenticationService {

    private FirebaseFirestore db;

    public FirebaseAuthService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    private User getUserFromDocument(DocumentSnapshot document) {

        String docRole = document.get("role", String.class);
        User.Role role = User.Role.Participant;

        if (docRole.equals(null)) role = User.Role.Participant;
        if (docRole.equals("admin")) role = User.Role.Administrator;
        if (docRole.equals("participant")) role = User.Role.Participant;
        if (docRole.equals("editor")) role = User.Role.Editor;

        return new User(
                document.get("first_name", String.class),
                document.get("last_name", String.class),
                role);
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
                                LoginResponse response;
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists())
                                        response = LoginResponse.ok(getUserFromDocument(document));
                                    else
                                        response =
                                                LoginResponse.error(LoginResponse.Error.WrongToken);
                                } else {
                                    response =
                                            LoginResponse.error(
                                                    LoginResponse.Error.ConnectionError);
                                    task.getException().printStackTrace();
                                }

                                responseCallback.onReceive(response);
                            }
                        });
    }
}
