package com.apon.taalmaatjes.backend.facade;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;

public class StudentFacade {
    protected Context context;
    protected StudentMyDao studentMyDao;

    public StudentFacade (Context context) {
        this.context = context;
        studentMyDao = new StudentMyDao(context.getConfiguration());
    }

    public String getStudentName(int studentId) {
        StudentPojo studentPojo = studentMyDao.fetchOneByStudentid(studentId);
        if (studentPojo.getIsgroup()) {
            // Group is filled in lastName.
            return studentPojo.getLastname();
        } else {
            String studentName = "";

            if (studentPojo.getFirstname() != null) {
                studentName += studentPojo.getFirstname() + " ";
            }

            if (studentPojo.getInsertion() != null) {
                studentName += studentPojo.getInsertion() + " ";
            }

            // Last name is always filled.
            return studentName + studentPojo.getLastname();
        }
    }
}