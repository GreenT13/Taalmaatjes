package com.apon.taalmaatjes.frontend;

import com.apon.taalmaatjes.frontend.transition.TabEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("unused")
public class Main {

    @FXML
    TabPane tabPaneMain;

    @FXML
    Tab tabVolunteer, tabStudent, tabTask, tabReport;

    private boolean needToRefresh = true;
    private TabEnum currentTabEnum = TabEnum.VOLUNTEERS;

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
                        currentTabEnum = TabEnum.VOLUNTEERS;
                    } else if (newTab.equals(tabStudent)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.STUDENTS);
                        currentTabEnum = TabEnum.STUDENTS;
                    } else if (newTab.equals(tabTask)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.TASKS);
                        currentTabEnum = TabEnum.TASKS;
                    } else if (newTab.equals(tabReport)) {
                        TransitionHandler.getInstance().goToTab(TabEnum.REPORTS);
                        currentTabEnum = TabEnum.REPORTS;
                    }
                    needToRefresh = false;
                }
        );
    }

    @FXML
    public void onMouseClick(MouseEvent mouseEvent) {
        if (!needToRefresh) {
            needToRefresh = true;
            return;
        }

        if (mouseEvent.getSceneX() > 150) {
            return;
        }

        // Refresh current tab.
        TransitionHandler.getInstance().goToTab(currentTabEnum);
    }
}
