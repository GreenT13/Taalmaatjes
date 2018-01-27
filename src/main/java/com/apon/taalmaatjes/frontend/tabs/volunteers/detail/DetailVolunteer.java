package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.NameUtil;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DetailVolunteer {
    String volunteerExtId;

    @FXML
    TextField labelName, labelDateOfBirth, labelPhoneNr, labelMobPhoneNr, labelEmail, labelStreetNameAndHouseNr, labelPostalCode, labelCity;

    @FXML
    VBox vboxActive, vboxMatch;

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
        String name = String.valueOf(volunteerReturn.getExternalIdentifier()) + ": ";
        if (volunteerReturn.getFirstName() != null) {
            name += volunteerReturn.getFirstName() + " ";
        }
        if (volunteerReturn.getInsertion() != null) {
            name += volunteerReturn.getInsertion()  + " ";
        }

        name += volunteerReturn.getLastName();
        labelName.setText(name);

        // Set the date of birth.
        labelDateOfBirth.setText(StringUtil.getOutputString(volunteerReturn.getDateOfBirth()));

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

        // Add all volunteerInstance lines (in order!).
        for (VolunteerInstanceReturn volunteerInstanceReturn : volunteerReturn.getListVolunteerInstance()) {
            addActiveLine(volunteerInstanceReturn);
        }

        for (VolunteerMatchReturn volunteerMatchReturn : volunteerReturn.getListVolunteerMatch()) {
            addMatchLine(volunteerMatchReturn);
        }

        // Change hyperlink text according to active state of volunteer.
        setTextHyperlink(volunteerReturn);
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

    private void addMatchLine(VolunteerMatchReturn volunteerMatchReturn) {
        String studentName = NameUtil.getStudentName(volunteerMatchReturn.getStudent());

        Label label = new Label();
        label.getStyleClass().add("labelMatch");
        String text = "Heeft " + studentName + " begeleid van " + volunteerMatchReturn.getDateStart() + " tot ";
        if (volunteerMatchReturn.getDateEnd() == null) {
            text += "nu.";
        } else {
            text += volunteerMatchReturn.getDateEnd() + ".";
        }

        label.setText(text);
        vboxMatch.getChildren().add(label);
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
}
