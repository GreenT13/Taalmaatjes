package com.apon.taalmaatjes.frontend.tabs.volunteers.add;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class AddVolunteer {

    @FXML
    TextField inputFirstName, inputInsertion, inputLastName, inputPhoneNr, inputMobPhoneNr, inputEmail, inputPostalCode;

    @FXML
    TextField inputCity, inputStreetName, inputHouseNr;

    @FXML
    Label labelAge;

    @FXML
    CheckBox checkTraining;

    @FXML
    DatePicker inputDateOfBirth;

    String volunteerExtId;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        if (volunteerExtId != null) {
            // Tried to edit a volunteer, but cancelled. Hence we go back to the detail screen.
            Transition.getInstance().volunteerDetail(volunteerExtId);

        } else {
            // Tried to add a volunteer, but cancelled. Hence we go back to the overview screen.
            Transition.getInstance().volunteerOverview();
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
        VolunteerReturn volunteerReturn = convertControlsToPojo();
        if (volunteerExtId == null) {
            // Add a new volunteer.
            Result result = VolunteerAPI.getInstance().add(volunteerReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            volunteerExtId = (String) result.getResult();
        } else {
            // External identifier is not a field you can fill in, hence we fill it here with the value.
            volunteerReturn.setExternalIdentifier(volunteerExtId);

            // Update the volunteer.
            Result result = VolunteerAPI.getInstance().update(volunteerReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
        }

        Transition.hasAddedVolunteer = true;
        Transition.getInstance().volunteerDetail(volunteerExtId);
    }

    private VolunteerReturn convertControlsToPojo() {
        VolunteerReturn volunteerReturn = new VolunteerReturn();
        volunteerReturn.setFirstName(StringUtil.getDatabaseString(inputFirstName.getText()));
        volunteerReturn.setInsertion(StringUtil.getDatabaseString(inputInsertion.getText()));
        volunteerReturn.setLastName(StringUtil.getDatabaseString(inputLastName.getText()));
        volunteerReturn.setDateOfBirth(DateTimeUtil.convertLocalDateToSqlDate(inputDateOfBirth.getValue()));
        volunteerReturn.setPhoneNumber(StringUtil.getDatabaseString(inputPhoneNr.getText()));
        volunteerReturn.setMobilePhoneNumber(StringUtil.getDatabaseString(inputMobPhoneNr.getText()));
        volunteerReturn.setEmail(StringUtil.getDatabaseString(inputEmail.getText()));
        volunteerReturn.setStreetname(StringUtil.getDatabaseString(inputStreetName.getText()));
        volunteerReturn.setHouseNr(StringUtil.getDatabaseString(inputHouseNr.getText()));
        volunteerReturn.setPostalCode(StringUtil.getDatabaseString(inputPostalCode.getText()));
        volunteerReturn.setCity(StringUtil.getDatabaseString(inputCity.getText()));
        volunteerReturn.setHasTraining(checkTraining.isSelected());

        return volunteerReturn;
    }

    public void setVolunteerExtId(String volunteerExtId) {
        this.volunteerExtId = volunteerExtId;

        Result result = VolunteerAPI.getInstance().get(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        prefillVolunteer((VolunteerReturn) result.getResult());
    }

    private void prefillVolunteer(VolunteerReturn volunteerReturn) {
        inputFirstName.setText(volunteerReturn.getFirstName());
        inputInsertion.setText(volunteerReturn.getInsertion());
        inputLastName.setText(volunteerReturn.getLastName());
        if (volunteerReturn.getDateOfBirth() != null) {
            inputDateOfBirth.setValue(volunteerReturn.getDateOfBirth().toLocalDate());
            labelAge.setText(DateTimeUtil.determineAge(volunteerReturn.getDateOfBirth()).toString());
        }
        inputPhoneNr.setText(volunteerReturn.getPhoneNumber());
        inputMobPhoneNr.setText(volunteerReturn.getMobilePhoneNumber());
        inputEmail.setText(volunteerReturn.getEmail());
        inputStreetName.setText(volunteerReturn.getStreetname());
        inputHouseNr.setText(volunteerReturn.getHouseNr());
        inputPostalCode.setText(volunteerReturn.getPostalCode());
        inputCity.setText(volunteerReturn.getCity());
    }

    @FXML
    public void fillAge(ActionEvent actionEvent) {
        // Fill the age if the value is valid.
        if (inputDateOfBirth.getValue() != null) {
            labelAge.setText(DateTimeUtil.determineAge(inputDateOfBirth.getValue()).toString());
        } else {
            labelAge.setText("");
        }
    }
}
