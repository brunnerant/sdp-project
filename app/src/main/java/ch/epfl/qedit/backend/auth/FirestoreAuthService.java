package ch.epfl.qedit.backend.auth;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class FirestoreAuthService implements AuthenticationService {

    private final FirebaseFirestore db;

    public FirestoreAuthService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    private User getUserFromDocument(DocumentSnapshot document) {

        String docRole = document.get("role", String.class);
        User.Role role;

        if (docRole == null || docRole.equals("participant")) role = User.Role.Participant;
        else if (docRole.equals("admin")) role = User.Role.Administrator;
        else if (docRole.equals("editor")) role = User.Role.Editor;
        else role = User.Role.Participant;

        return new User(
                document.get("first_name", String.class),
                document.get("last_name", String.class),
                role);
    }

    @Override
    public void sendRequest(String token, final Callback<Response<User>> responseCallback) {
        db.collection("users")
                .document(token)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Response<User> response;
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists())
                                        response = Response.ok(getUserFromDocument(document));
                                    else response = Response.error(WRONG_TOKEN);
                                } else {
                                    response = Response.error(CONNECTION_ERROR);
                                }

                                responseCallback.onReceive(response);
                            }
                        });
    }
}
