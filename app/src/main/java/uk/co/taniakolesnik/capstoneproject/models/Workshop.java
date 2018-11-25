package uk.co.taniakolesnik.capstoneproject.models;

import java.io.Serializable;

public class Workshop implements Serializable {

    private String date;
    private String time;
    private String description;
    private String name;
    private String address;
    private String city;

    private boolean isChecked;

    // empty constructor for firebase
    public Workshop() {
    }

    public Workshop(String date, String time, String description, String name, String address, String city) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.name = name;
        this.address = address;
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Workshop{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}