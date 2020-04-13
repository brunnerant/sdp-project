package ch.epfl.qedit.backend.database;

import androidx.annotation.NonNull;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseDBService implements DatabaseService {

    private FirebaseFirestore db;

    public FirebaseDBService() {
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @param quizID String ID of the quiz in Firestore
     * @return a DocumentReference of the corresponding quiz in Firestore
     */
    private DocumentReference getQuizRef(String quizID) {
        return db.collection("quizzes").document(quizID);
    }

    /**
     * If a doc does not exist then its id does not describe an existing document in the database
     *
     * @param doc DocumentSnapshot of which we test the existence
     * @param responseCallback Callback function triggered with an error if the document does not
     *     exist
     * @param <T> This function is generic because we don't really need to know the type of response
     * @return true if doc exist, false otherwise, and response a WRONG_DOCUMENT error to the
     *     callback response.
     */
    private <T> boolean exists(DocumentSnapshot doc, Callback<Response<T>> responseCallback) {
        return Util.require(doc != null && doc.exists(), responseCallback, WRONG_DOCUMENT);
    }

    /**
     * If a collection does not exist then its id does not describe an existing collection in the
     * database
     *
     * @param collection QuerySnapshot of which we test its emptiness, in case of a collection, its
     *     existence
     * @param responseCallback Callback function triggered with an error if the collection does not
     *     exist
     * @param <T> This function is generic because we don't really need to know the type of response
     * @return true if collection exist, false otherwise, and response a WRONG_COLLECTION error to
     *     the callback response
     */
    private <T> boolean exists(QuerySnapshot collection, Callback<Response<T>> responseCallback) {
        return Util.require(
                collection != null && !collection.isEmpty(), responseCallback, WRONG_COLLECTION);
    }

    /**
     * If task is not successful, we don't retrieve any information with this query from the
     * database
     *
     * @param task Task of which we test if it is successful or not
     * @param responseCallback Callback function triggered with an error if the task is not
     *     successful
     * @param <T> This function is generic because we don't really need to know the type of response
     * @return true if the task is successful, false otherwise, and response a CONNECTION_ERROR
     *     error to the callback response
     */
    private <T> boolean isSuccessful(Task task, Callback<Response<T>> responseCallback) {
        return Util.require(task.isSuccessful(), responseCallback, CONNECTION_ERROR);
    }

    @Override
    public void getSupportedLanguage(
            String quizID, final Callback<Response<List<String>>> responseCallback) {

        getQuizRef(quizID)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (isSuccessful(task, responseCallback)) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (exists(doc, responseCallback)) {
                                        List<String> languages =
                                                Util.cast(doc.get("supported_languages"));
                                        if (Util.require(
                                                languages != null,
                                                responseCallback,
                                                WRONG_DOCUMENT)) {
                                            responseCallback.onReceive(Response.ok(languages));
                                        }
                                    }
                                }
                            }
                        });
    }

    @Override
    public void getStringPool(
            String quizID,
            String language,
            final Callback<Response<Map<String, String>>> responseCallback) {

        getQuizRef(quizID)
                .collection("string_pools")
                .document(language)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (isSuccessful(task, responseCallback)) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (exists(doc, responseCallback)) {
                                        Map<String, String> stringPool =
                                                Util.convertToStringPool(doc);
                                        responseCallback.onReceive(Response.ok(stringPool));
                                    }
                                }
                            }
                        });
    }

    @Override
    public void getQuizStructure(String quizID, final Callback<Response<Quiz>> responseCallback) {

        getQuizRef(quizID)
                .collection("questions")
                .orderBy("index")
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (isSuccessful(task, responseCallback)) {
                                    QuerySnapshot docs = task.getResult();
                                    if (exists(docs, responseCallback)) {
                                        Quiz quiz = Util.convertToQuiz(docs);
                                        responseCallback.onReceive(Response.ok(quiz));
                                    }
                                }
                            }
                        });
    }

    // ===========================================================================================================================

    @Override
    public void getQuizQuestions(
            String quizID, final Callback<Response<List<Question>>> responseCallback) {

        String language = Locale.getDefault().getLanguage();
        db.collection("quizzes")
                .document(quizID)
                .collection("questions_" + language)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Response<List<Question>> response;
                                if (isSuccessful(task, responseCallback)) {
                                    QuerySnapshot docs = task.getResult();
                                    if (exists(docs, responseCallback)) {

                                        // We get all questions store as document in firestore and
                                        // translate them as Question object

                                        List<Question> questions = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : docs) {
                                            try {
                                                questions.add(Util.convertToQuestion(doc));
                                            } catch (Exception e) {

                                                // If the translation raise an exception then the
                                                // format of the document is wrong, we print the
                                                // stack trace for debug purpose, and respond an
                                                // error

                                                e.printStackTrace();
                                                response = Response.error(WRONG_DOCUMENT);
                                                responseCallback.onReceive(response);
                                                return;
                                            }
                                        }
                                        response = Response.ok(questions);
                                        responseCallback.onReceive(response);
                                    }
                                }
                            }
                        });
    }

    @Override
    public void getQuizTitle(String quizID, final Callback<Response<String>> responseCallback) {

        final String language = Locale.getDefault().getLanguage();
        db.collection("quizzes")
                .document(quizID)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Response<String> response;
                                if (isSuccessful(task, responseCallback)) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (exists(doc, responseCallback)) {
                                        String title = doc.get("title_" + language, String.class);
                                        responseCallback.onReceive(Response.ok(title));
                                    }
                                }
                            }
                        });
    }

    @Override
    public void getQuiz(final String quizID, final Callback<Response<Quiz>> responseCallback) {

        // First we load the title from the database

        getQuizTitle(
                quizID,
                new Callback<Response<String>>() {
                    @Override
                    public void onReceive(final Response<String> titleResponse) {

                        // If we manage to extract the title

                        if (titleResponse.getError() == Response.NO_ERROR) {
                            final String title = titleResponse.getData();

                            // We try to extract the list of Questions
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

                                                // If we cannot load the questions, we respond the
                                                // error we get from getQuizTitle

                                                responseCallback.onReceive(
                                                        Response.<Quiz>error(
                                                                questionsResponse.getError()));
                                            }
                                        }
                                    });
                        } else {

                            // If we cannot load the title, we respond the error we get from
                            // getQuizTitle

                            responseCallback.onReceive(
                                    Response.<Quiz>error(titleResponse.getError()));
                        }
                    }
                });
    }
}
