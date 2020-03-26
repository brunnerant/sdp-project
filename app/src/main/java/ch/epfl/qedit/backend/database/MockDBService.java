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

    private final CountingIdlingResource idlingResource;
    private final Map<String, BundledData> database;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        database = new HashMap<>();

        Question bananaQuestion =
                new Question("Banane", "Combien y a-t-il de bananes ?", new MatrixFormat(1, 1));

        database.put(
                "quizzes/quiz0",
                new Quiz(
                                Arrays.asList(
                                        new Question(
                                                "Banane",
                                                "Combien y a-t-il de bananes ?",
                                                new MatrixFormat(1, 1)),
                                        bananaQuestion,
                                        new Question(
                                                "The matches problem",
                                                "How many matches can fit in a shoe of size 43?",
                                                new MatrixFormat(3, 3)),
                                        new Question(
                                                "Pigeons",
                                                "How many pigeons are there on Earth? (Hint: do not count yourself)",
                                                new MatrixFormat(1, 1)),
                                        new Question("KitchenBu", "Oyster", new MatrixFormat(1, 1)),
                                        new Question(
                                                "Everything",
                                                "What is the answer to life the univere and everything ?",
                                                new MatrixFormat(3, 3)),
                                        new Question(
                                                "Banane",
                                                "Combien y a-t-il de bananes ?",
                                                new MatrixFormat(1, 1)),
                                        bananaQuestion,
                                        new Question(
                                                "Pomme",
                                                "Combien y a-t-il de pommes ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Abricot",
                                                "Combien y a-t-il d'abricots ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Cerise",
                                                "Combien y a-t-il de cerises ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Amande",
                                                "Combien y a-t-il d'amandes ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Ananas",
                                                "Combien y a-t-il d'ananas ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Avocat",
                                                "Combien y a-t-il d'avocats ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Citron",
                                                "Combien y a-t-il de citrons ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Clémentine",
                                                "Combien y a-t-il de clémentines ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Figue",
                                                "Combien y a-t-il de figues ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Fraise",
                                                "Combien y a-t-il de fraises ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Framboise",
                                                "Combien y a-t-il de framboises ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Kiwi",
                                                "Combien y a-t-il de kiwis ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Mandarine",
                                                "Combien y a-t-il de mandarines ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Melon",
                                                "Combien y a-t-il de melons ?",
                                                new MatrixFormat(1, 1)),
                                        new Question(
                                                "Noix",
                                                "Combien y a-t-il de noix ?",
                                                new MatrixFormat(1, 1))))
                        .toBundle());

        database.put("quizzes/quiz1", new Quiz(Arrays.asList(bananaQuestion)).toBundle());

        database.put("quizzes/quiz2", new Quiz(Arrays.asList(bananaQuestion)).toBundle());

        database.put("quizzes/quiz3", new Quiz(Arrays.asList(bananaQuestion)).toBundle());
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
