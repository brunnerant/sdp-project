package ch.epfl.qedit.backend.cache;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.StringPool;
import ch.epfl.qedit.model.User;

public class CacheDecorator implements DatabaseService {

    private FirebaseDBService dbService;

    public CacheDecorator(){
        dbService = new FirebaseDBService();
    }

    public CompletableFuture<List<String>> getQuizLanguages(String quizId) {
        CompletableFuture<List<String>> future = dbService.getQuizLanguages(String quizId);
        return future;
    }

    public CompletableFuture<Quiz> getQuizStructure(String quizId) {
        CompletableFuture<Quiz> future = dbService.getQuizStructure(String quizId);
        return future;
    }

    public CompletableFuture<StringPool> getQuizStringPool(String quizId, String language) {
        CompletableFuture<StringPool> future = dbService.getQuizStringPool(quizId, language);
        return future;
    }

    public CompletableFuture<User> getUser(String userId) {
        Boolean userIsInCache = true;
        if(userIsInCache) {
            return null;
        } else {
            CompletableFuture<User> future = dbService.getUser(userId);
            return future;
        }
    }

    public CompletableFuture<Void> createUser(String userId, String firstName, String lastName) {
        CompletableFuture<Void> future = dbService.createUser(userId, firstName, lastName);
        return future;
    }

    public CompletableFuture<Void> updateUserStatistics(String userId, int score, int successes, int attempts) {
        CompletableFuture<Void> future = dbService.updateUserStatistics(userId, score, successes, attempts);
        return future;
    }

    public CompletableFuture<Void> updateUserQuizList(String userId, Map<String, String> quizzes) {
        CompletableFuture<Void> future = dbService.updateUserQuizList(userId, quizzes);
        return future;
    }

    // Cache management

    // Internal storage structure:
    //

    public static void storeContent(Context context, String fileName, String content) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getContent(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            String contents = stringBuilder.toString();
            return contents;
        }
    }

    public static String[] getFilesList(Context context) {
        String[] files = context.fileList();
        return files;
    }
}
