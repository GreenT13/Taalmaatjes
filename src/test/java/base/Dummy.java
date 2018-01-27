package base;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerInstanceMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMatchMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import org.jooq.Configuration;

public class Dummy {
    protected Configuration configuration;

    protected Dummy(Configuration configuration) {
        this.configuration = configuration;
    }

    public VolunteerPojo addVolunteer() {
//        VolunteerMyDao volunteerMyDao = new VolunteerMyDao();
//        VolunteerPojo volunteerPojo = createDummyVolunteer();
//        volunteerMyDao.insert(volunteerPojo);
//        return volunteerPojo;
        return null;
    }

    public VolunteerinstancePojo addVolunteerinstance(Integer volunteerId, String dateStart, String dateEnd) {
        VolunteerInstanceMyDao volunteerMyDao = new VolunteerInstanceMyDao(configuration);
        VolunteerinstancePojo volunteerinstancePojo = createDummyVolunteerInstance(volunteerId, dateStart, dateEnd);
        volunteerMyDao.insert(volunteerinstancePojo);
        return volunteerinstancePojo;
    }

    public VolunteerPojo createDummyVolunteer() {
        VolunteerPojo volunteerPojo = new VolunteerPojo();
        volunteerPojo.setFirstname("Rico");
        volunteerPojo.setLastname("Apon");
        return volunteerPojo;
    }

    public VolunteerinstancePojo createDummyVolunteerInstance(Integer volunteerId) {
        return createDummyVolunteerInstance(volunteerId, "2018-01-01", null);
    }

    public VolunteerinstancePojo createDummyVolunteerInstance(Integer volunteerId, String dateStart, String dateEnd) {
        VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
        volunteerinstancePojo.setVolunteerid(volunteerId);
        volunteerinstancePojo.setDatestart(DateTimeUtil.parseDate(dateStart));
        if (dateEnd != null) {
            volunteerinstancePojo.setDateend(DateTimeUtil.parseDate(dateEnd));
        }
        return volunteerinstancePojo;
    }

    public StudentPojo addStudent(Boolean isGroup) {
        StudentMyDao studentMyDao = new StudentMyDao(configuration);
        StudentPojo studentPojo = createDummyStudent(isGroup);
        studentMyDao.insert(studentPojo);
        return studentPojo;
    }

    public VolunteermatchPojo addStudentVolunteerWithMatch(Boolean isGroup, String dateStart, String dateEnd) {
        VolunteermatchPojo volunteermatchPojo = createDummyVolunteermatch(addVolunteer().getVolunteerid(), addStudent(isGroup).getStudentid(), dateStart, dateEnd);
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(configuration);
        volunteerMatchMyDao.insert(volunteermatchPojo);
        return volunteermatchPojo;
    }

    public VolunteermatchPojo addCreateDummyVolunteermatch(Integer volunteerId, Integer studentId, String dateStart, String dateEnd) {
        VolunteermatchPojo volunteermatchPojo = createDummyVolunteermatch(volunteerId, studentId, dateStart, dateEnd);
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(configuration);
        volunteerMatchMyDao.insert(volunteermatchPojo);
        return volunteermatchPojo;
    }

    public VolunteermatchPojo createDummyVolunteermatch(Integer volunteerId, Integer studentId, String dateStart, String dateEnd) {
        VolunteermatchPojo volunteermatchPojo = new VolunteermatchPojo();
        volunteermatchPojo.setVolunteerid(volunteerId);
        volunteermatchPojo.setStudentid(studentId);
        volunteermatchPojo.setDatestart(DateTimeUtil.parseDate(dateStart));
        if (dateEnd!= null) {
            volunteermatchPojo.setDateend(DateTimeUtil.parseDate(dateEnd));
        }
        return volunteermatchPojo;
    }

    public StudentPojo createDummyStudent(Boolean isGroup) {
        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstname("First");
        studentPojo.setLastname("Last");
        studentPojo.setIsgroup(isGroup);
        return studentPojo;
    }
}
