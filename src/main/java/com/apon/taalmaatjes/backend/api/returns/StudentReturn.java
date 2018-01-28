package com.apon.taalmaatjes.backend.api.returns;

import java.util.List;

public class StudentReturn {
    private String externalIdentifier;
    private String firstName;
    private String insertion;
    private String lastName;
    private Boolean isLookingForVolunteer;
    private Boolean isGroup;

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

    public Boolean getLookingForVolunteer() {
        return isLookingForVolunteer;
    }

    public void setLookingForVolunteer(Boolean lookingForVolunteer) {
        isLookingForVolunteer = lookingForVolunteer;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public List<VolunteerMatchReturn> getListVolunteerMatch() {
        return listVolunteerMatch;
    }

    public void setListVolunteerMatch(List<VolunteerMatchReturn> listVolunteerMatch) {
        this.listVolunteerMatch = listVolunteerMatch;
    }
}
