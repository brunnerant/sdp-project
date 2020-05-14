package ch.epfl.qedit.backend.database;

import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This interface represents a database from which data can be retrieved or modified. For now, only
 * getting data is possible, but in the future, there will be support for modifying data. Note that
 * the functions in this interface are all asynchronous. There are many asynchronous primitives out
 * there, but CompletableFuture is the one that is the most complete. It has a good variety of
 * methods to compose futures together, so this is why it was chosen.
 */
public interface DatabaseService {
    /**
     * Asynchronously retrieves a supported language table of a quiz from the database.
     *
     * @param quizId the id of the quiz
     * @return the future for the quiz
     */
    CompletableFuture<List<String>> getQuizLanguages(String quizId);

    /**
     * Asynchronously retrieves the structure of a quiz from the database. By structure, it is meant
     * that the quiz does not contain the text, but rather ids that refer to the strings in the
     * string pools. This allows one quiz to be translated easily across languages by simply adding
     * string pools.
     *
     * @param quizId the id of the quiz
     * @return the future for the quiz structure
     */
    CompletableFuture<Quiz> getQuizStructure(String quizId);

    /**
     * Asynchronously retrieves the string pool for a quiz, in the given language. The string pool
     * can be used to instantiate the quiz structure in a particular language by using the
     * instantiateLanguage method which is implemented in Quiz.
     *
     * @param quizId the id of the quiz
     * @param language the language of the string pool
     * @return the future for the string pool
     */
    CompletableFuture<StringPool> getQuizStringPool(String quizId, String language);

    /**
     * Asynchronously retrieves the user that correspond to the userId.
     *
     * @param userId the id of the user in the database
     * @return the future for the user
     */
    CompletableFuture<User> getUser(String userId);

    /**
     * Asynchronously create a user in the database. The user must have an unique id and at least a
     * first and last name.
     *
     * @param userId the id of the new user
     * @param firstName of the new user
     * @param lastName of the new user
     * @return the future for the error if any. Else the future is empty.
     */
    CompletableFuture<Void> createUser(String userId, String firstName, String lastName);

    /**
     * Asynchronously update the user statistics attributes in the database.
     *
     * @param userId the id of the new user
     * @param score of the user
     * @param successes of the user
     * @param attempts of the user
     * @return the future for the error if any. Else the future is empty.
     */
    CompletableFuture<Void> updateUserStatistics(
            String userId, int score, int successes, int attempts);

    /**
     * Asynchronously update the user quizzes list in the database.
     *
     * @param userId the id of the new user
     * @param quizzes, a map of the quizzes id to quizzes title of the user
     * @return the future for the error if any. Else the future is empty.
     */
    CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes);
}
