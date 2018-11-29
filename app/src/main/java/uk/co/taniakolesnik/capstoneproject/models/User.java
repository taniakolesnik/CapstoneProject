package uk.co.taniakolesnik.capstoneproject.models;

public class User {

    private String email;
    private int role; // 1 - coach ; 0 - organiser ; 2 - student

    public User(String userEmail, int role) {
        this.email = userEmail;
        this.role = role;
    }

    // for firebase
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
