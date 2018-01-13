package com.apon.taalmaatjes.frontend.transition;

import com.apon.taalmaatjes.frontend.tabs.volunteers.detail.DetailVolunteer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

public class Transition {
    private static Transition ourInstance = new Transition();

    public static Transition getInstance() {
        return ourInstance;
    }

    private Transition() {
    }

    Tab tabHome, tabVolunteer, tabStudent, tabReport;
    Node previousVolunteer;

    public void volunteerDetail(int volunteerId) {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FxmlLocation.DETAIL_VOLUNTEERS + ".fxml"));
        // Load so we have the controller instantiated.
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        DetailVolunteer detailVolunteer = loader.getController();
        detailVolunteer.setVolunteerId(volunteerId);

        // Set content last, so we make sure that what is shown on the screen is initialized.
        tabVolunteer.setContent(root);
    }

    public void volunteerAdd() {
        try {
            tabVolunteer.setContent(FXMLLoader.load(getClass().getClassLoader().getResource(FxmlLocation.ADD_VOLUNTEERS + ".fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void volunteerOverview() {
        tabVolunteer.setContent(previousVolunteer);
    }

    public void setTabHome(Tab tabHome) {
        this.tabHome = tabHome;
    }

    public void setTabVolunteer(Tab tabVolunteer) {
        this.tabVolunteer = tabVolunteer;
        previousVolunteer = tabVolunteer.getContent();
    }

    public void setTabStudent(Tab tabStudent) {
        this.tabStudent = tabStudent;
    }

    public void setTabReport(Tab tabReport) {
        this.tabReport = tabReport;
    }
}
