package com.apon.taalmaatjes.backend.facade;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;

import java.sql.Date;

public class ReportingFacade {
    protected Context context;

    public ReportingFacade(Context context) {
        this.context = context;
    }

    /**
     * Create a report that counts how many new entities and active entities there are between (and including)
     * dateStart and dateEnd.
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public ReportResult createReport(Date dateStart, Date dateEnd) {
        ReportResult report = new ReportResult(dateStart, dateEnd);

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context.getConfiguration());
        report.setNrOfNewVolunteers(volunteerMyDao.countByDateStart(dateStart, dateEnd, Boolean.TRUE));
        report.setNrOfActiveVolunteers(volunteerMyDao.countActiveInPeriod(dateStart, dateEnd, Boolean.TRUE));

        StudentMyDao studentMyDao = new StudentMyDao(context.getConfiguration());
        report.setNrOfNewStudents(studentMyDao.countNewStudents(dateStart, dateEnd, false));
        report.setNrOfActiveStudents(studentMyDao.countActiveInPeriod(dateStart, dateEnd, false));

        report.setNrOfNewGroups(studentMyDao.countNewStudents(dateStart, dateEnd, true));
        report.setNrOfActiveGroups(studentMyDao.countActiveInPeriod(dateStart, dateEnd, true));

        return report;
    }
}
