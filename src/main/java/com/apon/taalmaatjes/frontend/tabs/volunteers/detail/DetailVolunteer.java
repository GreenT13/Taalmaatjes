package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.*;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.*;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class DetailVolunteer {
    String volunteerExtId;

    @FXML
    TextField labelName, labelDateOfBirth, labelPhoneNr, labelMobPhoneNr, labelEmail, labelStreetNameAndHouseNr;
    @FXML
    TextField labelPostalCode, labelCity, labelHasTraining, labelAge;

    @FXML
    VBox vboxActive, vboxMatch;

    @FXML
    ComboBox<String> comboStudents;

    @FXML
    Hyperlink hyperlinkChangeActive;
    boolean isActive;

    public void setVolunteerExtId(String volunteerExtId) {
        this.volunteerExtId = volunteerExtId;
        initializeValues();
    }

    @FXML HBox hboxError; @FXML Label labelError;

    public void showError(Result result) {
        hboxError.setVisible(true);
        labelError.setText(MessageResource.getInstance().getValue(result.getErrorMessage()));
    }

    public void hideError() {
        hboxError.setVisible(false);
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     */
    public void initializeValues() {
        Result result = VolunteerAPI.getInstance().get(volunteerExtId);
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

        // Change hyperlink text according to active state of volunteer.
        setTextHyperlink(volunteerReturn);

        // comboStudents stuff.
        comboStudents.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                changeList(oldValue, newValue);
            }
        });
    }

    @FXML
    public void back() {
        // In case we added a volunteer, we refresh.
        Transition.getInstance().volunteerOverview();
    }

    private void addActiveLine(VolunteerInstanceReturn volunteerInstanceReturn) {
        Label label = new Label();
        label.getStyleClass().add("labelActive");
        String text = "Actief vanaf " + volunteerInstanceReturn.getDateStart() + " tot ";
        if (volunteerInstanceReturn.getDateEnd() == null) {
            text += "nu.";
        } else {
            text += volunteerInstanceReturn.getDateEnd() + ".";
        }

        label.setText(text);
        vboxActive.getChildren().add(label);
    }

    /**
     * Adds an hbox to vboxMatch, containing hyperlinks to stop/delete and showing the information from the match in a label.
     * @param volunteerMatchReturn
     */
    private void addMatchLine(VolunteerMatchReturn volunteerMatchReturn) {
        // Initialize the frontend variables.
        HBox hbox = new HBox();
        Hyperlink hyperlink = new Hyperlink();
        Label label = new Label();
        hbox.getChildren().addAll(hyperlink, label);
        vboxMatch.getChildren().add(hbox);

        // Determine whether the hyperlink should say "start" or "stop".
        if (DateTimeUtil.isActiveTodayMinusOne(volunteerMatchReturn.getDateStart(), volunteerMatchReturn.getDateEnd())){
            hyperlink.setText("(stop)");
        } else {
            hyperlink.setText("(start)");
        }
        // Request focus because whenever startStopMatch is called, it focuses labelName.
        hyperlink.requestFocus();

        // Add the action to the hyperlink.
        hyperlink.setUserData(volunteerMatchReturn.getExternalIdentifier());
        hyperlink.setOnAction(event -> startStopMatch(volunteerMatchReturn.getExternalIdentifier()));

        // Create the label with the correct text.
        String studentName = NameUtil.getStudentName(volunteerMatchReturn.getStudent());
        label.getStyleClass().add("labelMatch");
        String text = "Heeft van " + volunteerMatchReturn.getDateStart() + " tot ";
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
    public void edit(ActionEvent actionEvent) {
        Transition.getInstance().volunteerAdd(volunteerExtId);
    }

    @FXML
    public void changeActive(ActionEvent actionEvent) {
        //volunteerFacade.changeActive(volunteerId);
        Result result = VolunteerAPI.getInstance().toggleActive(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Refresh output, but also needs to retrieve the volunteer again.
        result = VolunteerAPI.getInstance().get(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        vboxActive.getChildren().clear();
        for (VolunteerInstanceReturn volunteerInstanceReturn : ((VolunteerReturn) result.getResult()).getListVolunteerInstance()) {
            addActiveLine(volunteerInstanceReturn);
        }

        setTextHyperlink((VolunteerReturn) result.getResult());
    }

    private void setTextHyperlink(VolunteerReturn volunteerReturn) {
        // Change hyperlink text according to active state of volunteer.
        if (volunteerReturn.isActiveToday() &&
                (volunteerReturn.getActiveUntil() == null ||
                    volunteerReturn.getActiveUntil().compareTo(DateTimeUtil.getCurrentDate()) < 0)) {
            hyperlinkChangeActive.setText("(stop)");
        } else {
            hyperlinkChangeActive.setText("(start)");
        }
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
        if (comboStudents.getValue().indexOf(":") < 0) {
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
        }

        // Add the new match
        result = VolunteerAPI.getInstance().addMatch(volunteerExtId, studentReturn);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Correctly handled the adding. Now refresh the output.
        vboxMatch.getChildren().clear();
        result = VolunteerAPI.getInstance().get(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
        for (VolunteerMatchReturn volunteerMatchReturn : volunteerReturn.getListVolunteerMatch()) {
            addMatchLine(volunteerMatchReturn);
        }

        hideError();
    }

    @FXML
    public void changeList(String oldValue, String newValue) {
        if (newValue.contains(":")) {
            return;
        }

        if (newValue == null || newValue.trim().length() == 0) {
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

    public void startStopMatch(String volunteerMatchExtId) {
        Result result = VolunteerAPI.getInstance().toggleMatch(volunteerExtId, volunteerMatchExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Correctly handled the adding. Now refresh the output.
        vboxMatch.getChildren().clear();
        result = VolunteerAPI.getInstance().get(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();
        for (VolunteerMatchReturn volunteerMatchReturn : volunteerReturn.getListVolunteerMatch()) {
            addMatchLine(volunteerMatchReturn);
        }

        hideError();
    }
}
