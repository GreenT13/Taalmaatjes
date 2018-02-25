package com.apon.taalmaatjes.frontend.tabs.students.add;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AddStudent implements Screen {

    @FXML
    Label labelTitle;

    @FXML
    TextField inputFirstName, inputInsertion, inputLastName, inputGroupIdentification;

    @FXML
    CheckBox checkHasQuit;

    private String studentExtId;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(studentExtId);
    }

    @FXML HBox hboxError; @FXML Label labelError;

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
    }

    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        StudentReturn studentReturn = convertControlsToPojo();
        if (studentExtId == null) {
            // Add a new student.
            Result result = StudentAPI.getInstance().addStudent(studentReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            studentExtId = (String) result.getResult();
            TransitionHandler.getInstance().goBack();

        } else {
            // External identifier is not a field you can fill in, hence we fill it here with the value.
            studentReturn.setExternalIdentifier(studentExtId);

            // Update the volunteer.
            Result result = StudentAPI.getInstance().updateStudent(studentReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            TransitionHandler.getInstance().goToScreen(ScreenEnum.STUDENTS_DETAIL, studentExtId,
                    false, false);

        }

    }

    private StudentReturn convertControlsToPojo() {
        StudentReturn studentReturn = new StudentReturn();
        studentReturn.setFirstName(StringUtil.getDatabaseString(inputFirstName.getText()));
        studentReturn.setInsertion(StringUtil.getDatabaseString(inputInsertion.getText()));
        studentReturn.setLastName(StringUtil.getDatabaseString(inputLastName.getText()));
        studentReturn.setGroupIdentification(StringUtil.getDatabaseString(inputGroupIdentification.getText()));
        studentReturn.setHasQuit(checkHasQuit.isSelected());

        return studentReturn;
    }

    public void setObject(Object studentExtId) {
        this.studentExtId = (String) studentExtId;

        Result result = StudentAPI.getInstance().getStudent(this.studentExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        labelTitle.setText("Bewerken student");

        prefillStudent((StudentReturn) result.getResult());
    }

    private void prefillStudent(StudentReturn studentReturn) {
        inputFirstName.setText(studentReturn.getFirstName());
        inputInsertion.setText(studentReturn.getInsertion());
        inputLastName.setText(studentReturn.getLastName());
        inputGroupIdentification.setText(studentReturn.getGroupIdentification());
        checkHasQuit.setSelected(studentReturn.getHasQuit());
    }
}
