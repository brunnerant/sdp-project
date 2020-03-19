package ch.epfl.qedit.backend.database;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.model.MatrixFormat;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.BundledData;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MockDBService implements DatabaseService {

    private CountingIdlingResource idlingResource;
    private final Map<String, BundledData> database;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        database = new HashMap<>();

        database.put(
                "quizzes/quiz1",
                new Quiz(
                                Arrays.asList(
                                        new Question(
                                                "The matches problem",
                                                "How many matches can fit in a shoe of size 43?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Pigeons",
                                                "How many pigeons are there on Earth? (Hint: do not count yourself)",
                                                new MatrixFormat(1, 1)),
                                        new Question("KitchenBu", "Oyster", new MatrixFormat(1, 1)),
                                        new Question(
                                                "Everything",
                                                "What is the answer to life the univere and everything ?",
                                                new MatrixFormat(3, 3))))
                        .toBundle());
    }

    @Override
    public void getBundle(
            final String collection,
            final String document,
            final Callback<Response<BundledData>> responseCallback) {
        idlingResource.increment();
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Response<BundledData> response;
                                String key = collection + "/" + document;
                                if (!database.containsKey(key))
                                    response = Response.error(WRONG_DOCUMENT);
                                else response = Response.ok(database.get(key));

                                idlingResource.decrement();
                                responseCallback.onReceive(response);
                            }
                        })
                .start();
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }
}
