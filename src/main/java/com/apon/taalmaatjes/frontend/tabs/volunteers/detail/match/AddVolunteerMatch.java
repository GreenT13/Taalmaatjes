package com.apon.taalmaatjes.frontend.tabs.volunteers.detail.match;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.VolunteerMatchAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.NameUtil;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.VolunteerMatchKey;
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
import java.util.Optional;

@SuppressWarnings("unused")
public class AddVolunteerMatch implements Screen {
    private VolunteerMatchKey volunteerMatchKey;

    @FXML
    Label labelTitle;

    @FXML
    DatePicker inputDateStart, inputDateEnd;

    @FXML
    ComboBox<String> comboStudents;

    @FXML
    Button btnDelete;

    @FXML HBox hboxError; @FXML Label labelError;

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();
        btnDelete.managedProperty().bind(btnDelete.visibleProperty());

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
    public void setObject(Object volunteerMatchKey) {
        this.volunteerMatchKey = (VolunteerMatchKey) volunteerMatchKey;

        if (this.volunteerMatchKey.getVolunteerMatchExtId() == null) {
            return;
        }

        // We are in edit mode. So retrieve information and prefill screen.
        Result result = VolunteerMatchAPI.getInstance().getVolunteerMatch(this.volunteerMatchKey.getVolunteerExtId(), this.volunteerMatchKey.getVolunteerMatchExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        labelTitle.setText("Bewerken koppeling");
        btnDelete.setVisible(true);

        prefill((VolunteerMatchReturn) result.getResult());
    }

    @SuppressWarnings("Duplicates")
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
        Result result = StudentAPI.getInstance().advancedSearch(newValue,null,null);

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
    public void handleActionSearchStudents(KeyEvent keyEvent) {
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

        Result result = StudentAPI.getInstance().getStudent(externalIdentifier);
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
    public void handleActionSave(ActionEvent actionEvent) {
        // Save the match.
        Result result;
        if (volunteerMatchKey.getVolunteerMatchExtId() == null) {
            result = VolunteerMatchAPI.getInstance().addVolunteerMatch(getReturn());
        }  else {
            result = VolunteerMatchAPI.getInstance().updateVolunteerMatch(getReturn());
        }

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back to the detail screen (only place we could've come from).
        // Could just insert a transition, but there is not real need to at this point.
        TransitionHandler.getInstance().goBack(volunteerMatchKey.getVolunteerExtId());
    }

    @FXML
    public void handleActionDelete(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Weet je zeker dat je deze regel wilt verwijderen?",
                ButtonType.YES,
                ButtonType.NO);
        alert.setTitle("Verwijderen koppeling");
        Optional<ButtonType> alertResult = alert.showAndWait();

        if (alertResult.get() == ButtonType.NO) {
            return;
        }

        // Delete the instance.
        Result result = VolunteerMatchAPI.getInstance().deleteVolunteerMatch(getReturn());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back to the detail screen (only place we could've come from).
        // Could just insert a transition, but there is not real need to at this point.
        TransitionHandler.getInstance().goBack(volunteerMatchKey.getVolunteerExtId());
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(volunteerMatchKey.getVolunteerExtId());
    }

    private VolunteerMatchReturn getReturn() {
        VolunteerMatchReturn volunteerMatchReturn = new VolunteerMatchReturn();
        volunteerMatchReturn.setVolunteerExtId(volunteerMatchKey.getVolunteerExtId());
        volunteerMatchReturn.setExternalIdentifier(volunteerMatchKey.getVolunteerMatchExtId());
        volunteerMatchReturn.setDateStart(DateTimeUtil.convertLocalDateToSqlDate(inputDateStart.getValue()));
        volunteerMatchReturn.setDateEnd(DateTimeUtil.convertLocalDateToSqlDate(inputDateEnd.getValue()));

        if (comboStudents.getValue() != null) {
            volunteerMatchReturn.setStudentExtId(comboStudents.getValue().substring(0, comboStudents.getValue().indexOf(':')));
        }

        return volunteerMatchReturn;
    }

    private void prefill(VolunteerMatchReturn volunteerMatchReturn) {
        inputDateStart.setValue(volunteerMatchReturn.getDateStart().toLocalDate());
        if (volunteerMatchReturn.getDateEnd() != null){
            inputDateEnd.setValue(volunteerMatchReturn.getDateEnd().toLocalDate());
        }

        Result result = StudentAPI.getInstance().getStudent(volunteerMatchReturn.getStudentExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        StudentReturn studentReturn = (StudentReturn) result.getResult();
        comboStudents.setValue(studentReturn.getExternalIdentifier() + ": " + NameUtil.getStudentName(studentReturn));
    }

}
