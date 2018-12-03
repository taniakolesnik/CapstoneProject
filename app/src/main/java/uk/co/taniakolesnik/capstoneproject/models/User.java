package uk.co.taniakolesnik.capstoneproject.models;

import android.support.annotation.Keep;

@Keep
public class User {

    private String email;
    private String displayName ;
    private String photoUrl;

    public User(String userEmail, String displayName, String photoUrl) {
        this.email = userEmail;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    // for firebase
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email==null){
            email = "not provided";
        }
        this.email = email;
    }

    public String getDisplayName() {
        if (displayName == null){
            displayName = "not provided";
        }
        return displayName;
    }

    public void setDisplayName(String name) {
        if (name == null){
            name = "not provided";
        }
        this.displayName = name;
    }

    public String getPhotoUrl() {
        if (photoUrl == null){
            photoUrl = "not provided";
        }
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        if (photoUrl == null){
            photoUrl = "not provided";
        }
        this.photoUrl = photoUrl;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                "displayName='" + displayName + '\'' +
                "photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
