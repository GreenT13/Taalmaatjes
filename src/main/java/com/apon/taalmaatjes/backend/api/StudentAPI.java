package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.StudentMapper;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMatchMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentAPI {
    private static StudentAPI ourInstance = new StudentAPI();

    public static StudentAPI getInstance() {
        return ourInstance;
    }

    private StudentAPI() { }

    /**
     * Get a student based on the external identifier.
     * @param externalIdentifier The external identifier from a student.
     * @return VolunteerReturn
     */
    public Result get(String externalIdentifier) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }

        Log.logDebug("Start StudentAPI.getVolunteer for externalIdentifier " + externalIdentifier);

        StudentMyDao studentMyDao = new StudentMyDao(context);

        // First check if the externalIdentifier is valid.
        Integer studentId = studentMyDao.getIdFromExtId(externalIdentifier);
        if (studentId == null) {
            return ResultUtil.createError("StudentAPI.error.noExtIdFound");
        }

        // Mapper to create the output.
        StudentMapper studentMapper = new StudentMapper();

        // Retrieve volunteer from the database.
        studentMapper.setStudent(studentMyDao.fetchOneByStudentid(studentId));

        // Retrieve all matches
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        studentMapper.setMatchList(volunteerMatchMyDao.getMatchForStudent(studentId, false), new VolunteerMyDao(context));

        // Close and return.
        context.close();
        Log.logDebug("End StudentAPI.getVolunteer");
        return ResultUtil.createOk(studentMapper.getStudentReturn());
    }

    /**
     * Update a student based on frontend object.
     * @param studentReturn The student.
     * @return nothing
     */
    public Result update(StudentReturn studentReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start StudentAPI.update for externalIdentifier " + studentReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (studentReturn.getLookingForVolunteer() == null) {
            return ResultUtil.createError("StudentAPI.update.error.fillIsLookingForVolunteer");
        }
        if (studentReturn.getExternalIdentifier() == null) {
            return ResultUtil.createError("StudentAPI.update.error.fillExtId");
        }
        if (studentReturn.getLastName() == null) {
            return ResultUtil.createError("StudentAPI.update.error.fillLastName");
        }

        StudentMyDao studentMyDao = new StudentMyDao(context);
        Integer studentId = studentMyDao.getIdFromExtId(studentReturn.getExternalIdentifier());
        if (studentId == null) {
            return ResultUtil.createError("StudentAPI.error.noExtIdFound");
        }

        // Volunteer is valid, so we map return to pojo.
        StudentMapper studentMapper = new StudentMapper(studentReturn);
        StudentPojo studentPojo = studentMapper.getPojo(studentId);

        studentMyDao.update(studentPojo);

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End StudentAPI.update");
        return ResultUtil.createOk();
    }

    /**
     * Add a student based on frontend object.
     * @param studentReturn The student.
     * @return external identifier from the added student.
     */
    public Result add(StudentReturn studentReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start StudentAPI.add for externalIdentifier " + studentReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (studentReturn.getLookingForVolunteer() == null) {
            return ResultUtil.createError("StudentAPI.add.error.fillIsLookingForVolunteer");
        }
        if (studentReturn.getLastName() == null) {
            return ResultUtil.createError("StudentAPI.add.error.fillLastName");
        }

        StudentMyDao studentMyDao = new StudentMyDao(context);
        StudentMapper studentMapper = new StudentMapper(studentReturn);

        // Insert also fills the external identifier we return later.
        StudentPojo studentPojo = studentMapper.getPojo(null);
        if (!studentMyDao.insertPojo(studentPojo)) {
            context.rollback();
            return ResultUtil.createError("StudentAPI.add.error.insertStudent");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End StudentAPI.add");
        return ResultUtil.createOk(studentPojo.getExternalidentifier());
    }

    /**
     * Search for volunteers that specify the given conditions, if they are filled.
     * @param input Search input for firstName, insertion, lastName.
     * @param isLookingForVolunteer Whether Student.isLookingForVolunteer must be true or false.
     * @param isGroup Whether Student.isGroup must be true or false.
     * @param hasMatch Whether there is a VolunteerMatch for the student.
     * @return List&lt;StudentReturn&gt; with the students found.
     */
    public Result advancedSearch(String input, Boolean isLookingForVolunteer, Boolean isGroup, Boolean hasMatch) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start StudentAPI.advancedSearch for input " + input + " isLookingForVolunteer " + isLookingForVolunteer
                + " isGroup " + isGroup + " hasMatch " + hasMatch);
        // Retrieve the list from the database.
        StudentMyDao studentMyDao = new StudentMyDao(context);
        List<StudentPojo> studentPojoList = studentMyDao.advancedSearch(input, isLookingForVolunteer, isGroup, hasMatch);

        // No connection is needed.
        context.close();

        // Convert the list of pojos to returns.
        List<StudentReturn> studentReturns = new ArrayList();
        for (StudentPojo studentPojo : studentPojoList) {
            StudentMapper studentMapper = new StudentMapper();
            studentMapper.setStudent(studentPojo);

            studentReturns.add(studentMapper.getStudentReturn());
        }

        // Return the list.
        Log.logDebug("End StudentAPI.searchByInput");
        return ResultUtil.createOk(studentReturns);
    }
}
