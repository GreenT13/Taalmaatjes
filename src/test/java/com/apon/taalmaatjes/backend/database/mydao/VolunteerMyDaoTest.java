package com.apon.taalmaatjes.backend.database.mydao;

import base.BaseTest;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertEquals;

public class VolunteerMyDaoTest extends BaseTest {
    VolunteerMyDao volunteerMyDao;

//    @Before
//    public void initializeMyDao() {
//        volunteerMyDao = new VolunteerMyDao(configuration);
//    }
//
//    @Test
//    public void generateIds() {
//        // Variable which I will empty every time to check if it gets filled correctly.
//        VolunteerPojo volunteerPojo = new VolunteerPojo();
//
//        // Test that with an empty table, the id equals the starting id.
//        volunteerMyDao.generateIds(volunteerPojo);
//        assertEquals(volunteerPojo.getVolunteerid(), VolunteerMyDao.STARTING_ID);
//
//        // Test that with a non-empty table, the id equals the number of items + starting id.
//        dummy.addVolunteer();
//        volunteerPojo.setVolunteerid(null);
//        volunteerMyDao.generateIds(volunteerPojo);
//        assertEquals(volunteerPojo.getVolunteerid(), Integer.valueOf(VolunteerMyDao.STARTING_ID + 1));
//
//        // Test it for two items in the database.
//        dummy.addVolunteer();
//        volunteerPojo.setVolunteerid(null);
//        volunteerMyDao.generateIds(volunteerPojo);
//        assertEquals(volunteerPojo.getVolunteerid(), Integer.valueOf(VolunteerMyDao.STARTING_ID + 2));
//    }
//
//    @Test
//    public void countByDateStart() {
//        // Test that result is 0 on empty database.
//        int result = 0;
//        String s1 = "2018-01-02";
//        String s2 = "2018-02-02";
//        Date d1 = DateTimeUtil.parseDate(s1);
//        Date d2 = DateTimeUtil.parseDate(s2);
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result is 0 on dates that do not lie in the window.
//        Integer volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, "2017-01-01", "2018-01-01");
//        dummy.addVolunteerinstance(volunteerId, "2018-02-03", null);
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result is increased by 1 on date that lies on the edges.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, s1); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s2, s2); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, s2); result++;
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result increases by only one if we have several instances inside the date range.
//        volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, s1, s1);
//        dummy.addVolunteerinstance(volunteerId, s1, s2);
//        dummy.addVolunteerinstance(volunteerId, s2, s2);
//        result++;
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result increases by one if dateEnd is null but with overlap.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s2, null); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, null); result++;
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result increases by one if range is completely inside other  range.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2018-01-03", "2018-02-02"); result++;
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result does not increase when the first start is outside the range, but with an instance inside the range.
//        volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, "2017-01-01", "2017-01-01");
//        dummy.addVolunteerinstance(volunteerId, s1, s2);
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that result does increases when dateEnd is out of the range.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2018-01-10", "2018-02-10"); result++;
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//        // Test that the result does not increase when dateStart is out of range,  but dateEnd inside the range.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2017-01-10", "2018-01-10");
//        assertEquals(volunteerMyDao.countByDateStart(d1, d2, null), result);
//
//    }
//
//    @Test
//    public void countActiveInPeriod() {
//        // Test that result is 0 on empty database.
//        int result = 0;
//        String s1 = "2018-01-02";
//        String s2 = "2018-02-02";
//        String s3 = "2018-03-02";
//        Date d1 = DateTimeUtil.parseDate(s1);
//        Date d2 = DateTimeUtil.parseDate(s2);
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result is 0 on dates that do not lie in the window.
//        Integer volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, "2017-01-01", "2018-01-01");
//        dummy.addVolunteerinstance(volunteerId, "2018-02-03", null);
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result is increased by 1 on date that lies on the edges.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, s1); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s2, s2); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, s2); result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result increases by only one if we have several instances inside the date range.
//        volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, s1, s1);
//        dummy.addVolunteerinstance(volunteerId, s1, s2);
//        dummy.addVolunteerinstance(volunteerId, s2, s2);
//        result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result increases by one if dateEnd is null but with overlap.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s2, null); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), s1, null); result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result increases by one if range is completely inside other  range.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2018-01-03", "2018-02-02"); result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result does increases when either dateStart or dateEnd is outside the range.
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2018-01-10", "2018-02-10"); result++;
//        dummy.addVolunteerinstance(dummy.addVolunteer().getVolunteerid(), "2017-01-10", "2018-01-10"); result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//
//        // Test that result increases when the first start is outside the range, but with an instance inside the range.
//        volunteerId = dummy.addVolunteer().getVolunteerid();
//        dummy.addVolunteerinstance(volunteerId, "2017-01-01", "2017-01-01");
//        dummy.addVolunteerinstance(volunteerId, s1, s2);
//        result++;
//        assertEquals(volunteerMyDao.countActiveInPeriod(d1, d2, null), result);
//    }
}