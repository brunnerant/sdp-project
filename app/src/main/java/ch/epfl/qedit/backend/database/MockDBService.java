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
import java.util.Locale;

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

        public String getTitle(String language) {
            if (language.equals("en")) {
                return title_en;
            } else {
                return title_fr;
            }
        }

        public ImmutableList<Question> getQuestions(String language) {
            if (language.equals("en")) {
                return questions_en;
            } else {
                return questions_fr;
            }
        }
    }

    private HashMap<String, MockQuiz> db;
    private CountingIdlingResource idlingResource;

    @SuppressWarnings("SpellCheckingInspection")
    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        db = new HashMap<>();
        Question bananaQuestion_fr =
                new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1");
        Question bananaQuestion_en =
                new Question("Banana", "How many bananas can you count ?", "matrix1x1");
        List<Question> q_en =
                Arrays.asList(
                        new Question("Banana", "How many bananas is there on earth ?", "matrix1x1"),
                        new Question("Apple", "How many apples is there on earth ?", "matrix1x1"),
                        new Question("Vector", "Qive a unit vecor ? ", "matrix1x3"),
                        new Question("Operation", "What is the results of 1 + 10 ?", "matrix1x1"),
                        new Question("Matrix", "Fill this matrix ?", "matrix3x3"));

        List<Question> q_fr =
                Arrays.asList(
                        new Question(
                                "Banane", "Combien y a t'il de bananes sur terre ?", "matrix1x1"),
                        new Question(
                                "Pomme", "Combien y a t'il de pommes sur terre ?", "matrix1x1"),
                        new Question("Vecteur", "Donnez un vecteur unitaire ?", "matrix1x3"),
                        new Question("Operation", "Quel est le resultat de 1 + 10 ?", "matrix1x1"),
                        new Question("Matrice", "Remplissez cette matrice ?", "matrix3x3"));

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
                                            new ArrayList<>(
                                                    db.get(quizID)
                                                            .getQuestions(
                                                                    Locale.getDefault()
                                                                            .getLanguage()));
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
                                    response =
                                            Response.ok(
                                                    db.get(quizID)
                                                            .getTitle(
                                                                    Locale.getDefault()
                                                                            .getLanguage()));
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
                                            new ArrayList<>(
                                                    db.get(quizID)
                                                            .getQuestions(
                                                                    Locale.getDefault()
                                                                            .getLanguage()));
                                    response =
                                            Response.ok(
                                                    new Quiz(
                                                            db.get(quizID)
                                                                    .getTitle(
                                                                            Locale.getDefault()
                                                                                    .getLanguage()),
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
