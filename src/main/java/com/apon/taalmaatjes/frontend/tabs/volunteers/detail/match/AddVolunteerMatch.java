package com.apon.taalmaatjes.frontend.tabs.volunteers.detail.match;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.NameUtil;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class AddVolunteerMatch implements Screen {
    private String volunteerExtId;

    @FXML
    DatePicker inputDateStart, inputDateEnd;

    @FXML
    ComboBox<String> comboStudents;

    @FXML HBox hboxError; @FXML Label labelError;

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        comboStudents.getEditor().textProperty().addListener((observable, oldValue, newValue) -> changeList(oldValue, newValue));
    }

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


    @Override
    public void setObject(Object volunteerExtId) {
        this.volunteerExtId = (String) volunteerExtId;
    }

    private void changeList(String oldValue, String newValue) {
        if (newValue.contains(":")) {
            return;
        }

        if (newValue.trim().length() == 0) {
            comboStudents.setItems(null);
            comboStudents.hide();
            hideError();
            return;
        }
        Result result = StudentAPI.getInstance().advancedSearch(newValue,null,null,null);

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // So now we create a list of names to fill in the combobox.
        ObservableList<String> comboStudentObservableList = FXCollections.observableArrayList();
        List<StudentReturn> studentReturns = (List<StudentReturn>) result.getResult();
        for (StudentReturn studentReturn : studentReturns) {
            comboStudentObservableList.add(studentReturn.getExternalIdentifier() + ": " + NameUtil.getStudentName(studentReturn));
        }

        // Fill the combobox.
        comboStudents.setItems(comboStudentObservableList);
        comboStudents.show();
    }

    @FXML
    public void addMatch(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.ENTER) {
            return;
        }

        // Check if the current input is empty, because then we just clear the list.
        if (comboStudents.getValue() == null || comboStudents.getValue().trim().length() == 0) {
            comboStudents.setItems(null);
            comboStudents.setValue(null);
            return;
        }

        // If input is chosen, check that it is valid.
        String externalIdentifier;
        if (!comboStudents.getValue().contains(":")) {
            externalIdentifier = comboStudents.getValue();
        } else {
            externalIdentifier = comboStudents.getValue().substring(0, comboStudents.getValue().indexOf(":"));
        }

        if (externalIdentifier == null || externalIdentifier.trim().length() == 0) {
            // Something is wrong.
            showError(ResultUtil.createError("DetailVolunteer.addMatch.wrongStudent"));
        }

        Result result = StudentAPI.getInstance().get(externalIdentifier);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        StudentReturn studentReturn = (StudentReturn) result.getResult();
        if (studentReturn == null) {
            showError(ResultUtil.createError("DetailVolunteer.addMatch.noStudentFound"));
            return;
        }

        hideError();
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        // Save the match.
        Result result = VolunteerAPI.getInstance().addMatch(volunteerExtId, getReturn());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back to the detail screen (only place we could've come from).
        // Could just insert a transition, but there is not real need to at this point.
        TransitionHandler.getInstance().goBack(volunteerExtId);
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(volunteerExtId);
    }

    private VolunteerMatchReturn getReturn() {
        VolunteerMatchReturn volunteerMatchReturn = new VolunteerMatchReturn();
        volunteerMatchReturn.setDateStart(DateTimeUtil.convertLocalDateToSqlDate(inputDateStart.getValue()));
        volunteerMatchReturn.setDateEnd(DateTimeUtil.convertLocalDateToSqlDate(inputDateEnd.getValue()));

        StudentReturn studentReturn = new StudentReturn();
        if (comboStudents.getValue() != null) {
            studentReturn.setExternalIdentifier(comboStudents.getValue().substring(0, comboStudents.getValue().indexOf(':')));
        }
        volunteerMatchReturn.setStudent(studentReturn);

        return volunteerMatchReturn;
    }
}
