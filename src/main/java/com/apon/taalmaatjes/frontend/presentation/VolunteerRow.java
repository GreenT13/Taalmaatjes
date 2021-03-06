package com.apon.taalmaatjes.frontend.presentation;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class VolunteerRow {
    // TODO: rewrite this class so it is "normal".
    private final SimpleStringProperty extId;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty insertion;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phoneNr;
    private final SimpleIntegerProperty nrOfMatches;
    private final SimpleStringProperty city;

    public VolunteerRow(String extId, String fName, String insertion, String lName, String email, String phoneNr,
                        Integer nrOfMatches, String city) {
        this.extId = new SimpleStringProperty(extId);
        this.firstName = new SimpleStringProperty(fName);
        this.insertion = new SimpleStringProperty(insertion);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
        this.phoneNr = new SimpleStringProperty(phoneNr);
        this.nrOfMatches = new SimpleIntegerProperty(nrOfMatches);
        this.city = new SimpleStringProperty(city);
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

    public String getInsertion() { return insertion.get(); }
    public void setInsertion(String insertion) { this.insertion.set(insertion); }

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

    public String getPhoneNr(){ return phoneNr.get(); }
    public void setPhoneNr(String phoneNr) { this.phoneNr.set(phoneNr); }

    public Integer getNrOfMatches() { return nrOfMatches.get(); }
    public void setNrOfMatches(Integer nrOfMatches) { this.nrOfMatches.set(nrOfMatches); }

    public String getCity() { return city.get(); }
    public void setCity(String city) { this.city.set(city); }
}
