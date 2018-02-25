package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.TaskMapper;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.TaskPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.TaskMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskAPI {
    private static TaskAPI ourInstance = new TaskAPI();

    public static TaskAPI getInstance() {
        return ourInstance;
    }

    private TaskAPI() { }

    /**
     * Get a task based on the external identifier.
     * @param taskExtId The external identifier.
     * @return TaskReturn.
     */
    public Result getTask(String taskExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaskAPI.getTask for taskExtId " + taskExtId);

        // Get taskId
        TaskMyDao taskMyDao = new TaskMyDao(context);
        Integer taskId = taskMyDao.getIdFromExtId(taskExtId);
        if (taskExtId == null) {
            return ResultUtil.createError("TaskAPI.error.noTaskExtIdFound");
        }

        TaskMapper taskMapper = new TaskMapper();
        TaskPojo taskPojo = taskMyDao.fetchOneByTaskid(taskId);
        taskMapper.setTask(taskPojo);

        // Get volunteerExtId
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        String volunteerExtId = volunteerMyDao.fetchOneByVolunteerid(taskPojo.getVolunteerid()).getExternalidentifier();
        taskMapper.getTaskReturn().setVolunteerExtId(volunteerExtId);

        // Close and return.
        context.close();
        Log.logDebug("End TaskAPI.getTask");
        return ResultUtil.createOk(taskMapper.getTaskReturn());
    }

    /**
     * Add a task based on the frontend object.
     * @param taskReturn The task.
     * @return external identifier from the added task.
     */
    public Result addTask(TaskReturn taskReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaskAPI.addTask for taskExtId " + taskReturn.getTaskExtId());

        // Check if it is a valid volunteer.
        if (taskReturn.getVolunteerExtId() == null) {
            return ResultUtil.createError("TaskAPI.error.fillVolunteerExtId");
        }

        TaskMyDao taskMyDao = new TaskMyDao(context);
        TaskMapper taskMapper = new TaskMapper(taskReturn);

        // Retrieve the volunteerId
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(taskReturn.getVolunteerExtId());
        if (volunteerId == null) {
            return ResultUtil.createError("TaskAPI.error.noVolunteerExtIdFound");
        }

        // Insert also fills the external identifier we return later.
        TaskPojo taskPojo = taskMapper.getPojo(null, volunteerId);
        if (!taskMyDao.insertPojo(taskPojo)) {
            context.rollback();
            return ResultUtil.createError("TaskAPI.addTask.error.insertTask");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End TaskAPI.addTask");
        return ResultUtil.createOk(taskPojo.getExternalidentifier());

    }

    /**
     * Update a task based on the frontend object.
     * @param taskReturn The task.
     * @return Nothing.
     */
    public Result updateTask(TaskReturn taskReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaskAPI.updateTask for taskExtId " + taskReturn.getTaskExtId());

        // Check if it is a valid volunteer.
        if (taskReturn.getTaskExtId() == null) {
            return ResultUtil.createError("TaskAPI.error.fillTaskExtId");
        }
        if (taskReturn.getVolunteerExtId() == null) {
            return ResultUtil.createError("TaskAPI.error.fillVolunteerExtId");
        }

        // Get taskId
        TaskMyDao taskMyDao = new TaskMyDao(context);
        Integer taskId = taskMyDao.getIdFromExtId(taskReturn.getTaskExtId());
        if (taskId == null) {
            return ResultUtil.createError("TaskAPI.error.noTaskExtIdFound");
        }

        // Get volunteerId
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(taskReturn.getVolunteerExtId());
        if (volunteerId == null) {
            return ResultUtil.createError("TaskAPI.error.noVolunteerExtIdFound");
        }

        // Task is valid, so we map return to pojo.
        TaskMapper taskMapper = new TaskMapper(taskReturn);
        TaskPojo taskPojo = taskMapper.getPojo(taskId, volunteerId);

        if (!taskMyDao.updatePojo(taskPojo)) {
            return ResultUtil.createError("TaskAPI.error.couldNotUpdate");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End TaskAPI.updateTask");
        return ResultUtil.createOk();
    }

    /**
     * Search for tasks that specify the given conditions, if they are filled.
     * @param input The input searched for in the title and the description.
     * @param isCancelled Whether the value of Task.isCancelled is true or false.
     * @param isFinished Whether the value of Task.isFinished is true or false.
     * @param volunteerExtId The volunteer it is linked to.
     * @return List&lt;TaskReturn&gt;
     */
    public Result advancedSearch(String input, Boolean isCancelled, Boolean isFinished, String volunteerExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaskAPI.advancedSearch for input " + input + " isCancelled " + isCancelled + " isFinished " + isFinished
                + " volunteerExtId " + volunteerExtId);

        // Retrieve the list from the database.
        TaskMyDao taskMyDao = new TaskMyDao(context);
        List<TaskPojo> taskPojos = taskMyDao.advancedSearch(input, isFinished, volunteerExtId);

        // Convert the list of pojos to returns.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        List<TaskReturn> taskReturns = new ArrayList();
        for (TaskPojo taskPojo: taskPojos) {
            TaskMapper taskMapper = new TaskMapper();
            taskMapper.setTask(taskPojo);
            taskMapper.getTaskReturn().setVolunteerExtId(
                    volunteerMyDao.fetchOneByVolunteerid(taskPojo.getVolunteerid())
                            .getExternalidentifier());

            taskReturns.add(taskMapper.getTaskReturn());
        }

        // Return the list.
        context.close();
        Log.logDebug("End TaskAPI.advancedSearch");
        return ResultUtil.createOk(taskReturns);

    }
}
