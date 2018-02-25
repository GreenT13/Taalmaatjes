package com.apon.taalmaatjes.backend.api.returns.mapper;

import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.TaskPojo;

public class TaskMapper {
    private TaskReturn taskReturn;

    public TaskMapper() {
        taskReturn = new TaskReturn();
    }

    public TaskMapper(TaskReturn taskReturn) {
        this.taskReturn = taskReturn;
    }

    public TaskReturn getTaskReturn() {
        return taskReturn;
    }

    public void setTask(TaskPojo taskPojo) {
        taskReturn.setTaskExtId(taskPojo.getExternalidentifier());
        taskReturn.setTitle(taskPojo.getTitle());
        taskReturn.setDescription(taskPojo.getDescription());
        taskReturn.setFinished(taskPojo.getIsfinished());
        taskReturn.setDateToBeFinished(taskPojo.getDatetobefinished());
    }

    public TaskPojo getPojo(Integer taskId, Integer volunteerId) {
        TaskPojo taskPojo = new TaskPojo();
        taskPojo.setTaskid(taskId);
        taskPojo.setExternalidentifier(taskReturn.getTaskExtId());
        taskPojo.setTitle(taskReturn.getTitle());
        taskPojo.setDescription(taskReturn.getDescription());
        taskPojo.setVolunteerid(volunteerId);
        taskPojo.setIsfinished(taskReturn.getFinished());
        taskPojo.setDatetobefinished(taskReturn.getDateToBeFinished());

        return taskPojo;
    }
}
