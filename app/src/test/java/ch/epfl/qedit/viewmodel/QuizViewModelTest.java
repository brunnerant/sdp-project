package ch.epfl.qedit.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import ch.epfl.qedit.model.Quiz;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class QuizViewModelTest {
    @Rule public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    @Test
    public void testThatInitialStateIsCorrect() {
        QuizViewModel model = new QuizViewModel();
        assertEquals(QuizViewModel.Status.NotLoaded, model.getStatus().getValue());
        assertNull(model.getQuiz().getValue());
        assertNull(model.getFocusedQuestion().getValue());
        assertNull(model.getAnswers().getValue());
    }

    @Test
    public void testThatQuizCanBeLoaded() {
        Observer statusObserver = mock(Observer.class);
        Observer quizObserver = mock(Observer.class);
        ArgumentCaptor<QuizViewModel.Status> statusArgs =
                ArgumentCaptor.forClass(QuizViewModel.Status.class);
        ArgumentCaptor<Quiz> quizArgs = ArgumentCaptor.forClass(Quiz.class);

        QuizViewModel model = new QuizViewModel();
        model.getStatus().observeForever(statusObserver);
        model.getQuiz().observeForever(quizObserver);
        model.loadQuiz("quiz0");

        verify(statusObserver, timeout(5000).times(3)).onChanged(statusArgs.capture());
        verify(quizObserver, timeout(5000).times(2)).onChanged(quizArgs.capture());

        assertEquals(
                Arrays.asList(
                        QuizViewModel.Status.NotLoaded,
                        QuizViewModel.Status.Loading,
                        QuizViewModel.Status.Loaded),
                statusArgs.getAllValues());
        assertNull(quizArgs.getAllValues().get(0));
        assertNotNull(quizArgs.getAllValues().get(1));
    }

    @Test
    public void testThatQuestionCanBeSelected() {
        Observer questionObserver = mock(Observer.class);
        QuizViewModel model = new QuizViewModel();
        model.getFocusedQuestion().observeForever(questionObserver);
        model.getFocusedQuestion().setValue(3);
        verify(questionObserver).onChanged(3);
    }
}
