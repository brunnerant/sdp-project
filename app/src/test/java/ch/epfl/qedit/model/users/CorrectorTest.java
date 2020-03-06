package ch.epfl.qedit.model.users;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CorrectorTest {
    @Test
    public void correctorConstructorTest(){
        Corrector corrector = new Corrector("Paul", "Jean", 37,"EN");
        assertEquals(corrector.getFirstName(),"Paul");
        assertEquals(corrector.getLanguage(),"EN");
        assertEquals(corrector.getLastName(),"Jean");
        assertEquals(corrector.getUserId(),37);
    }
}
