package com.apon.taalmaatjes.frontend.presentation;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Person {
    // TODO: rewrite this class so it is "normal".
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;

    public Person(Integer id, String fName, String lName, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
    }

    public Integer getId() {
        return id.get();
    }
    public void setId(Integer id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getEmail() {
        return email.get();
    }
    public void setEmail(String fName) {
        email.set(fName);
    }

}
