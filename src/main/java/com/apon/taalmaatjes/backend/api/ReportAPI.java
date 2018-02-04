package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.ReportReturn;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.Date;
import java.sql.SQLException;

public class ReportAPI {
    private static ReportAPI ourInstance = new ReportAPI();

    public static ReportAPI getInstance() {
        return ourInstance;
    }

    private ReportAPI() { }

    /**
     * Create report based on dateStart and dateEnd.
     * @param dateStart The start date.
     * @param dateEnd The end date.
     * @return ReportReturn
     */
    public Result createReport(Date dateStart, Date dateEnd) {
        if (dateStart == null || dateEnd == null) {
            return ResultUtil.createError("ReportAPI.createReport.error.noDate");
        }

        if (dateStart.compareTo(dateEnd) > 0) {
            return ResultUtil.createError("ReportAPI.createReport.error.startBeforeEnd");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }

        Log.logDebug("Start ReportAPI.createReport from " + dateStart.toString() + " until " + dateEnd.toString());

        ReportReturn report = new ReportReturn(dateStart, dateEnd);

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        report.setNrOfNewVolunteers(volunteerMyDao.countByDateStart(dateStart, dateEnd, Boolean.TRUE));
        report.setNrOfActiveVolunteers(volunteerMyDao.countActiveInPeriod(dateStart, dateEnd, Boolean.TRUE));

        StudentMyDao studentMyDao = new StudentMyDao(context);
        report.setNrOfNewStudents(studentMyDao.countNewStudents(dateStart, dateEnd, false));
        report.setNrOfActiveStudents(studentMyDao.countActiveInPeriod(dateStart, dateEnd, false));

        report.setNrOfNewGroups(studentMyDao.countNewStudents(dateStart, dateEnd, true));
        report.setNrOfActiveGroups(studentMyDao.countActiveInPeriod(dateStart, dateEnd, true));

        // Close, log and return.
        context.close();
        Log.logDebug("End ReportAPI.createReport");
        return ResultUtil.createOk(report);
    }
}
