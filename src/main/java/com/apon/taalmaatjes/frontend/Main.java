package com.apon.taalmaatjes.frontend;

import com.apon.taalmaatjes.frontend.transition.TabEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@SuppressWarnings("unused")
public class Main {

    @FXML
    TabPane tabPaneMain;

    @FXML
    Tab tabVolunteer, tabStudent, tabTask, tabReport;

    @FXML
    public void initialize() {
        // Add all tabs to the TransitionHandler class so it can transition.
        TransitionHandler.getInstance().setTabVolunteer(tabVolunteer);
        TransitionHandler.getInstance().setTabStudent(tabStudent);
        TransitionHandler.getInstance().setTabTask(tabTask);
        TransitionHandler.getInstance().setTabReport(tabReport);

        tabPaneMain.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldTab, newTab) -> {
                    if (newTab.equals(tabVolunteer)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.VOLUNTEERS);
                    } else if (newTab.equals(tabStudent)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.STUDENTS);
                    } else if (newTab.equals(tabTask)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.TASKS);
                    } else if (newTab.equals(tabReport)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.REPORTS);
                    }
                }
        );
    }
}
