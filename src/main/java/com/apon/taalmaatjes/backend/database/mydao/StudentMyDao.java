package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Student;
import com.apon.taalmaatjes.backend.database.generated.tables.Volunteermatch;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.StudentDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import org.jooq.Configuration;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;

import static org.jooq.impl.DSL.using;

public class StudentMyDao extends StudentDao {
    public final static Integer STARTING_ID = Integer.valueOf(5001);

    public StudentMyDao(Context context) {
        super(context.getConfiguration());
    }

    @Deprecated
    public StudentMyDao(Configuration configuration) {
        super(configuration);
    }

    public boolean generateIds(StudentPojo studentPojo) {
        // Do not generate id if it is already filled.
        if (studentPojo.getStudentid() != null) {
            return true;
        }

        Integer maxId = getMaxInteger();
        studentPojo.setStudentid(maxId != null ? maxId + 1 : STARTING_ID);

        return true;
    }

    protected Integer getMaxInteger() {
        return using(configuration())
                .select(Student.STUDENT.STUDENTID.max())
                .from(Student.STUDENT)
                .fetchOne(0, Integer.class);
    }

    @Override
    public void insert(StudentPojo studentPojo) {
        if (!generateIds(studentPojo)) {
            // Some kind of error message?
            return;
        }

        super.insert(studentPojo);
    }

    public String getExtIdFromId(Integer studentId) {
        return using(configuration())
                .select(Student.STUDENT.EXTERNALIDENTIFIER)
                .from(Student.STUDENT)
                .where(Student.STUDENT.STUDENTID.eq(studentId))
                .fetchOne(0, String.class);
    }

    /**
     * Count how many students have their first dateStart from a match between minimumDate and maximumDate.
     * If isGroup is non-null, add criteria that it must match.
     * @param minimumDate
     * @param maximumDate
     * @param isGroup
     * @return
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
}
