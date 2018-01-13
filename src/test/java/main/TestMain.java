package main;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.facade.Report;
import com.apon.taalmaatjes.backend.facade.ReportingFacade;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;

public class TestMain {

    public static void main(String[] args) {
        Context context = new Context();

//        Report report = ReportingFacade.createReport(DateTimeUtil.parseDate("2018-03-01"),
//                DateTimeUtil.parseDate("2018-12-01"),
//                context.getConfiguration());
//        System.out.println(report.getNrOfActiveVolunteers());
//        System.out.println(report.getNrOfNewVolunteers());
    }

}
