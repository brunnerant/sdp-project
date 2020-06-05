package ch.epfl.qedit.view.quiz;

import android.content.Context;
import android.content.Intent;
import android.opengl.Matrix;
import android.os.Bundle;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.qedit.model.Question;
import ch.epfl.qedit.model.Quiz;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.model.answer.AnswerModel;
import ch.epfl.qedit.model.answer.MatrixFormat;
import ch.epfl.qedit.model.answer.MatrixModel;

import static ch.epfl.qedit.view.home.HomeActivity.USER;
import static ch.epfl.qedit.view.home.HomeQuizListFragment.QUIZ_ID;

public final class CorrectionUtil {
    private CorrectionUtil(){}
    private static ArrayList<Integer> correctedQuestions;

     public static List<Question> correctedQuestions(ImmutableList<Question> questions, HashMap<Integer, AnswerModel>answers){
         correctedQuestions= new ArrayList<>();
        List<Question>corrected = new ArrayList<>();
        for(int i =0;i<questions.size();i++){
            MatrixModel answerModel = (MatrixModel) answers.get(i);
            int goodAnswer = questions.get(i).getFormat().correct(answerModel) ? 1:0;
            correctedQuestions.add(i,goodAnswer);
            corrected.add(i,makePrefilledMatrixQuestion(answerModel,questions.get(i)));
        }
        return corrected;
     }
     static Question makePrefilledMatrixQuestion(MatrixModel answerModel,Question quizQuestion){
         MatrixModel model;
         if(answerModel==null){
             model = (MatrixModel)quizQuestion.getFormat().getEmptyAnswerModel();
         }else{
             model=answerModel;
         }
         MatrixFormat.Builder builder = new MatrixFormat.Builder(model.getNumRows(),model.getNumCols());
         for(int x =0;x<model.getNumRows();x++){
             for(int y =0; y<model.getNumCols();y++){
                 builder.withField(x,y,MatrixFormat.Field.preFilledField(model.getAnswer(x,y)));
             }
         }
         return new Question(quizQuestion.getTitle(),quizQuestion.getText(),builder.build());
     }
     public static ArrayList<Integer> getGoodAnswers(){
         return correctedQuestions;
     }
     public static void startCorrection(ImmutableList<Question> questions, HashMap<Integer,AnswerModel> answers, Context context, User user){
         Quiz questionsLocked = new Quiz("Correction", correctedQuestions(questions,answers);
         Intent intent = new Intent(context, QuizActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         Bundle bundle = new Bundle();
         bundle.putSerializable(QUIZ_ID,questionsLocked);
         bundle.putIntegerArrayList(GOOD_ANSWERS,CorrectionUtil.getGoodAnswers());
         bundle.putSerializable(USER,user);
         bundle.putBoolean(CORRECTION,true);
         intent.putExtras(bundle);
         context.startActivity(intent);
     }
}
