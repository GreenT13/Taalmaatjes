package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;

public class VolunteerMatchReturn {
    private String volunteerExternalIdentifier;
    private String externalIdentifier;
    private StudentReturn student;
    private Date dateStart;
    private Date dateEnd;

    public String getVolunteerExternalIdentifier() {
        return volunteerExternalIdentifier;
    }

    public void setVolunteerExternalIdentifier(String volunteerExternalIdentifier) {
        this.volunteerExternalIdentifier = volunteerExternalIdentifier;
    }

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public StudentReturn getStudent() {
        return student;
    }

    public void setStudent(StudentReturn student) {
        this.student = student;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
}
