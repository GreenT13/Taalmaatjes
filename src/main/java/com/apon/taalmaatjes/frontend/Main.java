package com.apon.taalmaatjes.frontend;

import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Main {

    @FXML
    TabPane tabPaneMain;

    @FXML
    Tab tabHome, tabVolunteer, tabStudent, tabReport;

    @FXML
    public void initialize() {
        // Add all tabs to the Transition class so it can transition.
        Transition.getInstance().setTabHome(tabHome);
        Transition.getInstance().setTabVolunteer(tabVolunteer);
        Transition.getInstance().setTabStudent(tabStudent);
        Transition.getInstance().setTabReport(tabReport);
    }
}
