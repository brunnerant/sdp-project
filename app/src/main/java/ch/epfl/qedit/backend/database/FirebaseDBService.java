package ch.epfl.qedit.backend.database;

import androidx.annotation.NonNull;
import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class FirebaseDBService implements DatabaseService {

    private final FirebaseFirestore db;

    public FirebaseDBService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getBundle(
            final String collection,
            String document,
            final Callback<Response<BundledData>> responseCallback) {
        db.collection(collection)
                .document(document)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Response<BundledData> response;
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists())
                                        response = Response.ok(new BundledData(document.getData()));
                                    else response = Response.error(WRONG_DOCUMENT);
                                } else response = Response.error(CONNECTION_ERROR);
                                responseCallback.onReceive(response);
                            }
                        });
    }
}
