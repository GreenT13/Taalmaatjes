package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;
import java.util.List;

public class StudentReturn {
    private String externalIdentifier;
    private String firstName;
    private String insertion;
    private String lastName;
    private String sex;
    private Date dateOfBirth;
    private String groupIdentification;
    private Boolean hasQuit;

    private List<VolunteerMatchReturn> listVolunteerMatch;

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

    public List<VolunteerMatchReturn> getListVolunteerMatch() {
        return listVolunteerMatch;
    }

    public void setListVolunteerMatch(List<VolunteerMatchReturn> listVolunteerMatch) {
        this.listVolunteerMatch = listVolunteerMatch;
    }

    public String getGroupIdentification() {
        return groupIdentification;
    }

    public void setGroupIdentification(String groupIdentification) {
        this.groupIdentification = groupIdentification;
    }

    public Boolean getHasQuit() {
        return hasQuit;
    }

    public void setHasQuit(Boolean hasQuit) {
        this.hasQuit = hasQuit;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
