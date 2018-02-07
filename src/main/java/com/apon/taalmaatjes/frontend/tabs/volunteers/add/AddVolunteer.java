package com.apon.taalmaatjes.frontend.tabs.volunteers.add;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AddVolunteer implements Screen {

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

    private String volunteerExtId;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(volunteerExtId);
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

        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_DETAIL, volunteerExtId,
                false, false);
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

    public void setObject(Object volunteerExtId) {
        this.volunteerExtId = (String) volunteerExtId;

        Result result = VolunteerAPI.getInstance().getVolunteer(this.volunteerExtId);
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
        checkTraining.setSelected(volunteerReturn.getHasTraining());
    }

    @FXML
    public void handleActionFillAgeOnEditDate(ActionEvent actionEvent) {
        // Fill the age if the value is valid.
        if (inputDateOfBirth.getValue() != null) {
            labelAge.setText(DateTimeUtil.determineAge(inputDateOfBirth.getValue()).toString());
        } else {
            labelAge.setText("");
        }
    }
}
