package ch.epfl.qedit.backend.database;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.AnswerFormat;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirestoreDBService implements DatabaseService {

    private FirebaseFirestore db;

    public FirestoreDBService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    /** Parse AnswerFormat from the string answer_format extract from the database */
    private AnswerFormat parseFormat(String format) throws Exception {

        Matcher matrixMatcher = Pattern.compile("^matrix(\\d+)x(\\d+)$").matcher(format);

        /** Match format: 'matrixNxM' where N and M are [0-9]+ */
        if (matrixMatcher.find()) {
            /** Extract the row and column size */
            Matcher digit = Pattern.compile("(\\d+)").matcher(format);
            digit.find();
            int i = Integer.parseInt(digit.group(1));
            digit.find();
            int j = Integer.parseInt(digit.group(1));
            return new MatrixFormat(i, j);
        } else {
            throw new Exception("Invalid answer format.");
        }
    }

    private Question getQuestionFromDoc(Map<String, Object> doc) throws Exception {

        Object title = doc.get("title");
        if (title == null) throw new Exception("Title not found in question document");
        Object text = doc.get("text");
        if (text == null) throw new Exception("Text not found in question document");
        Object format = doc.get("answer_format");
        if (format == null) throw new Exception("Answer format not found in question document");

        return new Question((String) title, (String) text, parseFormat((String) format));
    }

    @Override
    public void getQuizQuestions(
            String quizID, final Callback<Response<BundledData>> responseCallback) {
        String language = "en";
        db.collection("quizzes")
                .document(quizID)
                .collection("questions_" + language)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Response<BundledData> response;
                                if (task.isSuccessful()) {
                                    QuerySnapshot docs = task.getResult();
                                    if (docs != null && !docs.isEmpty()) {
                                        ArrayList<Question> questions = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : docs) {
                                            try {
                                                questions.add(getQuestionFromDoc(doc.getData()));
                                            } catch (Exception e) {
                                                e.printStackTrace(); // TODO
                                            }
                                        }
                                        response =
                                                Response.ok(
                                                        new BundledData("questions", questions));
                                    } else {
                                        response = Response.error(WRONG_COLLECTION);
                                    }
                                } else response = Response.error(CONNECTION_ERROR);
                                responseCallback.onReceive(response);
                            }
                        });
    }

    @Override
    public void getQuizTitle(
            String quizID, final Callback<Response<BundledData>> responseCallback) {}

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
