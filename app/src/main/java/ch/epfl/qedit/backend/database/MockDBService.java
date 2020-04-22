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
import java.util.Map;

public class MockDBService implements DatabaseService {

    // FOR NOW, WE KEEP VERSION 1 (V1) AND getQuiz(...), getQuestions(...), getQuizTitle(...)
    // THIS WILL BE DELETED IN A FURTHER PR, AND ALL 'V2' OCCURRENCE NEED TO BE DELETED

    // TODO DELETE IN FURTHER PR
    // ========================================================================================
    /** Simulate a Quiz store in firestore database */
    private static class MockQuizV1 {
        private String title_en;
        private String title_fr;
        private ImmutableList<Question> questions_fr;
        private ImmutableList<Question> questions_en;

        MockQuizV1(
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

        public static MockQuizV1 createTestMockQuiz1() {
            List<Question> q_en =
                    Arrays.asList(
                            new Question(
                                    "Banana", "How many bananas are there on Earth?", "matrix1x1"),
                            new Question(
                                    "Apple", "How many apples are there on Earth?", "matrix1x1"),
                            new Question("Vector", "Give a unit vector.", "matrix1x3"),
                            new Question(
                                    "Operation", "What is the results of 1 + 10?", "matrix1x1"),
                            new Question("Matrix", "Fill this matrix.", "matrix3x3"));

            List<Question> q_fr =
                    Arrays.asList(
                            new Question(
                                    "Banane",
                                    "Combien y a-t-il de bananes sur Terre ?",
                                    "matrix1x1"),
                            new Question(
                                    "Pomme", "Combien y a-t-il de pommes sur Terre ?", "matrix1x1"),
                            new Question("Vecteur", "Donnez un vecteur unitaire.", "matrix1x3"),
                            new Question(
                                    "Operation", "Quel est le résultat de 1 + 10 ?", "matrix1x1"),
                            new Question("Matrice", "Remplissez cette matrice.", "matrix3x3"));

            return new MockQuizV1("I am a Mock Quiz!", "Je suis un Mock Quiz !", q_en, q_fr);
        }

        public static MockQuizV1 createTestMockQuiz2() {
            Question bananaQuestion_fr =
                    new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1");
            Question bananaQuestion_en =
                    new Question("Banana", "How many bananas can you count?", "matrix1x1");

            return new MockQuizV1(
                    "Title",
                    "Titre",
                    Arrays.asList(bananaQuestion_en),
                    Arrays.asList(bananaQuestion_fr));
        }
    }
    // ========================================================================================
    /** Simulate a Quiz store in firestore database */
    private static class MockQuizV2 {

        private ImmutableList<Question> questions;
        private HashMap<String, HashMap<String, String>> stringPools;

        MockQuizV2(List<Question> questions, HashMap<String, HashMap<String, String>> stringPools) {
            this.questions = ImmutableList.copyOf(questions);
            this.stringPools = stringPools;
        }

        public List<Question> getQuestions() {
            return new ArrayList<>(questions);
        }

        List<String> getLanguages() {
            return new ArrayList<>(stringPools.keySet());
        }

        HashMap<String, String> getStringPool(String language) {
            return stringPools.get(language);
        }

        static MockQuizV2 createTestMockQuiz1() {

            HashMap<String, String> stringPool_en = new HashMap<>();
            stringPool_en.put("main_title", "I am a Mock Quiz!");
            stringPool_en.put("q1_title", "Banana");
            stringPool_en.put("q1_text", "How many bananas are there on Earth?");
            stringPool_en.put("q2_title", "Apple");
            stringPool_en.put("q2_text", "How many apples are there on Earth?");
            stringPool_en.put("q3_title", "Vector");
            stringPool_en.put("q3_text", "Give a unit vector.");
            stringPool_en.put("q4_title", "Operation");
            stringPool_en.put("q4_text", "What is the results of 1 + 10?");
            stringPool_en.put("q5_title", "Matrix");
            stringPool_en.put("q5_text", "Fill this matrix.");

            HashMap<String, String> stringPool_fr = new HashMap<>();
            stringPool_en.put("main_title", "Je suis un Mock Quiz !");
            stringPool_fr.put("q1_title", "Banane");
            stringPool_fr.put("q1_text", "Combien y a-t-il de bananes sur Terre ?");
            stringPool_fr.put("q2_title", "Pomme");
            stringPool_fr.put("q2_text", "Combien y a-t-il de pommes sur Terre ?");
            stringPool_fr.put("q3_title", "Vecteur");
            stringPool_fr.put("q3_text", "Donnez un vecteur unitaire.");
            stringPool_fr.put("q4_title", "Operation");
            stringPool_fr.put("q4_text", "Quel est le résultat de 1 + 10 ?");
            stringPool_fr.put("q5_title", "Matrice");
            stringPool_fr.put("q5_text", "Remplissez cette matrice.");

            HashMap<String, HashMap<String, String>> stringPools = new HashMap<>();
            stringPools.put("en", stringPool_en);
            stringPools.put("fr", stringPool_fr);

            List<Question> questions =
                    Arrays.asList(
                            new Question("q1_title", "q1_text", "matrix1x1"),
                            new Question("q2_title", "q2_text", "matrix1x1"),
                            new Question("q3_title", "q3_text", "matrix1x3"),
                            new Question("q4_title", "q4_text", "matrix1x1"),
                            new Question("q5_title", "q5_text", "matrix3x3"));

            return new MockQuizV2(questions, stringPools);
        }

