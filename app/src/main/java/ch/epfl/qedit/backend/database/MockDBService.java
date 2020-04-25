package ch.epfl.qedit.backend.database;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.answer.MatrixFormat;

public class MockDBService implements DatabaseService {
    /** This class simulates a quiz that is stored in Firestore */
    private static class MockQuiz {

        private ImmutableList<Question> questions;
        private Map<String, StringPool> stringPools;

        MockQuiz(List<Question> questions, Map<String, StringPool> stringPools) {
            this.questions = ImmutableList.copyOf(questions);
            this.stringPools = stringPools;
        }

        public List<Question> getQuestions() {
            return new ArrayList<>(questions);
        }

        List<String> getLanguages() {
            return new ArrayList<>(stringPools.keySet());
        }

        StringPool getStringPool(String language) {
            return stringPools.get(language);
        }

        static final MatrixFormat simpleFormat =
                MatrixFormat.singleField(
                        MatrixFormat.Field.textField("hint1", MatrixFormat.Field.NO_LIMIT));
        static final MatrixFormat compoundFormat =
                new MatrixFormat.Builder(2, 2)
                        .withField(0, 0, MatrixFormat.Field.preFilledField("hint2"))
                        .withField(0, 1, MatrixFormat.Field.numericField(false, true, "hint3", 4))
                        .withField(1, 0, MatrixFormat.Field.textField("hint4", 16))
                        .withField(
                                1,
                                1,
                                MatrixFormat.Field.numericField(
                                        true, false, "hint5", MatrixFormat.Field.NO_LIMIT))
                        .build();

        static MockQuiz createTestMockQuiz1() {
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
            stringPool_en.put("hint1", "text field");
            stringPool_en.put("hint2", "pre-filled field");
            stringPool_en.put("hint3", "signed int field");
            stringPool_en.put("hint4", "text field");
            stringPool_en.put("hint5", "unsigned float field");

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
            stringPool_fr.put("hint1", "champ texte");
            stringPool_fr.put("hint2", "champ pré-rempli");
            stringPool_fr.put("hint3", "champ entier signé");
            stringPool_fr.put("hint4", "champ texte");
            stringPool_fr.put("hint5", "champ décimal non-signé");

            HashMap<String, StringPool> stringPools = new HashMap<>();
            stringPools.put("en", new StringPool(stringPool_en));
            stringPools.put("fr", new StringPool(stringPool_fr));

            List<Question> questions =
                    Arrays.asList(
                            new Question("q1_title", "q1_text", simpleFormat),
                            new Question("q2_title", "q2_text", compoundFormat),
                            new Question("q3_title", "q3_text", simpleFormat),
                            new Question("q4_title", "q4_text", compoundFormat),
                            new Question("q5_title", "q5_text", simpleFormat));

            return new MockQuiz(questions, stringPools);
        }

        static MockQuiz createTestMockQuiz2() {
            HashMap<String, String> stringPool_en = new HashMap<>();
            stringPool_en.put("main_title", "Title");
            stringPool_en.put("q1_title", "Banana");
            stringPool_en.put("q1_text", "How many bananas are there on Earth?");
            stringPool_en.put("hint1", "text field");

            HashMap<String, String> stringPool_fr = new HashMap<>();
            stringPool_fr.put("main_title", "Titre");
            stringPool_fr.put("q1_title", "Banane");
            stringPool_fr.put("q1_text", "Combien y a-t-il de bananes sur Terre ?");
            stringPool_fr.put("hint1", "champ texte");

            HashMap<String, StringPool> stringPools = new HashMap<>();
            stringPools.put("en", new StringPool(stringPool_en));
            stringPools.put("fr", new StringPool(stringPool_fr));

            List<Question> questions =
                    Arrays.asList(new Question("q1_title", "q1_text", simpleFormat));

            return new MockQuiz(questions, stringPools);
        }
    }

    private HashMap<String, MockQuiz> quizzes;
    private CountingIdlingResource idlingResource;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        quizzes = new HashMap<>();
        quizzes.put("quiz0", MockQuiz.createTestMockQuiz1());
        quizzes.put("quiz1", MockQuiz.createTestMockQuiz2());
        quizzes.put("quiz2", MockQuiz.createTestMockQuiz2());
        quizzes.put("quiz3", MockQuiz.createTestMockQuiz2());
    }

    /** Simply make the current thread wait 2 second */
    private static void wait2second() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private <T> void waitForQuiz(
            CompletableFuture<T> future, String quizId, Function<MockQuiz, T> f) {
        idlingResource.increment();

        new Thread(
                () -> {
                    wait2second();
                    MockQuiz quiz = quizzes.get(quizId);
                    if (quiz == null)
                        future.completeExceptionally(new Util.RequestException("Invalid quiz id"));
                    else future.complete(f.apply(quiz));
                    idlingResource.decrement();
                });
    }

    @Override
    public CompletableFuture<List<String>> getQuizLanguages(final String quizId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        waitForQuiz(future, quizId, MockQuiz::getLanguages);
        return future;
    }

    @Override
    public CompletableFuture<Quiz> getQuizStructure(String quizId) {
        CompletableFuture<Quiz> future = new CompletableFuture<>();
        waitForQuiz(future, quizId, mockQuiz -> new Quiz("main_title", mockQuiz.getQuestions()));
        return future;
    }

    @Override
    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        CompletableFuture<StringPool> future = new CompletableFuture<>();
        waitForQuiz(future, quizId, mockQuiz -> mockQuiz.getStringPool(language));
        return future;
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }
}
