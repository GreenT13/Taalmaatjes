package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;

import java.util.ArrayList;
import java.util.List;

public class StudentMapper {
    private StudentReturn studentReturn;

    public StudentMapper(StudentReturn studentReturn) {
        this.studentReturn = studentReturn;
    }
    public StudentMapper() {
        studentReturn = new StudentReturn();
    }

    public StudentReturn getStudentReturn() {
        return studentReturn;
    }

    public void setStudent(StudentPojo studentPojo) {
        studentReturn.setExternalIdentifier(studentPojo.getExternalidentifier());
        studentReturn.setFirstName(studentPojo.getFirstname());
        studentReturn.setInsertion(studentPojo.getInsertion());
        studentReturn.setLastName(studentPojo.getLastname());
        studentReturn.setLookingForVolunteer(studentPojo.getIslookingforvolunteer());
        studentReturn.setGroup(studentPojo.getIsgroup());
    }

    private void addMatch(VolunteermatchPojo volunteermatchPojo, VolunteerMyDao volunteerMyDao) {
        VolunteerMatchReturn volunteerMatchReturn = new VolunteerMatchReturn();
        volunteerMatchReturn.setStudentExtId(studentReturn.getExternalIdentifier());
        volunteerMatchReturn.setExternalIdentifier(volunteermatchPojo.getExternalidentifier());
        volunteerMatchReturn.setDateStart(volunteermatchPojo.getDatestart());
        volunteerMatchReturn.setDateEnd(volunteermatchPojo.getDateend());

        // Set the volunteer.
        VolunteerMapper volunteerMapper = new VolunteerMapper();
        volunteerMatchReturn.setVolunteerExtId(volunteerMyDao.fetchOneByVolunteerid(volunteermatchPojo.getVolunteerid()).getExternalidentifier());

        studentReturn.getListVolunteerMatch().add(volunteerMatchReturn);
    }

    public void setMatchList(List<VolunteermatchPojo> volunteermatchPojoList, VolunteerMyDao volunteerMyDao) {
        List<VolunteerMatchReturn> listVolunteerMatchReturn = new ArrayList();
        studentReturn.setListVolunteerMatch(listVolunteerMatchReturn);

        for (VolunteermatchPojo volunteermatchPojo : volunteermatchPojoList) {
            addMatch(volunteermatchPojo, volunteerMyDao);
        }
    }

    public StudentPojo getPojo(Integer studentId) {
        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setStudentid(studentId);
        studentPojo.setExternalidentifier(studentReturn.getExternalIdentifier());
        studentPojo.setFirstname(studentReturn.getFirstName());
        studentPojo.setInsertion(studentReturn.getInsertion());
        studentPojo.setLastname(studentReturn.getLastName());
        studentPojo.setIslookingforvolunteer(studentReturn.getLookingForVolunteer());
        studentPojo.setIsgroup(studentReturn.getGroup());

        return studentPojo;
    }
}
