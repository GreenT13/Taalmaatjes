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

    Integer volunteerId;

    @FXML
    public void goBack(ActionEvent actionEvent) {
        //Transition.getInstance().transitionVolunteer(VolunteerScreens.OVERVIEW);
        Transition.getInstance().volunteerOverview();
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        VolunteerFacade volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        VolunteerPojo volunteerPojo = convertControlsToPojo();
        if (volunteerId == null) {
            // Use facade to add the user.
            volunteerFacade.addActiveVolunteer(volunteerPojo);
        } else {
            volunteerPojo.setVolunteerid(volunteerId);
            volunteerFacade.updateVolunteer(volunteerPojo);
        }
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
        volunteerPojo.setStreetname(StringUtil.getDatabaseString(inputStreetName.getText()));
        volunteerPojo.setHousenr(StringUtil.getDatabaseString(inputHouseNr.getText()));
        volunteerPojo.setPostalcode(StringUtil.getDatabaseString(inputPostalCode.getText()));
        volunteerPojo.setCity(StringUtil.getDatabaseString(inputCity.getText()));

        return volunteerPojo;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
        VolunteerFacade volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        VolunteerPojo volunteerPojo = volunteerFacade.getVolunteer(volunteerId);
        prefillVolunteer(volunteerPojo);
    }

    private void prefillVolunteer(VolunteerPojo volunteerPojo) {
        inputFirstName.setText(volunteerPojo.getFirstname());
        inputInsertion.setText(volunteerPojo.getInsertion());
        inputLastName.setText(volunteerPojo.getLastname());
        if (volunteerPojo.getDateofbirth() != null) {
            inputDateOfBirth.setValue(volunteerPojo.getDateofbirth().toLocalDate());
        }
        inputPhoneNr.setText(volunteerPojo.getPhonenumber());
        inputMobPhoneNr.setText(volunteerPojo.getMobilephonenumber());
        inputEmail.setText(volunteerPojo.getEmail());
        inputStreetName.setText(volunteerPojo.getStreetname());
        inputHouseNr.setText(volunteerPojo.getHousenr());
        inputPostalCode.setText(volunteerPojo.getPostalcode());
        inputCity.setText(volunteerPojo.getCity());
    }
}
