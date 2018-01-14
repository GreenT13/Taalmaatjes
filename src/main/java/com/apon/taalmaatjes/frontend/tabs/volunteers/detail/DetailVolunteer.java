package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class DetailVolunteer {
    VolunteerMyDao volunteerMyDao;
    int volunteerId;

    @FXML
    Label labelName, labelDateOfBirth, labelPhoneNumber;

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
        volunteerMyDao = new VolunteerMyDao(FrontendContext.getInstance().getConfiguration());
        initializeValues();
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     *
     */
    public void initializeValues() {
        VolunteerPojo volunteerPojo = volunteerMyDao.fetchOneByVolunteerid(volunteerId);

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
