package com.apon.taalmaatjes.frontend.tabs.task.add;

import com.apon.taalmaatjes.backend.api.TaskAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.AddTaskObject;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.NameUtil;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class AddTask implements Screen {
    // Initialize to prevent nullpointers.
    private AddTaskObject addTaskObject = new AddTaskObject();

    @FXML
    Label labelTitle;

    @FXML
    TextField inputTitle;

    @FXML
    ComboBox<String> comboVolunteer;

    @FXML
    DatePicker datePickerToBeFinished;

    @FXML
    TextArea inputDescription;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        if (addTaskObject.getVolunteerExtId() != null) {
            TransitionHandler.getInstance().goBack(addTaskObject.getVolunteerExtId());
        }  else if (addTaskObject.getTaskExtId() != null) {
            TransitionHandler.getInstance().goBack(addTaskObject.getTaskExtId());
        } else {
            // In the case that both extId are null, just go back.
            TransitionHandler.getInstance().goBack();
        }
    }

    @FXML
    HBox hboxError; @FXML Label labelError;

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

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        comboVolunteer.getEditor().textProperty().addListener((observable, oldValue, newValue) -> changeList(oldValue, newValue));
        datePickerToBeFinished.setValue(DateTimeUtil.getCurrentDate().toLocalDate());
    }

    @SuppressWarnings("Duplicates")
    private void changeList(String oldValue, String newValue) {
        if (newValue.contains(":")) {
            return;
        }

        if (newValue.trim().length() == 0) {
            comboVolunteer.setItems(null);
            comboVolunteer.hide();
            hideError();
            return;
        }
        Result result = VolunteerAPI.getInstance().advancedSearch(newValue,null,null,null, null);

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // So now we create a list of names to fill in the combobox.
        ObservableList<String> comboVolunteerObservableList = FXCollections.observableArrayList();
        List<VolunteerReturn> volunteerReturns = (List<VolunteerReturn>) result.getResult();
        for (VolunteerReturn volunteerReturn : volunteerReturns) {
            comboVolunteerObservableList.add(volunteerReturn.getExternalIdentifier() + ": " + NameUtil.getVolunteerName(volunteerReturn));
        }

        // Fill the combobox.
        comboVolunteer.setItems(comboVolunteerObservableList);
        comboVolunteer.show();
    }


    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        TaskReturn taskReturn = convertControlsToPojo();
        if (taskReturn.getTaskExtId() == null) {
            // Add a new task.
            Result result = TaskAPI.getInstance().addTask(taskReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            addTaskObject.setTaskExtId((String) result.getResult());
        } else {
            // External identifier is not a field you can fill in, hence we fill it here with the value.
            taskReturn.setTaskExtId(addTaskObject.getTaskExtId());

            // Update the task.
            Result result = TaskAPI.getInstance().updateTask(taskReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
        }

        if (addTaskObject.getVolunteerExtId() != null) {
            TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_DETAIL, addTaskObject.getVolunteerExtId(),
                    false, false);
        } else {
            // Go to overview screen.
            TransitionHandler.getInstance().goBack();
        }
    }

    private TaskReturn convertControlsToPojo() {
        TaskReturn taskReturn = new TaskReturn();
        taskReturn.setTaskExtId(addTaskObject.getTaskExtId());
        taskReturn.setTitle(StringUtil.getDatabaseString(inputTitle.getText()));
        if (datePickerToBeFinished != null) {
            taskReturn.setDateToBeFinished(DateTimeUtil.convertLocalDateToSqlDate(datePickerToBeFinished.getValue()));
        }
        if (comboVolunteer.getValue() != null) {
            taskReturn.setVolunteerExtId(comboVolunteer.getValue().substring(0, comboVolunteer.getValue().indexOf(':')));
        }
        taskReturn.setDescription(StringUtil.getDatabaseString(inputDescription.getText()));

        return taskReturn;
    }

    public void setObject(Object addTaskObject) {
        this.addTaskObject = (AddTaskObject) addTaskObject;

        if (this.addTaskObject.getVolunteerExtId() != null) {
            Result result = VolunteerAPI.getInstance().getVolunteer(this.addTaskObject.getVolunteerExtId());
            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }

            VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
            comboVolunteer.setValue(volunteerReturn.getExternalIdentifier() + ": " + NameUtil.getVolunteerName(volunteerReturn));

            // Disable choosing a volunteer
            comboVolunteer.setDisable(true);
        }

        if (this.addTaskObject.getTaskExtId() != null) {
            Result result = TaskAPI.getInstance().getTask(this.addTaskObject.getTaskExtId());
            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }

            labelTitle.setText("Bewerken taak");

            prefill((TaskReturn) result.getResult());
        }
    }

    private void prefill(TaskReturn taskReturn) {
        inputTitle.setText(taskReturn.getTitle());
        inputDescription.setText(taskReturn.getDescription());

        Result result = VolunteerAPI.getInstance().getVolunteer(taskReturn.getVolunteerExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
        comboVolunteer.setValue(volunteerReturn.getExternalIdentifier() + ": " + NameUtil.getVolunteerName(volunteerReturn));
    }

    @FXML
    public void handleActionSearchVolunteers(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.ENTER) {
            return;
        }

        // Check if the current input is empty, because then we just clear the list.
        if (comboVolunteer.getValue() == null || comboVolunteer.getValue().trim().length() == 0) {
            comboVolunteer.setItems(null);
            comboVolunteer.setValue(null);
            return;
        }

        // If input is chosen, check that it is valid.
        String volunteerExtId;
        if (!comboVolunteer.getValue().contains(":")) {
            volunteerExtId = comboVolunteer.getValue();
        } else {
            volunteerExtId = comboVolunteer.getValue().substring(0, comboVolunteer.getValue().indexOf(":"));
        }

        if (volunteerExtId == null || volunteerExtId.trim().length() == 0) {
            // Something is wrong.
            showError(ResultUtil.createError("AddTask.wrongVolunteer"));
        }

        Result result = VolunteerAPI.getInstance().getVolunteer(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
        if (volunteerReturn == null) {
            showError(ResultUtil.createError("AddTask.noVolunteerFound"));
            return;
        }

        hideError();
    }


}
