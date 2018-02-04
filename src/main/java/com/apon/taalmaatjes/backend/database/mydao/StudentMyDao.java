package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Student;
import com.apon.taalmaatjes.backend.database.generated.tables.Volunteermatch;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.StudentDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.StudentRecord;
import com.apon.taalmaatjes.backend.database.generated.tables.records.VolunteermatchRecord;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.log.Log;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.util.mysql.MySQLDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.util.List;

import static org.jooq.impl.DSL.using;

public class StudentMyDao extends StudentDao {
    private final static Integer STARTING_EXT_ID = 5001;

    public StudentMyDao(Context context) {
        super(context.getConfiguration());
    }

    @Deprecated
    public StudentMyDao(Configuration configuration) {
        super(configuration);
    }

    @SuppressWarnings("Duplicates")
    private boolean generateIds(StudentPojo studentPojo) {
        if (studentPojo.getStudentid() == null) {
            Integer maxId = getMaxId();
            studentPojo.setStudentid(maxId != null ? maxId + 1 : 0);
        }

        if (studentPojo.getExternalidentifier() == null) {
            String maxExtId = getMaxExtId();
            if (maxExtId == null) {
                maxExtId = STARTING_EXT_ID.toString();
            } else {
                maxExtId = String.valueOf(Integer.valueOf(maxExtId) + 1);
            }
            studentPojo.setExternalidentifier(maxExtId);
        }

        return true;
    }

    private Integer getMaxId() {
        return using(configuration())
                .select(Student.STUDENT.STUDENTID.max())
                .from(Student.STUDENT)
                .fetchOne(0, Integer.class);
    }

    private String getMaxExtId() {
        return using(configuration())
                .select(Student.STUDENT.EXTERNALIDENTIFIER.cast(MySQLDataType.INT).max())
                .from(Student.STUDENT)
                .fetchOne(0, String.class);
    }

    public Integer getIdFromExtId(String externalIdentifier) {
        return using(configuration())
                .select(Student.STUDENT.STUDENTID)
                .from(Student.STUDENT)
                .where(Student.STUDENT.EXTERNALIDENTIFIER.eq(externalIdentifier))
                .fetchOne(0, Integer.class);
    }

    public boolean insertPojo(StudentPojo studentPojo) {
        if (!generateIds(studentPojo)) {
            // Some kind of logError message?
            return false;
        }

        try {
            super.insert(studentPojo);
        } catch (Exception e) {
            Log.logError("Could not insert student", e);
            return false;
        }

        return true;
    }

    /**
     * Count how many students have their first dateStart from a match between minimumDate and maximumDate.
     * If isGroup is non-null, add criteria that it must match.
     * @param minimumDate Minimum start date.
     * @param maximumDate Maximum start date.
     * @param isGroup Whether Student.isGroup is true or false.
     * @return Integer value of students that satisfy the criteria.
     */
    public int countNewStudents(@Nonnull Date minimumDate, @Nonnull Date maximumDate, @Nullable Boolean isGroup) {
        SelectConditionStep<Record1<Integer>> query =  using(configuration())
                .selectCount()
                .from(Student.STUDENT)
                .where(
                        using(configuration()).select(Volunteermatch.VOLUNTEERMATCH.DATESTART.min())
                                .from(Volunteermatch.VOLUNTEERMATCH)
                                .where(Volunteermatch.VOLUNTEERMATCH.STUDENTID.eq(Student.STUDENT.STUDENTID))
                                // Check whether this date is between the minimumDate and maximumDate
                                .asField().between(minimumDate, maximumDate));

        if (isGroup != null) {
            query.and(Student.STUDENT.ISGROUP.eq(isGroup));
        }

        return query.fetchOne(0, int.class);
    }

