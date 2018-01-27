package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Volunteer;
import com.apon.taalmaatjes.backend.database.generated.tables.Volunteerinstance;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.VolunteerDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.VolunteerRecord;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.log.Log;
import org.jooq.Configuration;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.jooq.util.mysql.MySQLDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.util.List;

import static org.jooq.impl.DSL.using;

public class VolunteerMyDao extends VolunteerDao {
    public final static Integer STARTING_EXT_ID = Integer.valueOf(1001);

    public VolunteerMyDao(Context context) {
        super(context.getConfiguration());
    }

    public boolean generateIds(VolunteerPojo volunteerPojo) {
        if (volunteerPojo.getVolunteerid() == null) {
            Integer maxId = getMaxId();
            volunteerPojo.setVolunteerid(maxId != null ? maxId + 1 : 0);
        }

        if (volunteerPojo.getExternalidentifier() == null) {
            String maxExtId = getMaxExtId();
            if (maxExtId == null) {
                maxExtId = STARTING_EXT_ID.toString();
            } else {
                maxExtId = String.valueOf(Integer.valueOf(maxExtId) + 1);
            }
            volunteerPojo.setExternalidentifier(maxExtId);
        }

        return true;
    }

    public Integer getMaxId() {
        return using(configuration())
                .select(Volunteer.VOLUNTEER.VOLUNTEERID.max())
                .from(Volunteer.VOLUNTEER)
                .fetchOne(0, Integer.class);
    }

    public String getMaxExtId() {
        return using(configuration())
                .select(Volunteer.VOLUNTEER.EXTERNALIDENTIFIER.cast(MySQLDataType.INT).max())
                .from(Volunteer.VOLUNTEER)
                .fetchOne(0, String.class);
    }

    public Integer getIdFromExtId(String externalIdentifier) {
        return using(configuration())
                .select(Volunteer.VOLUNTEER.VOLUNTEERID)
                .from(Volunteer.VOLUNTEER)
                .where(Volunteer.VOLUNTEER.EXTERNALIDENTIFIER.eq(externalIdentifier))
                .fetchOne(0, Integer.class);
    }

    public VolunteerPojo getPojo(String externalIdentifier) {
        return null;
    }

    public boolean insertPojo(VolunteerPojo volunteerPojo) {
        if (!generateIds(volunteerPojo)) {
            // Some kind of error message?
            return false;
        }

        try {
            super.insert(volunteerPojo);
        } catch (Exception e) {
            Log.error("Could not insert volunteer", e);
            return false;
        }

        return true;
    }

    /**
     * Count how many volunteers there are in the database with a VolunteerInstance.dateStart
     * between minimumDate and maximumDate. If hasTraining is non-null, add criteria that it must match.
     * @param minimumDate
     * @param maximumDate
     * @param hasTraining
     * @return
     */
    public int countByDateStart(@Nonnull Date minimumDate, @Nonnull Date maximumDate, @Nullable Boolean hasTraining) {
        SelectConditionStep<Record1<Integer>> query = using(configuration())
                .selectCount()
                .from(Volunteer.VOLUNTEER)
                // Select the miminum dateStart from the VolunteerInstances that connect to this Volunteer.
                .where(using(configuration()).select(Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.min())
                        .from(Volunteerinstance.VOLUNTEERINSTANCE)
                        .where(Volunteer.VOLUNTEER.VOLUNTEERID.eq(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERID))
                        // Check whether this date is between the minimumDate and maximumDate
                        .asField().between(minimumDate, maximumDate));

        if (hasTraining != null) {
            query.and(Volunteer.VOLUNTEER.HASTRAINING.eq(hasTraining));
        }

        // Return the single row integer.
        return query.fetchOne(0, int.class);
    }

