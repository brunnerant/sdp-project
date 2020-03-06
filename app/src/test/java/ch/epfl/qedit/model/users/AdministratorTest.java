package ch.epfl.qedit.model.users;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AdministratorTest {
    @Test
    public void administratorConstructorTest(){
        Administrator admin = new Administrator("Jean","Paul",42,"Polonais");

        assertEquals(admin.getFirstName(),"Jean");
        assertEquals(admin.getLanguage(),"Polonais");
        assertEquals(admin.getLastName(),"Paul");
        assertEquals(admin.getUserId(),42);

    }
}
