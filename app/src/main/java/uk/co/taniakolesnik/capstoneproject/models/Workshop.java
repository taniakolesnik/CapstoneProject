package uk.co.taniakolesnik.capstoneproject.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Workshop implements Serializable {

    private String date;
    private String time;
    private String description;
    private String name;
    private String address;
    private String city;
    Map<String, User> users = new HashMap<>();

    // empty constructor for firebase
    public Workshop() {
    }

    public Workshop(String date, String time, String description, String name, String address, String city,
                       Map<String, User> users) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.name = name;
        this.address = address;
        this.city = city;
        this.users = users;
    }

    public void setValue(Map<String, User> map)
    {
        this.users = map;
    }
    public Map<String, User> getValue()
    {
        return this.users;
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

    @Override
    public String toString() {
        return "Workshop{" +
                "view_date_month='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}