    /**
     * Count how many volunteers are active for at least one day in the period minimumDate-maximumDate.
     * If hasTraining is non-null, add criteria that it must match.
     * A volunteer is considered active on date X if there is some volunteerInstance for which holds:
     * 1. dateStart <= x
     * 2. dateEnd is null or x <= dateEnd
     * @param minimumDate
     * @param maximumDate
     * @param hasTraining
     * @return
     */
    public int countActiveInPeriod(@Nonnull Date minimumDate, @Nonnull Date maximumDate, @Nullable Boolean hasTraining) {
        SelectConditionStep<Record1<Integer>> query = using(configuration())
                // Since we use a join on instance, we must count distinct number of ID's.
                .select(Volunteer.VOLUNTEER.VOLUNTEERID.countDistinct())
                .from(Volunteer.VOLUNTEER)
                .join(Volunteerinstance.VOLUNTEERINSTANCE)
                    .on(Volunteer.VOLUNTEER.VOLUNTEERID.eq(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERID))
                .where(
                    // dateStart between minimumDate and maximumDate
                    Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.between(minimumDate, maximumDate)

                    // dateEnd between minimumDate and maximumDate
                    // Will give unknown if dateEnd is null, which is fine since it is part of an or-cause.
                    .or(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.between(minimumDate, maximumDate))

                    // dateStart <= min and (dateEnd is null || min <= dateEnd)
                    .or(Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.le(minimumDate)
                                .and(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.isNull()
                                    .or(DSL.val(minimumDate).le(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND))))

                    // dateStart <= max and (dateEnd is null || max <= dateEnd)
                    .or(Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.le(maximumDate)
                            .and(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.isNull()
                                    .or(DSL.val(maximumDate).le(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND))))
                );

        if (hasTraining != null) {
            query.and(Volunteer.VOLUNTEER.HASTRAINING.eq(hasTraining));
        }

        // Return the single row integer.
        return query.fetchOne(0, int.class);
    }

    @Override
    public List<VolunteerPojo> findAll() {
        return super.findAll();
    }

    public List<VolunteerPojo> fetch50MostRecent() {
        return using(configuration())
                .selectFrom(Volunteer.VOLUNTEER)
                .orderBy(Volunteer.VOLUNTEER.VOLUNTEERID.desc())
                .limit(50)
                .fetch()
                .map(mapper());

    }

    /**
     * The input gets split into several strings with space as delimiter.
     * Return any row in Volunteer for which each string is found in either firstName, insertion or lastName.
     * @param input Hopefully trimmed input?
     * @return
     */
    public List<VolunteerPojo> searchOnName(String input) {
        // Change input to lowercase (and also in search query) so casing is ignored.
        String[] searchStrings = input.toLowerCase().split(" ");

        org.jooq.SelectWhereStep<VolunteerRecord> query = using(configuration()).selectFrom(Volunteer.VOLUNTEER);
        for (String s : searchStrings) {
            query.where(
                    Volunteer.VOLUNTEER.FIRSTNAME.lower().like(s + "%")
                    .or(Volunteer.VOLUNTEER.INSERTION.lower().like(s + "%"))
                    .or(Volunteer.VOLUNTEER.LASTNAME.lower().like(s + "%"))
            );
        }

        // Return the most recent 50 rows.
        return query.orderBy(Volunteer.VOLUNTEER.VOLUNTEERID.desc()).limit(50).fetch().map(mapper());
    }

    public boolean isActive(int volunteerId) {
        int isActive = using(configuration())
                .selectCount()
                .from(Volunteerinstance.VOLUNTEERINSTANCE)
                .where(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERID.eq(volunteerId))
                // dateStart <= current_date and (dateEnd is null || current_date <= dateEnd)
                .and(Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.le(DSL.currentDate()))
                .and(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.isNull()
                        .or(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.ge(DSL.currentDate())))
                .fetchOne(0, int.class);

        // Return if at least one row is found.
        return (isActive >= 1);
    }

    public boolean isLastDayActive(int volunteerId) {
        int isActive = using(configuration())
                .selectCount()
                .from(Volunteerinstance.VOLUNTEERINSTANCE)
                .where(Volunteerinstance.VOLUNTEERINSTANCE.VOLUNTEERID.eq(volunteerId))
                // dateStart <= current_date and (dateEnd is null || current_date < dateEnd)
                .and(Volunteerinstance.VOLUNTEERINSTANCE.DATESTART.le(DSL.currentDate()))
                .and(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.isNull()
                        .or(Volunteerinstance.VOLUNTEERINSTANCE.DATEEND.greaterThan(DSL.currentDate())))
                .fetchOne(0, int.class);

        // Return if at least one row is found.
        return (isActive >= 1);
    }

}
