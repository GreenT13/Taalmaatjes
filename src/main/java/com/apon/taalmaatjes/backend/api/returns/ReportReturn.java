package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;

public class ReportReturn {
    private Date dateStart;
    private Date dateEnd;
    private Integer nrOfNewVolunteers;
    private Integer nrOfActiveVolunteers;
    private Integer nrOfNewStudents;
    private Integer nrOfActiveStudents;

    public ReportReturn(Date dateStart, Date dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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

    public Integer getNrOfNewVolunteers() {
        return nrOfNewVolunteers;
    }

    public void setNrOfNewVolunteers(Integer nrOfNewVolunteers) {
        this.nrOfNewVolunteers = nrOfNewVolunteers;
    }

    public Integer getNrOfActiveVolunteers() {
        return nrOfActiveVolunteers;
    }

    public void setNrOfActiveVolunteers(Integer nrOfActiveVolunteers) {
        this.nrOfActiveVolunteers = nrOfActiveVolunteers;
    }

    public Integer getNrOfNewStudents() {
        return nrOfNewStudents;
    }

    public void setNrOfNewStudents(Integer nrOfNewStudents) {
        this.nrOfNewStudents = nrOfNewStudents;
    }

    public Integer getNrOfActiveStudents() {
        return nrOfActiveStudents;
    }

    public void setNrOfActiveStudents(Integer nrOfActiveStudents) {
        this.nrOfActiveStudents = nrOfActiveStudents;
    }
}
