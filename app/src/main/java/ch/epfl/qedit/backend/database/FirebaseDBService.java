package ch.epfl.qedit.backend.database;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
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
import java.util.List;

public class FirebaseDBService implements DatabaseService {

    private FirebaseFirestore db;

    public FirebaseDBService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    private Question getQuestionFromDoc(QueryDocumentSnapshot doc) throws Exception {

        String title = doc.get("title", String.class);
        if (title == null) throw new Exception("Title not found in question document");
        String text = doc.get("text", String.class);
        if (text == null) throw new Exception("Text not found in question document");
        String format = doc.get("answer_format", String.class);
        if (format == null) throw new Exception("Answer format not found in question document");

        return new Question(title, text, format);
    }

    @Override
    public void getQuizQuestions(
            String quizID, final Callback<Response<List<Question>>> responseCallback) {

        String language = "en";
        db.collection("quizzes")
                .document(quizID)
                .collection("questions_" + language)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Response<List<Question>> response;
                                if (task.isSuccessful()) {
                                    QuerySnapshot docs = task.getResult();
                                    if (docs != null && !docs.isEmpty()) {
                                        /**
                                         * We get all questions store as document in firestore and
                                         * translate them as Question object
                                         */
                                        List<Question> questions = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : docs) {
                                            try {
                                                questions.add(getQuestionFromDoc(doc));
                                            } catch (Exception e) {
                                                /**
                                                 * If the translation raise an exception then the
                                                 * format of the document is wrong, we print the
                                                 * stack trace for debug purpose, and response an
                                                 * error
                                                 */
                                                e.printStackTrace();
                                                response = Response.error(WRONG_DOCUMENT);
                                                responseCallback.onReceive(response);
                                                return;
                                            }
                                        }
                                        response = Response.ok(questions);
                                    } else {
                                        /**
                                         * If the QuerySnapshot is empty then the collection does
                                         * not exist because we don't support Quizzes with no
                                         * Questions
                                         */
                                        response = Response.error(WRONG_COLLECTION);
                                    }
                                } else {
                                    /**
                                     * If task is not successful, we don't retrieve any information
                                     * with this query from the database
                                     */
                                    response = Response.error(CONNECTION_ERROR);
                                }
                                responseCallback.onReceive(response);
                            }
                        });
    }

    @Override
    public void getQuizTitle(String quizID, final Callback<Response<String>> responseCallback) {

        final String language = "en";

        db.collection("quizzes")
                .document(quizID)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Response<String> response;
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String title =
                                                document.get("title_" + language, String.class);
                                        response = Response.ok(title);
                                    } else {
                                        /**
                                         * If the document does not exist then quizID does not
                                         * describe an existing quiz in the database
                                         */
                                        response = Response.error(WRONG_DOCUMENT);
                                    }
                                } else {
                                    /**
                                     * If task is not successful, we don't retrieve any information
                                     * with this query from the database
                                     */
                                    response = Response.error(CONNECTION_ERROR);
                                }
                                responseCallback.onReceive(response);
                            }
                        });
    }

    public void getQuiz(final String quizID, final Callback<Response<Quiz>> responseCallback) {
        /** First we load the title from the database */
        getQuizTitle(
                quizID,
                new Callback<Response<String>>() {
                    @Override
                    public void onReceive(final Response<String> titleResponse) {
                        /** If we manage to extract the title */
                        if (titleResponse.getError() == Response.NO_ERROR) {
                            final String title = titleResponse.getData();
                            /** We try to extract the list of Questions */
                            getQuizQuestions(
                                    quizID,
                                    new Callback<Response<List<Question>>>() {
                                        @Override
                                        public void onReceive(
                                                Response<List<Question>> questionsResponse) {
                                            if (questionsResponse.getError() == Response.NO_ERROR) {
                                                Quiz quiz =
                                                        new Quiz(
                                                                title, questionsResponse.getData());
                                                responseCallback.onReceive(Response.ok(quiz));
                                            } else {
                                                /**
                                                 * If we cannot load the questions, we response the
                                                 * error we get from getQuizTitle
                                                 */
                                                responseCallback.onReceive(
                                                        Response.<Quiz>error(
                                                                questionsResponse.getError()));
                                            }
                                        }
                                    });
                        } else {
                            /**
                             * If we cannot load the title, we response the error we get from
                             * getQuizTitle
                             */
                            responseCallback.onReceive(
                                    Response.<Quiz>error(titleResponse.getError()));
                        }
                    }
                });
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
