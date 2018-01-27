package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;

public class StudentMapper {
    StudentReturn studentReturn;

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
}
