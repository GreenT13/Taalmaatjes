package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Volunteermatch;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.VolunteermatchDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.VolunteermatchRecord;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import org.jooq.Configuration;
import org.jooq.SelectConditionStep;

import java.util.List;

import static org.jooq.impl.DSL.using;

public class VolunteerMatchMyDao extends VolunteermatchDao {

    public VolunteerMatchMyDao(Context context) {
        super(context.getConfiguration());
    }

    @Deprecated
    public VolunteerMatchMyDao(Configuration configuration) {
        super(configuration);
    }

    public boolean generateIds(VolunteermatchPojo volunteermatchPojo) {
        // We cannot generate an id if the volunteerId is unknown.
        if (volunteermatchPojo.getVolunteerid() == null) {
            return false;
        }

        // Do not generate id if it is already filled.
        if (volunteermatchPojo.getVolunteermatchid() != null) {
            return true;
        }

        Integer maxId = getMaxInteger(volunteermatchPojo.getVolunteerid());
        volunteermatchPojo.setVolunteermatchid(maxId != null ? maxId + 1 : Integer.valueOf(0));

        return true;
    }

    protected Integer getMaxInteger(Integer volunteerId) {
        return using(configuration())
                .select(Volunteermatch.VOLUNTEERMATCH.VOLUNTEERMATCHID.max())
                .from(Volunteermatch.VOLUNTEERMATCH)
                .where(Volunteermatch.VOLUNTEERMATCH.VOLUNTEERID.eq(volunteerId))
                .fetchOne(0, Integer.class);
    }

    @Override
    public void insert(VolunteermatchPojo volunteermatchPojo) {
        if (!generateIds(volunteermatchPojo)) {
            // Some kind of error message?
            return;
        }

        super.insert(volunteermatchPojo);
    }

    public List<VolunteermatchPojo> getMatchForVolunteer(int volunteerId, boolean sortAscending) {
        SelectConditionStep<VolunteermatchRecord> query = using(configuration())
                .selectFrom(Volunteermatch.VOLUNTEERMATCH)
                .where(Volunteermatch.VOLUNTEERMATCH.VOLUNTEERID.eq(volunteerId));

        if (sortAscending) {
            query.orderBy(Volunteermatch.VOLUNTEERMATCH.DATESTART.asc());
        } else {
            query.orderBy(Volunteermatch.VOLUNTEERMATCH.DATESTART.desc());
        }

        return query.fetch().map(mapper());
    }

    public List<VolunteermatchPojo> getMatchForStudent(int studentId, boolean sortAscending) {
        SelectConditionStep<VolunteermatchRecord> query = using(configuration())
                .selectFrom(Volunteermatch.VOLUNTEERMATCH)
                .where(Volunteermatch.VOLUNTEERMATCH.STUDENTID.eq(studentId));

        if (sortAscending) {
            query.orderBy(Volunteermatch.VOLUNTEERMATCH.DATESTART.asc());
        } else {
            query.orderBy(Volunteermatch.VOLUNTEERMATCH.DATESTART.desc());
        }

        return query.fetch().map(mapper());
    }
}
