package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;

public class VolunteerMatchReturn {
    private VolunteerReturn volunteerReturn;
    private String externalIdentifier;
    private StudentReturn student;
    private Date dateStart;
    private Date dateEnd;

    public VolunteerReturn getVolunteerReturn() {
        return volunteerReturn;
    }

    public void setVolunteerReturn(VolunteerReturn volunteerReturn) {
        this.volunteerReturn = volunteerReturn;
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
