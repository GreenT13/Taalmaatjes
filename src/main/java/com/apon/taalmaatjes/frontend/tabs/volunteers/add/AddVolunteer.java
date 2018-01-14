package com.apon.taalmaatjes.frontend.tabs.volunteers.add;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.facade.VolunteerFacade;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddVolunteer {

    @FXML
    TextField inputFirstName, inputInitials, inputLastName, inputPhoneNr, inputMobPhoneNr, inputEmail;

    @FXML
    DatePicker inputDateOfBirth;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        //Transition.getInstance().transitionVolunteer(VolunteerScreens.OVERVIEW);
        Transition.getInstance().volunteerOverview();
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        // Use facade to add the user.
        VolunteerFacade volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        VolunteerPojo volunteerPojo = convertControlsToPojo();
        volunteerFacade.addVolunteer(volunteerPojo);

        Transition.getInstance().volunteerDetail(volunteerPojo.getVolunteerid());
    }

    private VolunteerPojo convertControlsToPojo() {
        VolunteerPojo volunteerPojo = new VolunteerPojo();
        volunteerPojo.setFirstname(inputFirstName.getText());
        volunteerPojo.setInitials(inputInitials.getText());
        volunteerPojo.setLastname(inputLastName.getText());
        volunteerPojo.setPhonenumber(inputPhoneNr.getText());
        volunteerPojo.setMobilephonenumber(inputMobPhoneNr.getText());
        volunteerPojo.setEmail(inputEmail.getText());

        volunteerPojo.setDateofbirth(DateTimeUtil.convertLocalDateToSqlDate(inputDateOfBirth.getValue()));

        return volunteerPojo;
    }
}
