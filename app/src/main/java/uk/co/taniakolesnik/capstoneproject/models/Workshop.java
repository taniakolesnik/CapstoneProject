package uk.co.taniakolesnik.capstoneproject.models;

import java.io.Serializable;

public class Workshop implements Serializable {

    private String date;
    private String time;
    private String description;
    private String name;
    private String webAddress;
    private String buildingName;
    private String street;
    private String city;
    private String country;
    private String postCode;
    private String directions;
    private String accessibilityInfo;

    // empty constructor for firebase
    public Workshop() {
    }

    public Workshop(String date, String time, String description,
                    String name, String webAddress, String buildingName, String street, String city,
                    String country, String postCode, String directions, String accessibilityInfo) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.name = name;
        this.webAddress = webAddress;
        this.buildingName = buildingName;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postCode = postCode;
        this.directions = directions;
        this.accessibilityInfo = accessibilityInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getAccessibilityInfo() {
        return accessibilityInfo;
    }

    public void setAccessibilityInfo(String accessibilityInfo) {
        this.accessibilityInfo = accessibilityInfo;
    }

    @Override
    public String toString() {
        return "Workshop{" +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", webAddress='" + webAddress + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postCode='" + postCode + '\'' +
                ", directions='" + directions + '\'' +
                ", accessibilityInfo='" + accessibilityInfo + '\'' +
                '}';
    }
}