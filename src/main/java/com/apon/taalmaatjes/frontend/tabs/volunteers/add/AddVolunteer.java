package com.apon.taalmaatjes.frontend.tabs.volunteers.add;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.facade.VolunteerFacade;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddVolunteer {

    @FXML
    TextField inputFirstName, inputInsertion, inputLastName, inputPhoneNr, inputMobPhoneNr, inputEmail, inputPostalCode;
    @FXML
    TextField inputCity, inputStreetName, inputHouseNr;

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
        volunteerPojo.setFirstname(StringUtil.getDatabaseString(inputFirstName.getText()));
        volunteerPojo.setInsertion(StringUtil.getDatabaseString(inputInsertion.getText()));
        volunteerPojo.setLastname(StringUtil.getDatabaseString(inputLastName.getText()));
        volunteerPojo.setDateofbirth(DateTimeUtil.convertLocalDateToSqlDate(inputDateOfBirth.getValue()));
        volunteerPojo.setPhonenumber(StringUtil.getDatabaseString(inputPhoneNr.getText()));
        volunteerPojo.setMobilephonenumber(StringUtil.getDatabaseString(inputMobPhoneNr.getText()));
        volunteerPojo.setEmail(StringUtil.getDatabaseString(inputEmail.getText()));
        volunteerPojo.setPostalcode(StringUtil.getDatabaseString(inputPostalCode.getText()));
        volunteerPojo.setCity(StringUtil.getDatabaseString(inputCity.getText()));
        volunteerPojo.setStreetname(StringUtil.getDatabaseString(inputStreetName.getText()));
        volunteerPojo.setHousenr(StringUtil.getDatabaseString(inputHouseNr.getText()));

        return volunteerPojo;
    }
}
