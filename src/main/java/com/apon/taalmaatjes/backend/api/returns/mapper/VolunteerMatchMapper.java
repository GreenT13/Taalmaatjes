package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;

public class VolunteerMatchMapper {
    private VolunteerMatchReturn volunteerMatchReturn;

    public VolunteerMatchMapper() {
        volunteerMatchReturn = new VolunteerMatchReturn();
    }

    public VolunteerMatchMapper(VolunteerMatchReturn volunteerMatchReturn) {
        this.volunteerMatchReturn = volunteerMatchReturn;
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

}
