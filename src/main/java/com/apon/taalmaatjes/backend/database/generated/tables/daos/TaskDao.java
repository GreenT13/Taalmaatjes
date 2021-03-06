/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.daos;


import com.apon.taalmaatjes.backend.database.generated.tables.Task;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.TaskPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.TaskRecord;

import java.sql.Date;
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
public class TaskDao extends DAOImpl<TaskRecord, TaskPojo, Integer> {

    /**
     * Create a new TaskDao without any configuration
     */
    public TaskDao() {
        super(Task.TASK, TaskPojo.class);
    }

    /**
     * Create a new TaskDao with an attached configuration
     */
    public TaskDao(Configuration configuration) {
        super(Task.TASK, TaskPojo.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(TaskPojo object) {
        return object.getTaskid();
    }

    /**
     * Fetch records that have <code>TASKID IN (values)</code>
     */
    public List<TaskPojo> fetchByTaskid(Integer... values) {
        return fetch(Task.TASK.TASKID, values);
    }

    /**
     * Fetch a unique record that has <code>TASKID = value</code>
     */
    public TaskPojo fetchOneByTaskid(Integer value) {
        return fetchOne(Task.TASK.TASKID, value);
    }

    /**
     * Fetch records that have <code>EXTERNALIDENTIFIER IN (values)</code>
     */
    public List<TaskPojo> fetchByExternalidentifier(String... values) {
        return fetch(Task.TASK.EXTERNALIDENTIFIER, values);
    }

    /**
     * Fetch a unique record that has <code>EXTERNALIDENTIFIER = value</code>
     */
    public TaskPojo fetchOneByExternalidentifier(String value) {
        return fetchOne(Task.TASK.EXTERNALIDENTIFIER, value);
    }

    /**
     * Fetch records that have <code>TITLE IN (values)</code>
     */
    public List<TaskPojo> fetchByTitle(String... values) {
        return fetch(Task.TASK.TITLE, values);
    }

    /**
     * Fetch records that have <code>DESCRIPTION IN (values)</code>
     */
    public List<TaskPojo> fetchByDescription(String... values) {
        return fetch(Task.TASK.DESCRIPTION, values);
    }

    /**
     * Fetch records that have <code>VOLUNTEERID IN (values)</code>
     */
    public List<TaskPojo> fetchByVolunteerid(Integer... values) {
        return fetch(Task.TASK.VOLUNTEERID, values);
    }

    /**
     * Fetch records that have <code>ISFINISHED IN (values)</code>
     */
    public List<TaskPojo> fetchByIsfinished(Boolean... values) {
        return fetch(Task.TASK.ISFINISHED, values);
    }

    /**
     * Fetch records that have <code>DATETOBEFINISHED IN (values)</code>
     */
    public List<TaskPojo> fetchByDatetobefinished(Date... values) {
        return fetch(Task.TASK.DATETOBEFINISHED, values);
    }
}
