package com.apon.taalmaatjes.frontend.tabs.task.detail;

import com.apon.taalmaatjes.backend.api.TaskAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.*;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unused")
public class DetailTask implements Screen {
    private String taskExtId;

    @FXML
    TextField labelTitle, labelVolunteer, labelDateToBeFinished, labelIsFinished;

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
        labelDateToBeFinished.setText(StringUtil.getOutputString(taskReturn.getDateToBeFinished()));
        labelIsFinished.setText(StringUtil.getOutputString(taskReturn.getFinished()));

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

    @FXML
    public void goToScreenEditTask(ActionEvent actionEvent) {
        AddTaskObject addTaskObject = new AddTaskObject();
        addTaskObject.setTaskExtId(taskExtId);
        TransitionHandler.getInstance().goToScreen(ScreenEnum.TASKS_ADD, addTaskObject,
                false, true);
    }

    @FXML
    public void deleteTask(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Weet je zeker dat je deze taak wilt verwijderen?",
                        ButtonType.YES,
                        ButtonType.NO);
        alert.setTitle("Verwijderen taak");
        Optional<ButtonType> alertResult = alert.showAndWait();

        if (alertResult.get() == ButtonType.NO) {
            return;
        }

        // Delete the task.
        Result result = TaskAPI.getInstance().deleteTask(taskExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back where you came from.
        TransitionHandler.getInstance().goBack();
    }

    @FXML
    public void finishTask(ActionEvent actionEvent) {
        Result result = TaskAPI.getInstance().getTask(taskExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        TaskReturn taskReturn = (TaskReturn) result.getResult();

        result = TaskAPI.getInstance().finishTask(taskExtId, !taskReturn.getFinished());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back where you came from.
        TransitionHandler.getInstance().goBack();
    }
}
