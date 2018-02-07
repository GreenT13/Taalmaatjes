package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;

public class VolunteerMatchMapper {
    private VolunteerMatchReturn volunteerMatchReturn;

    public VolunteerMatchMapper() {
        volunteerMatchReturn = new VolunteerMatchReturn();
    }

    public VolunteerMatchMapper(VolunteerMatchReturn volunteerMatchReturn) {
        this.volunteerMatchReturn = volunteerMatchReturn;
    }

    public VolunteerMatchReturn getVolunteerMatchReturn() {
        return volunteerMatchReturn;
    }

    public VolunteermatchPojo getPojo(Integer volunteerId, Integer studentId) {
        VolunteermatchPojo volunteermatchPojo = new VolunteermatchPojo();
        volunteermatchPojo.setVolunteerid(volunteerId);
        volunteermatchPojo.setStudentid(studentId);
        volunteermatchPojo.setDatestart(volunteerMatchReturn.getDateStart());
        volunteermatchPojo.setDateend(volunteerMatchReturn.getDateEnd());
        volunteermatchPojo.setExternalidentifier(volunteerMatchReturn.getExternalIdentifier());

        return volunteermatchPojo;
    }

    public void setVolunteerMatch(VolunteermatchPojo volunteermatchPojo) {
        volunteerMatchReturn.setExternalIdentifier(volunteermatchPojo.getExternalidentifier());
        volunteerMatchReturn.setDateStart(volunteermatchPojo.getDatestart());
        volunteerMatchReturn.setDateEnd(volunteermatchPojo.getDateend());
    }

    public void setStudent(String studentExtId) {
        volunteerMatchReturn.setStudentExtId(studentExtId);
    }

}
