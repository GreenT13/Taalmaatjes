package com.apon.taalmaatjes.frontend.presentation;

import javafx.beans.property.SimpleStringProperty;

public class StudentRow {
    // TODO: rewrite this class so it is "normal".
    private final SimpleStringProperty extId;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty currentVolunteer;

    public StudentRow(String extId, String fName, String lName, String volunteer) {
        this.extId = new SimpleStringProperty(extId);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.currentVolunteer = new SimpleStringProperty(volunteer);
    }

    public String getExtId() {
        return extId.get();
    }
    public void setExtId(String extId) {
        this.extId.set(extId);
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

    public String getCurrentVolunteer() {
        return currentVolunteer.get();
    }
    public void setCurrentVolunteer(String volunteer) {
        currentVolunteer.set(volunteer);
    }

}
