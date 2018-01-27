package com.apon.taalmaatjes.frontend.transition;

import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.frontend.tabs.volunteers.add.AddVolunteer;
import com.apon.taalmaatjes.frontend.tabs.volunteers.detail.DetailVolunteer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Transition {
    private static Transition ourInstance = new Transition();
    public static boolean hasAddedVolunteer = false;

    public static Transition getInstance() {
        return ourInstance;
    }

    private Transition() { }

    private Parent load(@Nonnull String path) {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource(path));
        } catch (IOException e) {
            Log.error("Could not find fxml with path: " + path, e);
            return null;
        }
    }

    private Tab tabHome, tabVolunteer, tabStudent, tabReport;
    private Node previousVolunteer;

    public void volunteerDetail(String volunteerExtId) {
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
        detailVolunteer.setVolunteerExtId(volunteerExtId);

        // Set content last, so we make sure that what is shown on the screen is initialized.
        tabVolunteer.setContent(root);
    }

    public void volunteerAdd() {
        tabVolunteer.setContent(load(FxmlLocation.ADD_VOLUNTEERS + ".fxml"));
    }

    public void volunteerAdd(String volunteerExtId) {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FxmlLocation.ADD_VOLUNTEERS + ".fxml"));
        // Load so we have the controller instantiated.
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        AddVolunteer addVolunteer = loader.getController();
        addVolunteer.setVolunteerExtId(volunteerExtId);

        // Set content last, so we make sure that what is shown on the screen is initialized.
        tabVolunteer.setContent(root);
    }

    public void volunteerOverview() {
        if (hasAddedVolunteer) {
            try {
                tabVolunteer.setContent(FXMLLoader.load(getClass().getClassLoader().getResource(FxmlLocation.VOLUNTEERS + ".fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            hasAddedVolunteer = false;
        } else {
            tabVolunteer.setContent(previousVolunteer);
        }
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
