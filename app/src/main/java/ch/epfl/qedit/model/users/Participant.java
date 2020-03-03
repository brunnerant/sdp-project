package ch.epfl.qedit.model.users;

import java.util.Date;

import ch.epfl.qedit.model.Quiz;

public class Participant extends User {
    private Quiz quiz;
    private Date startTime;


    public Participant(String firstName, String lastName, int userId, String language) {
        super(firstName, lastName, userId, language);
    }

    //allows participant to select a quiz or be assigned one
    public boolean getQuiz(Quiz quiz){
        if(this.quiz !=null) {
            this.quiz = quiz;
            return true;
        }
        return false;
    }

    public void setStartTime(Date date){
        startTime=date;
    }
    //this method should allow a participant to submit his quiz and store the time he took to complete it
    //returns true if the submission is successful, false otherwise
    public boolean submitQuiz(){
        //completion time in milliseconds
        long totalTime = new Date().getTime()-startTime.getTime();
        //convert totalTime to minutes
        totalTime/=(1000*60);

        //firebase.send(this.quiz);

        return false;
    }

}
