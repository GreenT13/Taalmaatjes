package com.apon.taalmaatjes.backend.facade;

import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import org.jooq.Configuration;

import java.sql.Date;

public class ReportingFacade {
    protected Configuration configuration;

    public ReportingFacade(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Create a report that counts how many new entities and active entities there are between (and including)
     * dateStart and dateEnd.
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public Report createReport(Date dateStart, Date dateEnd) {
        Report report = new Report(dateStart, dateEnd);

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(configuration);
        report.setNrOfNewVolunteers(volunteerMyDao.countByDateStart(dateStart, dateEnd, Boolean.TRUE));
        report.setNrOfActiveVolunteers(volunteerMyDao.countActiveInPeriod(dateStart, dateEnd, Boolean.TRUE));

        StudentMyDao studentMyDao = new StudentMyDao(configuration);
        report.setNrOfNewStudents(studentMyDao.countNewStudents(dateStart, dateEnd, false));
        report.setNrOfActiveStudents(studentMyDao.countActiveInPeriod(dateStart, dateEnd, false));

        report.setNrOfNewStudents(studentMyDao.countNewStudents(dateStart, dateEnd, true));
        report.setNrOfActiveStudents(studentMyDao.countActiveInPeriod(dateStart, dateEnd, true));

        return report;
    }
}
