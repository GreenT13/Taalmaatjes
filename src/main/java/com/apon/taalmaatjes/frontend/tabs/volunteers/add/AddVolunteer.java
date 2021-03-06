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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AddVolunteer implements Screen {

    @FXML
    Label labelTitle, labelAge;

    @FXML
    TextField inputFirstName, inputInsertion, inputLastName, inputPhoneNr, inputMobPhoneNr, inputEmail, inputPostalCode;

    @FXML
    TextField inputCity, inputStreetName, inputHouseNr, inputJob;

    @FXML
    HBox hboxActiveFrom1, hboxActiveFrom2;

    @FXML
    DatePicker inputDateOfBirth, inputDateTraining, inputDateActive;

    @FXML
    ComboBox<String> comboBoxSex;

    @FXML
    CheckBox checkboxIsClassAssistant, checkboxIsTaalmaatje;

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

        hboxActiveFrom1.managedProperty().bind(hboxActiveFrom1.visibleProperty());
        hboxActiveFrom2.managedProperty().bind(hboxActiveFrom2.visibleProperty());

        // Prefill date with today.
        inputDateActive.setValue(DateTimeUtil.getCurrentDate().toLocalDate());
    }

    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        VolunteerReturn volunteerReturn = convertControlsToPojo();
        if (volunteerExtId == null) {
            // Add a new volunteer.
            Result result = VolunteerAPI.getInstance().addVolunteer(volunteerReturn,
                    DateTimeUtil.convertLocalDateToSqlDate(inputDateActive.getValue()));

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            volunteerExtId = (String) result.getResult();

            TransitionHandler.getInstance().goBack();
        } else {
            // External identifier is not a field you can fill in, hence we fill it here with the value.
            volunteerReturn.setExternalIdentifier(volunteerExtId);

            // Update the volunteer.
            Result result = VolunteerAPI.getInstance().updateVolunteer(volunteerReturn);

            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }

            TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_DETAIL, volunteerExtId,
                    false, false);
        }

    }

    private VolunteerReturn convertControlsToPojo() {
        VolunteerReturn volunteerReturn = new VolunteerReturn();
        volunteerReturn.setFirstName(StringUtil.getDatabaseString(inputFirstName.getText()));
        volunteerReturn.setInsertion(StringUtil.getDatabaseString(inputInsertion.getText()));
        volunteerReturn.setLastName(StringUtil.getDatabaseString(inputLastName.getText()));
        volunteerReturn.setDateOfBirth(DateTimeUtil.convertLocalDateToSqlDate(inputDateOfBirth.getValue()));
        volunteerReturn.setSex(StringUtil.convertOutputSexToDb(comboBoxSex.getValue()));
        volunteerReturn.setPhoneNumber(StringUtil.getDatabaseString(inputPhoneNr.getText()));
        volunteerReturn.setMobilePhoneNumber(StringUtil.getDatabaseString(inputMobPhoneNr.getText()));
        volunteerReturn.setEmail(StringUtil.getDatabaseString(inputEmail.getText()));
        volunteerReturn.setStreetname(StringUtil.getDatabaseString(inputStreetName.getText()));
        volunteerReturn.setHouseNr(StringUtil.getDatabaseString(inputHouseNr.getText()));
        volunteerReturn.setPostalCode(StringUtil.getDatabaseString(inputPostalCode.getText()));
        volunteerReturn.setCity(StringUtil.getDatabaseString(inputCity.getText()));
        volunteerReturn.setDateTraining(DateTimeUtil.convertLocalDateToSqlDate(inputDateTraining.getValue()));
        volunteerReturn.setJob(StringUtil.getDatabaseString(inputJob.getText()));
        volunteerReturn.setClassAssistant(checkboxIsClassAssistant.isSelected());
        volunteerReturn.setTaalmaatje(checkboxIsTaalmaatje.isSelected());

        return volunteerReturn;
    }

    public void setObject(Object volunteerExtId) {
        this.volunteerExtId = (String) volunteerExtId;

        Result result = VolunteerAPI.getInstance().getVolunteer(this.volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        labelTitle.setText("Bewerken vrijwilliger");

        hboxActiveFrom1.setVisible(false);
        hboxActiveFrom2.setVisible(false);

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
        if (volunteerReturn.getSex().equals("F")) {
            comboBoxSex.setValue("Vrouw");
        }
        inputPhoneNr.setText(volunteerReturn.getPhoneNumber());
        inputMobPhoneNr.setText(volunteerReturn.getMobilePhoneNumber());
        inputEmail.setText(volunteerReturn.getEmail());
        inputStreetName.setText(volunteerReturn.getStreetname());
        inputHouseNr.setText(volunteerReturn.getHouseNr());
        inputPostalCode.setText(volunteerReturn.getPostalCode());
        inputCity.setText(volunteerReturn.getCity());
        if (volunteerReturn.getDateTraining() != null) {
            inputDateTraining.setValue(volunteerReturn.getDateTraining().toLocalDate());
        }
        inputJob.setText(volunteerReturn.getJob());
        checkboxIsClassAssistant.setSelected(volunteerReturn.getClassAssistant());
        checkboxIsTaalmaatje.setSelected(volunteerReturn.getTaalmaatje());
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
