package com.apon.taalmaatjes.frontend.tabs.volunteers.detail;

        import javafx.fxml.FXML;
        import javafx.scene.control.Label;

public class DetailVolunteer {
    @FXML
    Label labelText;

    int volunteerId;

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
        initialize();
    }

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     *
     */
    public void initialize() {
        labelText.setText(String.valueOf(volunteerId));
    }
}
