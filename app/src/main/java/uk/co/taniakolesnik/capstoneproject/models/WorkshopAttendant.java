package uk.co.taniakolesnik.capstoneproject.models;

public class WorkshopAttendant {

    private String email;
    private int role; // 1 - coach ; 0 - organiser ; 2 - student

    public WorkshopAttendant(String userEmail, int role) {
        this.email = userEmail;
        this.role = role;
    }

    // for firebase
    public WorkshopAttendant() {
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
        return "WorkshopAttendant{" +
                "email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
