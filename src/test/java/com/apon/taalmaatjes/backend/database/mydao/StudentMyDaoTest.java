package com.apon.taalmaatjes.backend.database.mydao;

import base.BaseTest;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;

public class StudentMyDaoTest extends BaseTest {
    private StudentMyDao studentMyDao;

    @Before
    public void initializeMyDao() {
        studentMyDao = new StudentMyDao(context.getConfiguration());
    }

    @Test
    public void countNewStudents() {
        // Test that result is 0 on empty database.
        int resultGroup = 0;
        int resultStudent = 0;
        String s1 = "2018-01-02";
        String s2 = "2018-02-02";
        Date d1 = DateTimeUtil.parseDate(s1);
        Date d2 = DateTimeUtil.parseDate(s2);
        assertCountNewStudents(d1, d2, resultGroup, resultStudent);

        // Test that result is 0 on dates that do not lie in the window.
        dummy.addStudentVolunteerWithMatch(true, "2017-01-01", "2018-01-02");
        dummy.addStudentVolunteerWithMatch(true, "2018-02-03", "2018-02-05");
        dummy.addStudentVolunteerWithMatch(true, "2018-02-03", null);
        dummy.addStudentVolunteerWithMatch(false, "2017-01-01", "2018-01-02");
        dummy.addStudentVolunteerWithMatch(false, "2018-02-03", "2018-02-05");
        dummy.addStudentVolunteerWithMatch(false, "2018-02-03", null);
        assertCountNewStudents(d1, d2, resultGroup, resultStudent);

        // Test that result is increased by 1 on date that lies on the edges.
        dummy.addStudentVolunteerWithMatch(true, s1, s1); resultGroup++;
        dummy.addStudentVolunteerWithMatch(true, s1, s2); resultGroup++;
        dummy.addStudentVolunteerWithMatch(true, s2, s2); resultGroup++;
        dummy.addStudentVolunteerWithMatch(false, s1, s1); resultStudent++;
        dummy.addStudentVolunteerWithMatch(false, s1, s2); resultStudent++;
        dummy.addStudentVolunteerWithMatch(false, s2, s2); resultStudent++;
        assertCountNewStudents(d1, d2, resultGroup, resultStudent);

        // Test that result increases by only one if we have several instances inside the date range.
        Integer groupId = dummy.addStudent(true).getStudentid();
        Integer studentId = dummy.addStudent(false).getStudentid();
        Integer volunteerId = dummy.addVolunteer().getVolunteerid();
        dummy.addCreateDummyVolunteermatch(volunteerId, groupId, s1, s1); resultGroup++;
        dummy.addCreateDummyVolunteermatch(volunteerId, groupId, s1, s2);
        dummy.addCreateDummyVolunteermatch(volunteerId, groupId, s2, s2);
        dummy.addCreateDummyVolunteermatch(volunteerId, studentId, s1, s1); resultStudent++;
        dummy.addCreateDummyVolunteermatch(volunteerId, studentId, s1, s2);
        dummy.addCreateDummyVolunteermatch(volunteerId, studentId, s2, s2);
        assertCountNewStudents(d1, d2, resultGroup, resultStudent);
    }

    private void assertCountNewStudents(Date d1, Date d2, int resultGroup, int resultStudent) {
        assertEquals(studentMyDao.countNewStudents(d1, d2), resultGroup);
        assertEquals(studentMyDao.countNewStudents(d1, d2), resultStudent);
        assertEquals(studentMyDao.countNewStudents(d1, d2), resultGroup + resultStudent);
    }

    @Test
    public void countActiveInPeriod() {

    }
}