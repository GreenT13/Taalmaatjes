package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;
import java.util.List;

public class ReportReturn {
    private Date dateStart;
    private Date dateEnd;
    private List<RangeReportReturn> volunteers;
    private List<RangeReportReturn> students;

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

    public List<RangeReportReturn> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<RangeReportReturn> volunteers) {
        this.volunteers = volunteers;
    }

    public List<RangeReportReturn> getStudents() {
        return students;
    }

    public void setStudents(List<RangeReportReturn> students) {
        this.students = students;
    }
}
