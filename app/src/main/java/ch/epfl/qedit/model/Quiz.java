package ch.epfl.qedit.model;

public class Quiz {
    private QuizCategorie categorie;
    private int nbOfQuestions ;

    public Quiz(int nbOfQuestions, QuizCategorie categorie){
        this.categorie=categorie;
        this.nbOfQuestions=nbOfQuestions;
    }

    public void edit() {

    }

    public int getNbOfQuestions() {
        return nbOfQuestions;
    }

    public QuizCategorie getCategorie() {
        return categorie;
    }


}
