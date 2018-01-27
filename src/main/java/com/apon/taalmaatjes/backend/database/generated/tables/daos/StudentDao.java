/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.daos;


import com.apon.taalmaatjes.backend.database.generated.tables.Student;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.StudentRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StudentDao extends DAOImpl<StudentRecord, StudentPojo, Integer> {

    /**
     * Create a new StudentDao without any configuration
     */
    public StudentDao() {
        super(Student.STUDENT, StudentPojo.class);
    }

    /**
     * Create a new StudentDao with an attached configuration
     */
    public StudentDao(Configuration configuration) {
        super(Student.STUDENT, StudentPojo.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(StudentPojo object) {
        return object.getStudentid();
    }

    /**
     * Fetch records that have <code>STUDENTID IN (values)</code>
     */
    public List<StudentPojo> fetchByStudentid(Integer... values) {
        return fetch(Student.STUDENT.STUDENTID, values);
    }

    /**
     * Fetch a unique record that has <code>STUDENTID = value</code>
     */
    public StudentPojo fetchOneByStudentid(Integer value) {
        return fetchOne(Student.STUDENT.STUDENTID, value);
    }

    /**
     * Fetch records that have <code>EXTERNALIDENTIFIER IN (values)</code>
     */
    public List<StudentPojo> fetchByExternalidentifier(String... values) {
        return fetch(Student.STUDENT.EXTERNALIDENTIFIER, values);
    }

    /**
     * Fetch a unique record that has <code>EXTERNALIDENTIFIER = value</code>
     */
    public StudentPojo fetchOneByExternalidentifier(String value) {
        return fetchOne(Student.STUDENT.EXTERNALIDENTIFIER, value);
    }

    /**
     * Fetch records that have <code>FIRSTNAME IN (values)</code>
     */
    public List<StudentPojo> fetchByFirstname(String... values) {
        return fetch(Student.STUDENT.FIRSTNAME, values);
    }

    /**
     * Fetch records that have <code>INSERTION IN (values)</code>
     */
    public List<StudentPojo> fetchByInsertion(String... values) {
        return fetch(Student.STUDENT.INSERTION, values);
    }

    /**
     * Fetch records that have <code>LASTNAME IN (values)</code>
     */
    public List<StudentPojo> fetchByLastname(String... values) {
        return fetch(Student.STUDENT.LASTNAME, values);
    }

    /**
     * Fetch records that have <code>ISLOOKINGFORVOLUNTEER IN (values)</code>
     */
    public List<StudentPojo> fetchByIslookingforvolunteer(Boolean... values) {
        return fetch(Student.STUDENT.ISLOOKINGFORVOLUNTEER, values);
    }

    /**
     * Fetch records that have <code>ISGROUP IN (values)</code>
     */
    public List<StudentPojo> fetchByIsgroup(Boolean... values) {
        return fetch(Student.STUDENT.ISGROUP, values);
    }
}
