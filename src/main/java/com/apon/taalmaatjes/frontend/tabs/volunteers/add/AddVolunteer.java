package com.apon.taalmaatjes.frontend.tabs.volunteers.add;

import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AddVolunteer {

    @FXML
    public void goBack(ActionEvent actionEvent) {
        //Transition.getInstance().transitionVolunteer(VolunteerScreens.OVERVIEW);
        Transition.getInstance().volunteerOverview();
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        Transition.getInstance().volunteerDetail(1);
    }
}
