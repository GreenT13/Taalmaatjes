package com.apon.taalmaatjes.frontend.tabs.students.add;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class AddStudent {

    @FXML
    TextField inputFirstName, inputInsertion, inputLastName;

    @FXML
    CheckBox checkIsGroup, checkIsLookingForVolunteer;

    String studentExtId;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        if (studentExtId != null) {
            // Tried to edit a volunteer, but cancelled. Hence we go back to the detail screen.
            Transition.getInstance().studentDetail(studentExtId);

        } else {
            // Tried to add a volunteer, but cancelled. Hence we go back to the overview screen.
            Transition.getInstance().studentOverview();
        }
    }

    @FXML HBox hboxError; @FXML Label labelError;

    public void showError(Result result) {
        hboxError.setVisible(true);
        labelError.setText(MessageResource.getInstance().getValue(result.getErrorMessage()));
    }

    public void hideError() {
        hboxError.setVisible(false);
    }

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        StudentReturn studentReturn = convertControlsToPojo();
        if (studentExtId == null) {
            // Add a new student.
            Result result = StudentAPI.getInstance().add(studentReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            studentExtId = (String) result.getResult();
        } else {
            // External identifier is not a field you can fill in, hence we fill it here with the value.
            studentReturn.setExternalIdentifier(studentExtId);

            // Update the volunteer.
            Result result = StudentAPI.getInstance().update(studentReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
        }

        Transition.hasAddedStudent = true;
        Transition.getInstance().studentDetail(studentExtId);
    }

    private StudentReturn convertControlsToPojo() {
        StudentReturn studentReturn = new StudentReturn();
        studentReturn.setFirstName(StringUtil.getDatabaseString(inputFirstName.getText()));
        studentReturn.setInsertion(StringUtil.getDatabaseString(inputInsertion.getText()));
        studentReturn.setLastName(StringUtil.getDatabaseString(inputLastName.getText()));
        studentReturn.setLookingForVolunteer(checkIsLookingForVolunteer.isSelected());
        studentReturn.setGroup(checkIsGroup.isSelected());

        return studentReturn;
    }

    public void setStudentExtId(String studentExtId) {
        this.studentExtId = studentExtId;

        Result result = StudentAPI.getInstance().get(studentExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        prefillStudent((StudentReturn) result.getResult());
    }

    private void prefillStudent(StudentReturn studentReturn) {
        inputFirstName.setText(studentReturn.getFirstName());
        inputInsertion.setText(studentReturn.getInsertion());
        inputLastName.setText(studentReturn.getLastName());
        checkIsLookingForVolunteer.setSelected(studentReturn.getLookingForVolunteer());
        checkIsGroup.setSelected(studentReturn.getGroup());
    }
}