    /**
     * Count how many students are active for at least one day in the period minimumDate-maximumDate.
     * If isGroup is non-null, add criteria that it must match.
     * A student is considered active on date X if there is some volunteerMatch for which holds:
     * 1. dateStart <= x
     * 2. dateEnd is null or x <= dateEnd
     * @param minimumDate
     * @param maximumDate
     * @param isGroup
     * @return
     */
    public int countActiveInPeriod(@Nonnull Date minimumDate, @Nonnull Date maximumDate, @Nullable Boolean isGroup) {
        SelectConditionStep<Record1<Integer>> query = using(configuration())
                // Since we use a join on instance, we must count distinct number of ID's.
                .select(Student.STUDENT.STUDENTID.countDistinct())
                .from(Student.STUDENT)
                .join(Volunteermatch.VOLUNTEERMATCH)
                .on(Student.STUDENT.STUDENTID.eq(Volunteermatch.VOLUNTEERMATCH.STUDENTID))
                .where(
                        // dateStart between minimumDate and maximumDate
                        Volunteermatch.VOLUNTEERMATCH.DATESTART.between(minimumDate, maximumDate)

                        // dateEnd between minimumDate and maximumDate
                        // Will give unknown if dateEnd is null, which is fine since it is part of an or-cause.
                        .or(Volunteermatch.VOLUNTEERMATCH.DATEEND.between(minimumDate, maximumDate))

                        // dateStart <= min and (dateEnd is null || min <= dateEnd)
                        .or(Volunteermatch.VOLUNTEERMATCH.DATESTART.le(minimumDate)
                                .and(Volunteermatch.VOLUNTEERMATCH.DATEEND.isNull()
                                        .or(DSL.val(minimumDate).le(Volunteermatch.VOLUNTEERMATCH.DATEEND))))

                        // dateStart <= max and (dateEnd is null || max <= dateEnd)
                        .or(Volunteermatch.VOLUNTEERMATCH.DATESTART.le(maximumDate)
                                .and(Volunteermatch.VOLUNTEERMATCH.DATEEND.isNull()
                                        .or(DSL.val(maximumDate).le(Volunteermatch.VOLUNTEERMATCH.DATEEND))))
                );

        if (isGroup != null) {
            query.and(Student.STUDENT.ISGROUP.eq(isGroup));
        }

        // Return the single row integer.
        return query.fetchOne(0, int.class);
    }

    /**
     * Search for students based on non-null inputs.
     * @param input
     * @param isLookingForVolunteer
     * @param isGroup
     * @param hasMatch
     * @return
     */
    public List<StudentPojo> advancedSearch(String input, Boolean isLookingForVolunteer, Boolean isGroup, Boolean hasMatch) {
        SelectWhereStep<StudentRecord> query = using(configuration()).selectFrom(Student.STUDENT);

        // Add the input to search criteria.
        if (input != null && input.trim().length() > 0) {
            String[] searchStrings = input.toLowerCase().split(" ");
            for (String s : searchStrings) {
                query.where(
                        Student.STUDENT.FIRSTNAME.lower().like(s + "%")
                                .or(Student.STUDENT.INSERTION.lower().like(s + "%"))
                                .or(Student.STUDENT.LASTNAME.lower().like(s + "%"))
                );
            }
        }

        if (isLookingForVolunteer != null) {
            query.where(Student.STUDENT.ISLOOKINGFORVOLUNTEER.eq(isLookingForVolunteer));
        }

        if (isGroup != null) {
            query.where(Student.STUDENT.ISGROUP.eq(isGroup));
        }

        if (hasMatch != null) {
            Select<VolunteermatchRecord> subQuery =using(configuration()).selectFrom(Volunteermatch.VOLUNTEERMATCH)
                    .where(Volunteermatch.VOLUNTEERMATCH.STUDENTID.eq(Student.STUDENT.STUDENTID)
                            // dateStart <= current_date and (dateEnd is null || current_date <= dateEnd)
                            .and(Volunteermatch.VOLUNTEERMATCH.DATESTART.le(DSL.currentDate()))
                            .and(Volunteermatch.VOLUNTEERMATCH.DATEEND.isNull()
                                    .or(Volunteermatch.VOLUNTEERMATCH.DATEEND.ge(DSL.currentDate()))));

            if (hasMatch) {
                query.whereExists(subQuery);
            } else {
                query.whereNotExists(subQuery);
            }
        }

        return query.orderBy(Student.STUDENT.STUDENTID.desc()).limit(50).fetch().map(mapper());
    }

}
