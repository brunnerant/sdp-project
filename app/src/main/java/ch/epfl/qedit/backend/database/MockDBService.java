package ch.epfl.qedit.backend.database;

import static ch.epfl.qedit.model.StringPool.TITLE_ID;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import ch.epfl.qedit.backend.Util;
import ch.epfl.qedit.backend.auth.MockAuthService;
import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.model.answer.MatrixFormat;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
                MatrixFormat.singleField(MatrixFormat.Field.textField("hint1"));
        static final MatrixFormat compoundFormat =
                new MatrixFormat.Builder(2, 2)
                        .withField(0, 0, MatrixFormat.Field.preFilledField("hint2"))
                        .withField(0, 1, MatrixFormat.Field.numericField(false, true, "hint3"))
                        .withField(1, 0, MatrixFormat.Field.textField("hint4"))
                        .withField(1, 1, MatrixFormat.Field.numericField(true, false, "hint5"))
                        .build();

        @SuppressWarnings("SpellCheckingInspection")
        static MockQuiz createTestMockQuiz1() {
            HashMap<String, String> stringPool_en = new HashMap<>();
            stringPool_en.put(TITLE_ID, "I am a Mock Quiz!");
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
            stringPool_fr.put(TITLE_ID, "Je suis un Mock Quiz !");
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

            StringPool stringPool = new StringPool(stringPool_en);
            stringPool.setLanguageCode("en");
            stringPools.put("en", stringPool);

            stringPool = new StringPool(stringPool_fr);
            stringPool.setLanguageCode("fr");
            stringPools.put("fr", stringPool);

            List<Question> questions =
                    Arrays.asList(
                            new Question("q1_title", "q1_text", simpleFormat),
                            new Question("q2_title", "q2_text", compoundFormat),
                            new Question("q3_title", "q3_text", simpleFormat),
                            new Question("q4_title", "q4_text", compoundFormat),
                            new Question("q5_title", "q5_text", simpleFormat));

            return new MockQuiz(questions, stringPools);
        }

        @SuppressWarnings("SpellCheckingInspection")
        static MockQuiz createTestMockQuiz2() {
            HashMap<String, String> stringPool_en = new HashMap<>();
            stringPool_en.put(TITLE_ID, "An other Quiz");
            stringPool_en.put("q1_title", "Banana");
            stringPool_en.put("q1_text", "How many bananas are there on Earth?");
            stringPool_en.put("hint1", "text field");

            HashMap<String, String> stringPool_fr = new HashMap<>();
            stringPool_fr.put(TITLE_ID, "Un autre Quiz");
            stringPool_fr.put("q1_title", "Banane");
            stringPool_fr.put("q1_text", "Combien y a-t-il de bananes sur Terre ?");
            stringPool_fr.put("hint1", "champ texte");

            HashMap<String, StringPool> stringPools = new HashMap<>();

            StringPool stringPool = new StringPool(stringPool_en);
            stringPool.setLanguageCode("en");
            stringPools.put("en", stringPool);

            stringPool = new StringPool(stringPool_fr);
            stringPool.setLanguageCode("fr");
            stringPools.put("fr", stringPool);

            List<Question> questions =
                    Arrays.asList(new Question("q1_title", "q1_text", simpleFormat));

            return new MockQuiz(questions, stringPools);
        }
    }

    private HashMap<String, MockQuiz> quizzes;
    private HashMap<String, User> users;
    private CountingIdlingResource idlingResource;

    public MockDBService() {
        idlingResource = new CountingIdlingResource("MockDBService");
        quizzes = new HashMap<>();
        quizzes.put("quiz0", MockQuiz.createTestMockQuiz1());
        quizzes.put("quiz1", MockQuiz.createTestMockQuiz2());
        quizzes.put("quiz2", MockQuiz.createTestMockQuiz2());
        quizzes.put("quiz3", MockQuiz.createTestMockQuiz2());

        users = new HashMap<>();
        users.put(MockAuthService.ANTHONY_IOZZIA_ID, createAnthony());
        users.put(MockAuthService.COSME_JORDAN_ID, createCosme());
    }

    public static User createAnthony() {
        User anthony = new User("Anthony", "Iozzia", 78, 7, 3);
        anthony.addQuiz("quiz0", "I am a Mock Quiz!");
        anthony.addQuiz("quiz1", "An other Quiz");

        return anthony;
    }

    public static User createCosme() {
        User cosme = new User("Cosme", "Jordan");
        cosme.addQuiz("quiz0", "I am a Mock Quiz!");

        return cosme;
    }

    /** Simply make the current thread wait 2 second */
    private static void wait2second() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** This allows to complete the future with a request exception */
    private static void error(CompletableFuture<?> future, String message) {
        future.completeExceptionally(new Util.RequestException(message));
    }

    private <T> void waitForQuiz(
            CompletableFuture<T> future, String quizId, Function<MockQuiz, T> f) {
        idlingResource.increment();

        new Thread(
                        () -> {
                            wait2second();
                            MockQuiz quiz = quizzes.get(quizId);
                            if (quiz == null) error(future, "Invalid quiz id");
                            else future.complete(f.apply(quiz));
                            idlingResource.decrement();
                        })
                .run();
    }

    private CompletableFuture<Void> updateUser(String userId, User user, boolean error) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            wait2second();
                            if (error) error(future, "Invalid user id");
                            else {
                                users.put(userId, user);
                                future.complete(null);
                            }
                            idlingResource.decrement();
                        })
                .run();

        return future;
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
        waitForQuiz(future, quizId, mockQuiz -> new Quiz(TITLE_ID, mockQuiz.getQuestions()));
        return future;
    }

    @Override
    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        CompletableFuture<StringPool> future = new CompletableFuture<>();
        waitForQuiz(
                future,
                quizId,
                mockQuiz -> {
                    StringPool pool = mockQuiz.getStringPool(language);
                    if (pool == null) error(future, "Language does not exist");
                    return pool;
                });
        return future;
    }

    @Override
    public CompletableFuture<String> uploadQuiz(Quiz quiz, StringPool stringPool) {
        return null;
    }

    @Override
    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> future = new CompletableFuture<>();
        idlingResource.increment();
        new Thread(
                        () -> {
                            wait2second();
                            User user = users.get(userId);
                            if (user == null) error(future, "Invalid user id");
                            else future.complete(user);
                            idlingResource.decrement();
                        })
                .run();

        return future;
    }

    @Override
    public CompletableFuture<Void> createUser(String userId, String firstName, String lastName) {
        return updateUser(userId, new User(firstName, lastName), false);
    }

    @Override
    public CompletableFuture<Void> updateUserStatistics(
            String userId, int score, int successes, int attempts) {

        User user = users.get(userId);
        boolean error = user == null;
        if (!error) {
            user =
                    new User(
                            user.getFirstName(),
                            user.getLastName(),
                            user.getQuizzes(),
                            score,
                            successes,
                            attempts);
        }

        return updateUser(userId, user, error);
    }

    @Override
    public CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes) {

        User user = users.get(userId);
        boolean error = user == null;
        if (!error) {
            user =
                    new User(
                            user.getFirstName(),
                            user.getLastName(),
                            quizzes,
                            user.getScore(),
                            user.getSuccesses(),
                            user.getAttempts());
        }

        return updateUser(userId, user, error);
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }
}
