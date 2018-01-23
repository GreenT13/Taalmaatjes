package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.StudentPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.facade.StudentFacade;
import com.apon.taalmaatjes.backend.facade.VolunteerFacade;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DetailVolunteer {
    VolunteerFacade volunteerFacade;
    StudentFacade studentFacade;

    int volunteerId;

    @FXML
    TextField labelName, labelDateOfBirth, labelPhoneNr, labelMobPhoneNr, labelEmail, labelStreetNameAndHouseNr, labelPostalCode, labelCity;

    @FXML
    VBox vboxActive, vboxMatch;

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
        volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        studentFacade = new StudentFacade(FrontendContext.getInstance().getContext());
        initializeValues();
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     */
    public void initializeValues() {
        VolunteerPojo volunteerPojo = volunteerFacade.getVolunteer(volunteerId);

        // Set the name.
        String name = String.valueOf(volunteerPojo.getVolunteerid()) + ": ";
        if (volunteerPojo.getFirstname() != null) {
            name += volunteerPojo.getFirstname() + " ";
        }
        if (volunteerPojo.getInsertion() != null) {
            name += volunteerPojo.getInsertion()  + " ";
        }

        name += volunteerPojo.getLastname();
        labelName.setText(name);

        // Set the date of birth.
        labelDateOfBirth.setText(StringUtil.getOutputString(volunteerPojo.getDateofbirth()));

        // Set phonenumber,
        labelPhoneNr.setText(StringUtil.getOutputString(volunteerPojo.getPhonenumber()));

        // Set mobile phone nr.
        labelMobPhoneNr.setText(StringUtil.getOutputString(volunteerPojo.getMobilephonenumber()));

        // Set email.
        labelEmail.setText(StringUtil.getOutputString(volunteerPojo.getEmail()));

        // Set street and house number
        if (volunteerPojo.getStreetname() != null && volunteerPojo.getHousenr() != null) {
            labelStreetNameAndHouseNr.setText(StringUtil.getOutputString(volunteerPojo.getStreetname() + " " + volunteerPojo.getHousenr()));
        } else {
            labelStreetNameAndHouseNr.setText(StringUtil.getOutputString((String) null));
        }

        // Set postal code
        labelPostalCode.setText(StringUtil.getOutputString(volunteerPojo.getPostalcode()));

        // Set city
        labelCity.setText(StringUtil.getOutputString(volunteerPojo.getCity()));

        // Add all volunteerInstance lines (in order!).
        for (VolunteerinstancePojo volunteerinstancePojo : volunteerFacade.getVolunteerInstanceInOrder(volunteerId)) {
            addActiveLine(volunteerinstancePojo);
        }

        for (VolunteermatchPojo volunteermatchPojo : volunteerFacade.getVolunteerMatchInOrder(volunteerId)) {
            addMatchLine(volunteermatchPojo);
        }
    }

    @FXML
    public void back() {
        Transition.getInstance().volunteerOverview();
    }

    private void addActiveLine(VolunteerinstancePojo volunteerinstancePojo) {
        Label label = new Label();
        label.getStyleClass().add("labelActive");
        String text = "Actief vanaf " + volunteerinstancePojo.getDatestart() + " tot ";
        if (volunteerinstancePojo.getDateend() == null) {
            text += "nu.";
        } else {
            text += volunteerinstancePojo.getDateend() + ".";
        }

        label.setText(text);
        vboxActive.getChildren().add(label);
    }

    private void addMatchLine(VolunteermatchPojo volunteermatchPojo) {
        String studentName = studentFacade.getStudentName(volunteermatchPojo.getStudentid());

        Label label = new Label();
        label.getStyleClass().add("labelMatch");
        String text = "Heeft " + studentName + " begeleid van " + volunteermatchPojo.getDatestart() + " tot ";
        if (volunteermatchPojo.getDateend() == null) {
            text += "nu.";
        } else {
            text += volunteermatchPojo.getDateend() + ".";
        }

        label.setText(text);
        vboxMatch.getChildren().add(label);
    }

    @FXML
    public void initialize() {
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
        Transition.getInstance().volunteerAdd(volunteerId);
    }
}