        static MockQuizV2 createTestMockQuiz2() {

            HashMap<String, String> stringPool_en = new HashMap<>();
            stringPool_en.put("main_title", "Title");
            stringPool_en.put("q1_title", "Banana");
            stringPool_en.put("q1_text", "How many bananas are there on Earth?");

            HashMap<String, String> stringPool_fr = new HashMap<>();
            stringPool_fr.put("main_title", "Titre");
            stringPool_fr.put("q1_title", "Banane");
            stringPool_fr.put("q1_text", "Combien y a-t-il de bananes sur Terre ?");

            HashMap<String, HashMap<String, String>> stringPools = new HashMap<>();
            stringPools.put("en", stringPool_en);
            stringPools.put("fr", stringPool_fr);

            List<Question> questions =
                    Arrays.asList(new Question("q1_title", "q1_text", "matrix1x1"));

            return new MockQuizV2(questions, stringPools);
        }
    }

    // TODO DELETE IN FURTHER PR
    // ========================================================================================
    private HashMap<String, MockQuizV1> dbV1;
    // ========================================================================================

    private HashMap<String, MockQuizV2> dbV2;
    private CountingIdlingResource idlingResource;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");

        // TODO DELETE IN FURTHER PR
        dbV1 = new HashMap<>();
        dbV1.put("quiz0", MockQuizV1.createTestMockQuiz1());
        dbV1.put("quiz1", MockQuizV1.createTestMockQuiz2());
        dbV1.put("quiz2", MockQuizV1.createTestMockQuiz2());
        dbV1.put("quiz3", MockQuizV1.createTestMockQuiz2());

        dbV2 = new HashMap<>();
        dbV2.put("quiz0", MockQuizV2.createTestMockQuiz1());
        dbV2.put("quiz1", MockQuizV2.createTestMockQuiz2());
        dbV2.put("quiz2", MockQuizV2.createTestMockQuiz2());
        dbV2.put("quiz3", MockQuizV2.createTestMockQuiz2());
    }

    /** Simply make the current thread wait 2 second */
    private void wait2second() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the database contains the quiz identify by quizID
     *
     * @param quizID the unique identifier of the quiz we are searching
     * @param responseCallback Callback function triggered with an error if the quiz does not exist
     * @param <T> This function is generic because we don't really need to know the type of response
     * @return true if the quiz exists, return false otherwise and triggered responseCallback with a
     *     WRONG_DOCUMENT error
     */
    private <T> boolean exists(String quizID, Callback<Response<T>> responseCallback) {
        return Util.require(dbV2.get(quizID) != null, responseCallback, WRONG_DOCUMENT);
    }

    @Override
    public void getQuizLanguages(
            final String quizID, final Callback<Response<List<String>>> responseCallback) {
        idlingResource.increment();
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                wait2second();

                                if (exists(quizID, responseCallback)) {
                                    List<String> supportedLanguage =
                                            dbV2.get(quizID).getLanguages();
                                    responseCallback.onReceive(Response.ok(supportedLanguage));
                                }
                                idlingResource.decrement();
                            }
                        })
                .start();
    }

    @Override
    public void getQuizStringPool(
            final String quizID,
            final String language,
            final Callback<Response<Map<String, String>>> responseCallback) {
        idlingResource.increment();
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                wait2second();

                                if (exists(quizID, responseCallback)) {
                                    Map<String, String> stringPool =
                                            dbV2.get(quizID).getStringPool(language);
                                    responseCallback.onReceive(Response.ok(stringPool));
                                }
                                idlingResource.decrement();
                            }
                        })
                .start();
    }

    @Override
    public void getQuizStructure(
            final String quizID, final Callback<Response<Quiz>> responseCallback) {

        idlingResource.increment();
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                wait2second();

                                if (exists(quizID, responseCallback)) {
                                    Quiz stringPool =
                                            new Quiz("main_title", dbV2.get(quizID).getQuestions());
                                    responseCallback.onReceive(Response.ok(stringPool));
                                }
                                idlingResource.decrement();
                            }
                        })
                .start();
    }

    // TODO DELETE IN FURTHER PR
    // ========================================================================================
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
                                if (!dbV1.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    List<Question> questions =
                                            new ArrayList<>(
                                                    dbV1.get(quizID)
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
                                if (!dbV1.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    response =
                                            Response.ok(
                                                    dbV1.get(quizID)
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
                                if (!dbV1.containsKey(quizID))
                                    response = Response.error(WRONG_DOCUMENT);
                                else {
                                    List<Question> questions =
                                            new ArrayList<>(
                                                    dbV1.get(quizID)
                                                            .getQuestions(
                                                                    Locale.getDefault()
                                                                            .getLanguage()));
                                    response =
                                            Response.ok(
                                                    new Quiz(
                                                            dbV1.get(quizID)
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
    // ========================================================================================
}
