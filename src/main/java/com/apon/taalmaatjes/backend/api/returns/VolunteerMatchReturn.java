package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;

public class VolunteerMatchReturn {
    private String volunteerExtId;
    private String externalIdentifier;
    private String studentExtId;
    private Date dateStart;
    private Date dateEnd;

    public String getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(String externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
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

    public String getVolunteerExtId() {
        return volunteerExtId;
    }

    public void setVolunteerExtId(String volunteerExtId) {
        this.volunteerExtId = volunteerExtId;
    }

    public String getStudentExtId() {
        return studentExtId;
    }

    public void setStudentExtId(String studentExtId) {
        this.studentExtId = studentExtId;
    }
}
