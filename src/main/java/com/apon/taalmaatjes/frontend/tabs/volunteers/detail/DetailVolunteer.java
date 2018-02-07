package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.VolunteerMatchAPI;
import com.apon.taalmaatjes.backend.api.returns.*;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.*;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class DetailVolunteer implements Screen {
    private String volunteerExtId;

    @FXML
    TextField labelName, labelDateOfBirth, labelPhoneNr, labelMobPhoneNr, labelEmail, labelStreetNameAndHouseNr;
    @FXML
    TextField labelPostalCode, labelCity, labelHasTraining, labelAge;

    @FXML
    VBox vboxActive, vboxMatch;

    @FXML
    ComboBox<String> comboStudents;

    boolean isActive;

    public void setObject(Object volunteerExtId) {
        this.volunteerExtId = (String) volunteerExtId;
        initializeValues();
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

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     */
    private void initializeValues() {
        Result result = VolunteerAPI.getInstance().getVolunteer(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();

        // Set the name.
        labelName.setText(String.valueOf(volunteerReturn.getExternalIdentifier()) + ": " + NameUtil.getVolunteerName(volunteerReturn));

        // Set the date of birth and age.
        labelDateOfBirth.setText(StringUtil.getOutputString(volunteerReturn.getDateOfBirth()));
        labelAge.setText(DateTimeUtil.determineAge(volunteerReturn.getDateOfBirth()).toString());

        // Set phonenumber,
        labelPhoneNr.setText(StringUtil.getOutputString(volunteerReturn.getPhoneNumber()));

        // Set mobile phone nr.
        labelMobPhoneNr.setText(StringUtil.getOutputString(volunteerReturn.getMobilePhoneNumber()));

        // Set email.
        labelEmail.setText(StringUtil.getOutputString(volunteerReturn.getEmail()));

        // Set street and house number
        if (volunteerReturn.getStreetname() != null && volunteerReturn.getHouseNr() != null) {
            labelStreetNameAndHouseNr.setText(StringUtil.getOutputString(volunteerReturn.getStreetname() + " " + volunteerReturn.getHouseNr()));
        } else {
            labelStreetNameAndHouseNr.setText(StringUtil.getOutputString((String) null));
        }

        // Set postal code
        labelPostalCode.setText(StringUtil.getOutputString(volunteerReturn.getPostalCode()));

        // Set city
        labelCity.setText(StringUtil.getOutputString(volunteerReturn.getCity()));

        // Set training
        labelHasTraining.setText(StringUtil.getOutputString(volunteerReturn.getHasTraining()));

        // Add all volunteerInstance lines (in order!).
        for (VolunteerInstanceReturn volunteerInstanceReturn : volunteerReturn.getListVolunteerInstance()) {
            addActiveLine(volunteerInstanceReturn);
        }

        for (VolunteerMatchReturn volunteerMatchReturn : volunteerReturn.getListVolunteerMatch()) {
            addMatchLine(volunteerMatchReturn);
        }
    }

    @FXML
    public void goBack() {
        TransitionHandler.getInstance().goBack();
    }

    private void addActiveLine(VolunteerInstanceReturn volunteerInstanceReturn) {
        // Initialize the frontend variables.
        HBox hbox = new HBox();
        Hyperlink hyperlink = new Hyperlink();
        Label label = new Label();
        hbox.getChildren().addAll(hyperlink, label);
        vboxActive.getChildren().add(hbox);

        VolunteerInstanceKey volunteerInstanceKey = new VolunteerInstanceKey();
        volunteerInstanceKey.setVolunteerExtId(volunteerExtId);
        volunteerInstanceKey.setVolunteerInstanceExtId(volunteerInstanceReturn.getExternalIdentifier());

        // Fix hyperlink
        hyperlink.setText("(Wijzigen)");
        hyperlink.setUserData(volunteerInstanceKey);
        hyperlink.setOnAction(event -> goToScreenEditInstance(volunteerInstanceKey));

        // Fix the label
        label.getStyleClass().add("labelActive");
        String text = " Actief van " + volunteerInstanceReturn.getDateStart() + " tot ";
        if (volunteerInstanceReturn.getDateEnd() == null) {
            text += "nu.";
        } else {
            text += volunteerInstanceReturn.getDateEnd() + ".";
        }

        label.setText(text);
    }

    private void goToScreenEditInstance(VolunteerInstanceKey volunteerInstanceKey) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD_INSTANCE, volunteerInstanceKey,
                false, true);
    }

    /**
     * Adds an hbox to vboxMatch, containing hyperlinks to stop/delete and showing the information from the match in a label.
     * @param volunteerMatchReturn Object to retrieve all the data from.
     */
    private void addMatchLine(VolunteerMatchReturn volunteerMatchReturn) {
        // Initialize the frontend variables.
        HBox hbox = new HBox();
        Hyperlink hyperlink = new Hyperlink();
        Label label = new Label();
        hbox.getChildren().addAll(hyperlink, label);
        vboxMatch.getChildren().add(hbox);

        VolunteerMatchKey volunteerMatchKey = new VolunteerMatchKey();
        volunteerMatchKey.setVolunteerExtId(volunteerExtId);
        volunteerMatchKey.setVolunteerMatchExtId(volunteerMatchReturn.getExternalIdentifier());

        // Fix hyperlink
        hyperlink.setText("(Wijzigen)");
        hyperlink.setOnAction(event -> goToScreenEditMatch(volunteerMatchKey));

        // Create the label with the correct text.
        Result result = StudentAPI.getInstance().get(volunteerMatchReturn.getStudentExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        String studentName = NameUtil.getStudentName((StudentReturn) result.getResult());
        label.getStyleClass().add("labelMatch");
        String text = " Heeft van " + volunteerMatchReturn.getDateStart() + " tot ";
        if (volunteerMatchReturn.getDateEnd() == null) {
            text += "nu";
        } else {
            text += volunteerMatchReturn.getDateEnd();
        }
        text+= " " + studentName + " begeleid.";

        label.setText(text);
    }

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        TextUtils.setWidthToContent(labelName);
        TextUtils.setWidthToContent(labelDateOfBirth);
        TextUtils.setWidthToContent(labelPhoneNr);
        TextUtils.setWidthToContent(labelMobPhoneNr);
        TextUtils.setWidthToContent(labelEmail);
        TextUtils.setWidthToContent(labelStreetNameAndHouseNr);
        TextUtils.setWidthToContent(labelPostalCode);
        TextUtils.setWidthToContent(labelCity);
        TextUtils.setWidthToContent(labelHasTraining);
    }

    @FXML
    public void goToScreenEditVolunteer(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD, volunteerExtId,
                false, true);
    }

    @FXML
    public void goToScreenAddInstance(ActionEvent actionEvent) {
        VolunteerInstanceKey volunteerInstanceKey = new VolunteerInstanceKey();
        volunteerInstanceKey.setVolunteerExtId(volunteerExtId);
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD_INSTANCE, volunteerInstanceKey,
                false, true);
    }

    @FXML
    public void goToScreenAddMatch(ActionEvent actionEvent) {
        VolunteerMatchKey volunteerMatchKey = new VolunteerMatchKey();
        volunteerMatchKey.setVolunteerExtId(volunteerExtId);
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD_MATCH, volunteerMatchKey,
                false, true);
    }

    private void goToScreenEditMatch(VolunteerMatchKey volunteerMatchKey) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD_MATCH, volunteerMatchKey,
                false, true);
    }
}
