package com.apon.taalmaatjes.backend.api.returns;

import java.sql.Date;

public class TaskReturn {
    private String taskExtId;
    private String title;
    private String description;
    private String volunteerExtId;
    private Boolean isFinished;
    private Date dateToBeFinished;

    public String getTaskExtId() {
        return taskExtId;
    }

    public void setTaskExtId(String taskExtId) {
        this.taskExtId = taskExtId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVolunteerExtId() {
        return volunteerExtId;
    }

    public void setVolunteerExtId(String volunteerExtId) {
        this.volunteerExtId = volunteerExtId;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public Date getDateToBeFinished() {
        return dateToBeFinished;
    }

    public void setDateToBeFinished(Date dateToBeFinished) {
        this.dateToBeFinished = dateToBeFinished;
    }
}
