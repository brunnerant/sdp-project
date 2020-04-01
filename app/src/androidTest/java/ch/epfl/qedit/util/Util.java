package ch.epfl.qedit.util;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import java.util.Arrays;

public class Util {

    public static Quiz createMockQuiz(String title) {
        return new Quiz(
                title,
                Arrays.asList(
                        new Question(
                                "The matches problem",
                                "How many matches can fit in a shoe of size 43?",
                                "matrix3x3"),
                        new Question(
                                "Pigeons",
                                "How many pigeons are there on Earth? (Hint: do not count yourself)",
                                "matrix1x1"),
                        new Question("KitchenBu", "Oyster", "matrix1x1"),
                        new Question(
                                "Everything",
                                "What is the answer to life the universe and everything?",
                                "matrix3x3"),
                        new Question("Banane", "Combien y a-t-il de bananes ?", "matrix1x1")));
    }
}
