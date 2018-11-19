package uk.co.taniakolesnik.capstoneproject.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String pronouns;
    private String email;
    private String mobile;
    private String twitter;
    private String about;
    private int userType;  //
    private ArrayList<String> workshopsList;
    private ArrayList<String> subscriptions;

    // empty constructor for firebase
    public User() {
    }


    public User(String firstName, String lastName, String pronouns, String email,
                String mobile, String twitter, String about, int userType,
                ArrayList<String> workshopsList, ArrayList<String> subscriptions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.email = email;
        this.mobile = mobile;
        this.twitter = twitter;
        this.about = about;
        this.userType = userType;
        this.workshopsList = workshopsList;
        this.subscriptions = subscriptions;
    }

    public User(String firstName, String lastName, String pronouns, String email, int userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pronouns = pronouns;
        this.email = email;
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public ArrayList<String> getWorkshopsList() {
        return workshopsList;
    }

    public void setWorkshopsList(ArrayList<String> workshopsList) {
        this.workshopsList = workshopsList;
    }

    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "User{" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pronouns='" + pronouns + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", twitter='" + twitter + '\'' +
                ", about='" + about + '\'' +
                ", userType=" + userType +
                ", workshopsList=" + workshopsList +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
