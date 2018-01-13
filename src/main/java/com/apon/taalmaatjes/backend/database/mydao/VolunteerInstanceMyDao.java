package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Volunteerinstance;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.VolunteerinstanceDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import org.jooq.Configuration;

import static org.jooq.impl.DSL.using;

public class VolunteerInstanceMyDao extends VolunteerinstanceDao {

    public VolunteerInstanceMyDao(Configuration configuration) {
        super(configuration);
    }

    public boolean generateIds(VolunteerinstancePojo volunteerinstancePojo) {
        // We cannot generate an id if the volunteerId is unknown.
        if (volunteerinstancePojo.getVolunteerid() == null) {
            return false;
        }

        // Do not generate id if it is already filled.
        if (volunteerinstancePojo.getVolunteerinstanceid() != null) {
            return true;
        }

        Integer maxId = getMaxInteger(volunteerinstancePojo.getVolunteerid());
        volunteerinstancePojo.setVolunteerinstanceid(maxId != null ? maxId + 1 : Integer.valueOf(0));

        return true;
    }

    protected Integer getMaxInteger(Integer volunteerId) {
        return using(configuration())
                .select(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERINSTANCEID.max())
                .from(Volunteerinstance.VOLUNTEERINSTANCE)
                .where(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERID.eq(volunteerId))
                .fetchOne(0, Integer.class);
    }

    @Override
    public void insert(VolunteerinstancePojo volunteerinstancePojo) {
        if (!generateIds(volunteerinstancePojo)) {
            // Some kind of error message?
            return;
        }

        super.insert(volunteerinstancePojo);
    }

}
