package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.facade.VolunteerFacade;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DetailVolunteer {
    VolunteerFacade volunteerFacade;
    int volunteerId;

    @FXML
    Label labelName, labelDateOfBirth, labelPhoneNumber;

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
        volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        initializeValues();
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     *
     */
    public void initializeValues() {
        VolunteerPojo volunteerPojo = volunteerFacade.getVolunteer(volunteerId);

        // Set the name.
        String name = "";
        if (volunteerPojo.getFirstname() != null) {
            name += volunteerPojo.getFirstname() + " ";
        }
        if (volunteerPojo.getInitials() != null) {
            name += volunteerPojo.getInitials()  + " ";
        }
        if (volunteerPojo.getLastname() != null) {
            name += volunteerPojo.getLastname() + " ";
        }
        name += "(" + String.valueOf(volunteerPojo.getVolunteerid()) + ")";
        labelName.setText(name);

        // Set the date of birth.
        labelDateOfBirth.setText((volunteerPojo.getDateofbirth() != null) ?
                volunteerPojo.getDateofbirth().toString() :
                "Niet ingevuld");

        // Set phonenumber
        labelPhoneNumber.setText((volunteerPojo.getPhonenumber() != null) ?
                volunteerPojo.getPhonenumber().toString() :
                "Niet ingevuld");
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void back() {
        Transition.getInstance().volunteerOverview();
    }
}
