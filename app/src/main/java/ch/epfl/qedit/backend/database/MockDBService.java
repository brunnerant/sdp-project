package ch.epfl.qedit.backend.database;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MockDBService implements DatabaseService {

    public static class MockQuiz {
        private String title_en;
        private String title_fr;
        private ImmutableList<Question> questions_fr;
        private ImmutableList<Question> questions_en;

        MockQuiz(
                String title_en,
                String title_fr,
                List<Question> questions_en,
                List<Question> questions_fr) {
            this.title_en = title_en;
            this.title_fr = title_fr;
            this.questions_fr = ImmutableList.copyOf(questions_fr);
            this.questions_en = ImmutableList.copyOf(questions_en);
        }

        public String getTitle_en() {
            return title_en;
        }

        public String getTitle_fr() {
            return title_fr;
        }

        public ImmutableList<Question> getQuestions_fr() {
            return questions_fr;
        }

        public ImmutableList<Question> getQuestions_en() {
            return questions_en;
        }
    }

    private HashMap<String, MockQuiz> db;
    private CountingIdlingResource idlingResource;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        db = new HashMap<>();
        Question bananaQuestion_fr =
                new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1");
        Question bananaQuestion_en =
                new Question("Banana", "How many banana can you count ?", "matrix1x1");
        List<Question> q_en =
                Arrays.asList(
                        new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1"),
                        bananaQuestion_en,
                        new Question(
                                "The matches problem",
                                "How many matches can fit in a shoe of size 43 ?",
                                "matrix3x3"),
                        new Question(
                                "Pigeons",
                                "How many pigeons are there on Earth ? (Hint: do not count yourself)",
                                "matrix1x1"),
                        new Question("KitchenBu", "Oyster", "matrix1x1"),
                        new Question(
                                "Everything",
                                "What is the answer to life the univere and everything ?",
                                "matrix3x3"),
                        new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1"),
                        bananaQuestion_en,
                        new Question("Pomme", "Combien y a-t-il de pommes ?", "matrix1x1"),
                        new Question("Abricot", "Combien y a-t-il d'abricots ?", "matrix1x1"),
                        new Question("Cerise", "Combien y a-t-il de cerises ?", "matrix1x1"),
                        new Question("Amande", "Combien y a-t-il d'amandes ?", "matrix1x1"),
                        new Question("Ananas", "Combien y a-t-il d'ananas ?", "matrix1x1"),
                        new Question("Avocat", "Combien y a-t-il d'avocats ?", "matrix1x1"),
                        new Question("Citron", "Combien y a-t-il de citrons ?", "matrix1x1"),
                        new Question(
                                "Clémentine", "Combien y a-t-il de clémentines ?", "matrix1x1"),
                        new Question("Figue", "Combien y a-t-il de figues ?", "matrix1x1"),
                        new Question("Fraise", "Combien y a-t-il de fraises ?", "matrix1x1"),
                        new Question("Framboise", "Combien y a-t-il de framboises ?", "matrix1x1"),
                        new Question("Kiwi", "Combien y a-t-il de kiwis ?", "matrix1x1"),
                        new Question("Mandarine", "Combien y a-t-il de mandarines ?", "matrix1x1"),
                        new Question("Melon", "Combien y a-t-il de melons ?", "matrix1x1"),
                        new Question("Noix", "Combien y a-t-il de noix ?", "matrix1x1"));

        List<Question> q_fr = new ArrayList<>();

        db.put("quiz0", new MockQuiz("I am a Mock Quiz!", "Je suis un Mock Quiz!", q_en, q_fr));
        db.put(
                "quiz1",
                new MockQuiz(
                        "Title",
                        "Titre",
                        Arrays.asList(bananaQuestion_en),
                        Arrays.asList(bananaQuestion_fr)));
        db.put(
                "quiz2",
                new MockQuiz(
                        "Title",
                        "Titre",
                        Arrays.asList(bananaQuestion_en),
                        Arrays.asList(bananaQuestion_fr)));
        db.put(
                "quiz3",
                new MockQuiz(
                        "Title",
                        "Titre",
                        Arrays.asList(bananaQuestion_en),
                        Arrays.asList(bananaQuestion_fr)));
    }

    @Override
    public void getQuizQuestions(
            final String quizID, final Callback<Response<List<Question>>> responseCallback) {

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
                                Response<List<Question>> response;
                                if (!db.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    List<Question> questions =
                                            new ArrayList<>(db.get(quizID).getQuestions_en());
                                    response = Response.ok(questions);
                                }
                                idlingResource.decrement();
                                responseCallback.onReceive(response);
                            }
                        })
                .start();
    }

    @Override
    public void getQuizTitle(
            final String quizID, final Callback<Response<String>> responseCallback) {

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
                                Response<String> response;
                                if (!db.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    response = Response.ok(db.get(quizID).getTitle_en());
                                }
                                idlingResource.decrement();
                                responseCallback.onReceive(response);
                            }
                        })
                .start();
    }

    @Override
    public void getQuiz(final String quizID, final Callback<Response<Quiz>> responseCallback) {
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
                                Response<Quiz> response;
                                if (!db.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    List<Question> questions =
                                            new ArrayList<>(db.get(quizID).getQuestions_en());
                                    response =
                                            Response.ok(
                                                    new Quiz(
                                                            db.get(quizID).getTitle_en(),
                                                            questions));
                                }
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
