/*
 * This file is generated by jOOQ.
*/
package com.apon.taalmaatjes.backend.database.generated.tables.pojos;


import java.io.Serializable;
import java.sql.Date;

import javax.annotation.Generated;


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
public class TaskPojo implements Serializable {

    private static final long serialVersionUID = -1401908314;

    private Integer taskid;
    private String  externalidentifier;
    private String  title;
    private String  description;
    private Integer volunteerid;
    private Boolean isfinished;
    private Date    datetobefinished;

    public TaskPojo() {}

    public TaskPojo(TaskPojo value) {
        this.taskid = value.taskid;
        this.externalidentifier = value.externalidentifier;
        this.title = value.title;
        this.description = value.description;
        this.volunteerid = value.volunteerid;
        this.isfinished = value.isfinished;
        this.datetobefinished = value.datetobefinished;
    }

    public TaskPojo(
        Integer taskid,
        String  externalidentifier,
        String  title,
        String  description,
        Integer volunteerid,
        Boolean isfinished,
        Date    datetobefinished
    ) {
        this.taskid = taskid;
        this.externalidentifier = externalidentifier;
        this.title = title;
        this.description = description;
        this.volunteerid = volunteerid;
        this.isfinished = isfinished;
        this.datetobefinished = datetobefinished;
    }

    public Integer getTaskid() {
        return this.taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getExternalidentifier() {
        return this.externalidentifier;
    }

    public void setExternalidentifier(String externalidentifier) {
        this.externalidentifier = externalidentifier;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVolunteerid() {
        return this.volunteerid;
    }

    public void setVolunteerid(Integer volunteerid) {
        this.volunteerid = volunteerid;
    }

    public Boolean getIsfinished() {
        return this.isfinished;
    }

    public void setIsfinished(Boolean isfinished) {
        this.isfinished = isfinished;
    }

    public Date getDatetobefinished() {
        return this.datetobefinished;
    }

    public void setDatetobefinished(Date datetobefinished) {
        this.datetobefinished = datetobefinished;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TaskPojo (");

        sb.append(taskid);
        sb.append(", ").append(externalidentifier);
        sb.append(", ").append(title);
        sb.append(", ").append(description);
        sb.append(", ").append(volunteerid);
        sb.append(", ").append(isfinished);
        sb.append(", ").append(datetobefinished);

        sb.append(")");
        return sb.toString();
    }
}
