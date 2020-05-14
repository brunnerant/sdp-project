package ch.epfl.qedit.backend.auth;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthService implements AuthenticationService {

    private final FirebaseFirestore db;

    public FirebaseAuthService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    private User getUserFromDocument(DocumentSnapshot document) {
        return new User(
                document.get("first_name", String.class), document.get("last_name", String.class));
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
                                    else response = Response.error(CONNECTION_ERROR);
                                } else {
                                    response = Response.error(CONNECTION_ERROR);
                                }

                                responseCallback.onReceive(response);
                            }
                        });
    }
}
