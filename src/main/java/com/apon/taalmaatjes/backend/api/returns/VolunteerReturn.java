package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class VolunteerReturn {
    // Variables extracted from the database.
    private String externalIdentifier;
    private String firstName;
    private String insertion;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String mobilePhoneNumber;
    private String email;
    private Boolean hasTraining;
    private String postalCode;
    private String city;
    private String streetname;
    private String houseNr;
    private String log;

    // Variables determined by logic.
    private Date activeUntil;
    private boolean isActiveToday;

    private List<VolunteerInstanceReturn> listVolunteerInstance;
    private List<VolunteerMatchReturn> listVolunteerMatch;

    public VolunteerReturn() {
        listVolunteerInstance = new ArrayList();
        listVolunteerMatch = new ArrayList();
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getInsertion() {
        return insertion;
    }

    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasTraining() {
        return hasTraining;
    }

    public void setHasTraining(Boolean hasTraining) {
        this.hasTraining = hasTraining;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public String getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr;
    }

    public Date getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Date activeUntil) {
        this.activeUntil = activeUntil;
    }

    public boolean isActiveToday() {
        return isActiveToday;
    }

    public void setActiveToday(boolean activeToday) {
        isActiveToday = activeToday;
    }

    public List<VolunteerInstanceReturn> getListVolunteerInstance() {
        return listVolunteerInstance;
    }

    public void setListVolunteerInstance(List<VolunteerInstanceReturn> listVolunteerInstance) {
        this.listVolunteerInstance = listVolunteerInstance;
    }

    public List<VolunteerMatchReturn> getListVolunteerMatch() {
        return listVolunteerMatch;
    }

    public void setListVolunteerMatch(List<VolunteerMatchReturn> listVolunteerMatch) {
        this.listVolunteerMatch = listVolunteerMatch;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
