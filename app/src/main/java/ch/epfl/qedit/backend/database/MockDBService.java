package ch.epfl.qedit.backend.database;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.model.AnswerFormat;
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
                                                "Banane",
                                                "Combien y a-t-il de bananes ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Pomme",
                                                "Combien y a-t-il de pommes ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Abricot",
                                                "Combien y a-t-il d'abricots ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Cerise",
                                                "Combien y a-t-il de cerises ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Amande",
                                                "Combien y a-t-il d'amandes ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Ananas",
                                                "Combien y a-t-il d'ananas ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Avocat",
                                                "Combien y a-t-il d'avocats ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Citron",
                                                "Combien y a-t-il de citrons ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Clémentine",
                                                "Combien y a-t-il de clémentines ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Figue",
                                                "Combien y a-t-il de figues ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Fraise",
                                                "Combien y a-t-il de fraises ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Framboise",
                                                "Combien y a-t-il de framboises ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Kiwi",
                                                "Combien y a-t-il de kiwis ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Mandarine",
                                                "Combien y a-t-il de mandarines ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Melon",
                                                "Combien y a-t-il de melons ?",
                                                new AnswerFormat.NumberField(0, 1, 5)),
                                        new Question(
                                                "Noix",
                                                "Combien y a-t-il de noix ?",
                                                new AnswerFormat.NumberField(0, 1, 5))
                                        ))
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
