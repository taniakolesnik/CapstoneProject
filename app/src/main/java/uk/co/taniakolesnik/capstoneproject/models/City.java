package uk.co.taniakolesnik.capstoneproject.models;

import java.io.Serializable;

public class City implements Serializable {

    private String name;

    // for firebase
    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
