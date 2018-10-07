package uk.co.taniakolesnik.capstoneproject.objects;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String firstName;
    private String pronouns;
    private String email;
    private String mobile;
    private String twitter;
    private String about;
    private int userType; // 1 - participant  2 - admin

    // empty constructor for firebase
    public User() {
    }

}
