package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;

public class VolunteerInstanceMapper {
    private VolunteerInstanceReturn volunteerInstanceReturn;

    public VolunteerInstanceMapper() {
        volunteerInstanceReturn = new VolunteerInstanceReturn();
    }

    public VolunteerInstanceMapper(VolunteerInstanceReturn volunteerInstanceReturn) {
        this.volunteerInstanceReturn = volunteerInstanceReturn;
    }

    public VolunteerInstanceReturn getVolunteerInstanceReturn() {
        return volunteerInstanceReturn;
    }

    public void setVolunteerInstanceReturn(VolunteerInstanceReturn volunteerInstanceReturn) {
        this.volunteerInstanceReturn = volunteerInstanceReturn;
    }

    public VolunteerinstancePojo getPojo(Integer volunteerId, Integer volunteerInstanceId) {
        VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
        volunteerinstancePojo.setVolunteerid(volunteerId);
        volunteerinstancePojo.setVolunteerinstanceid(volunteerInstanceId);
        volunteerinstancePojo.setExternalidentifier(volunteerInstanceReturn.getExternalIdentifier());
        volunteerinstancePojo.setDatestart(volunteerInstanceReturn.getDateStart());
        volunteerinstancePojo.setDateend(volunteerInstanceReturn.getDateEnd());

        return volunteerinstancePojo;
    }

    public void setVolunteerInstance(VolunteerinstancePojo volunteerinstancePojo) {
        volunteerInstanceReturn.setExternalIdentifier(volunteerinstancePojo.getExternalidentifier());
        volunteerInstanceReturn.setDateStart(volunteerinstancePojo.getDatestart());
        volunteerInstanceReturn.setDateEnd(volunteerinstancePojo.getDateend());
    }
}
