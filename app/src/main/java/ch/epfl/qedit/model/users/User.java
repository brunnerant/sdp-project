package ch.epfl.qedit.model.users;

public abstract class User {
    private final int userId;
    private final String firstName;
    private final String lastName;
    private final String language;
    public User(String firstName, String lastName, int userId,String language){
        this.firstName=firstName;
        this.lastName=lastName;
        this.userId=userId;
        this.language=language;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getUserId() {
        return userId;
    }

    public String getLanguage() {
        return language;
    }

}

