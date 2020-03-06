package ch.epfl.qedit.model.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Administrator extends User {
    // list of participants for whom admin is responsible
    List<Participant> participants;

    public Administrator(String firstName, String lastName, int userId, String language) {
        super(firstName, lastName, userId, language);
        participants = new ArrayList<Participant>();
    }

    // launch the timer for a participant
    public void setQuizStartTime(Participant participant) {
        participant.setStartTime(new Date());
    }
}
