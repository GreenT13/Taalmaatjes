package com.apon.taalmaatjes.frontend.tabs.task.detail;

import com.apon.taalmaatjes.backend.api.TaskAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.frontend.presentation.*;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

public class DetailTask implements Screen {
    private String taskExtId;

    @FXML
    TextField labelTitle, labelVolunteer;

    @FXML
    TextArea textAreaDescription;

    @Override
    public void setObject(Object taskExtId) {
        this.taskExtId = (String) taskExtId;
        initializeValues();
    }

    @FXML
    HBox hboxError;
    @FXML
    Label labelError;

    private void showError(@Nullable Result result) {
        hboxError.setVisible(true);
        if (result != null) {
            labelError.setText(MessageResource.getInstance().getValue(result.getErrorMessage()));
        } else {
            labelError.setText("Something went horribly wrong.");
        }
    }

    private void hideError() {
        hboxError.setVisible(false);
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     */
    private void initializeValues() {
        Result result = TaskAPI.getInstance().getTask(taskExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        TaskReturn taskReturn = (TaskReturn) result.getResult();
        labelTitle.setText(taskReturn.getTaskExtId() + ": " + taskReturn.getTitle());

        result = VolunteerAPI.getInstance().getVolunteer(taskReturn.getVolunteerExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
        labelVolunteer.setText(NameUtil.getVolunteerName(volunteerReturn));

        textAreaDescription.setText(taskReturn.getDescription());
    }

    @FXML
    public void goBack() {
        TransitionHandler.getInstance().goBack();
    }

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        TextUtils.setWidthToContent(labelTitle);
        TextUtils.setWidthToContent(labelVolunteer);
    }

    @SuppressWarnings("unused")
    @FXML
    public void goToScreenEditTask(ActionEvent actionEvent) {
        AddTaskObject addTaskObject = new AddTaskObject();
        addTaskObject.setTaskExtId(taskExtId);
        TransitionHandler.getInstance().goToScreen(ScreenEnum.TASKS_ADD, addTaskObject,
                false, true);
    }
}
