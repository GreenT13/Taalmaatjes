package com.apon.taalmaatjes.frontend.transition;

import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TransitionHandler {
    private static TransitionHandler ourInstance = new TransitionHandler();

    public static TransitionHandler getInstance() {
        return ourInstance;
    }

    private TransitionHandler() {
        mapBreadcrum = new HashMap();
        mapBreadcrum.put(TabEnum.HOME, new Stack());
        mapBreadcrum.put(TabEnum.VOLUNTEERS, new Stack());
        mapBreadcrum.put(TabEnum.STUDENTS, new Stack());
        mapBreadcrum.put(TabEnum.REPORTS, new Stack());

        // Always start on the home screen.
        currentBreadcrum = mapBreadcrum.get(TabEnum.HOME);
        currentTab = TabEnum.HOME;
        currentScreen = ScreenEnum.HOME;

        mapEnumTab = new HashMap();
    }

    // Map for retrieving tab from enum.
    private Map<TabEnum, Tab> mapEnumTab;

    // Dynamically track the breadcrum.
    private Map<TabEnum, Stack<Transition>> mapBreadcrum;
    private Stack<Transition> currentBreadcrum;
    private ScreenEnum currentScreen;
    private TabEnum currentTab;

    /**
     * Change to the breadcrum from a different tab.
     * @param tabEnum The tab you changed to.
     */
    public void goToTab(TabEnum tabEnum) {
        Log.logDebug("Start transitioned to tab " + tabEnum.toString());
        currentBreadcrum = mapBreadcrum.get(tabEnum);
        currentTab = tabEnum;
        if (currentBreadcrum.empty()) {
            // Set the default screen.
            switch (tabEnum) {
                case HOME:
                    currentScreen = ScreenEnum.HOME;
                    break;
                case VOLUNTEERS:
                    currentScreen = ScreenEnum.VOLUNTEERS_OVERVIEW;
                    break;
                case STUDENTS:
                    currentScreen = ScreenEnum.STUDENTS_OVERVIEW;
                    break;
                case REPORTS:
                    currentScreen = ScreenEnum.REPORT;
                    break;
            }
        } else {
            currentScreen = currentBreadcrum.peek().getScreenTo();
        }
        Log.logDebug("End transitioned to tab " + tabEnum.toString());
    }

    /**
     * Transition to another screen.
     * @param screenEnum Screen to transition to.
     * @param object Object that is passed to the next screen.
     * @param rememberCurrentNode Indicates whether node needs to be reloaded or not.
     * @param canGoBack Indicate whether you can come back to this screen or not.
     */
    public void goToScreen(ScreenEnum screenEnum, Object object, boolean rememberCurrentNode, boolean canGoBack) {
        Log.logDebug("Start transitioning to screen " + screenEnum.toString());
        // Register the transition.
        Transition transition = new Transition();
        if (canGoBack) {
            transition.setScreenFrom(currentScreen);
        } else {
            transition.setScreenFrom(null);
        }
        transition.setScreenTo(screenEnum);
        if (rememberCurrentNode) {
            transition.setNodeFrom(mapEnumTab.get(currentTab).getContent());
        }
        currentBreadcrum.push(transition);
        currentScreen = screenEnum;

        if (!loadScreen(screenEnum, object)) {
            // Something went wrong.
            return;
        }
        Log.logDebug("End transitioning to screen " + screenEnum.toString());
    }

    public void goBack() {
        goBack(null);
    }

    public void goBack(Object object) {
        // Loop over the stack until you find a transition for which holds:
        // node in memory is not empty OR (from screen is not null AND not equals to current screen).
        Log.logDebug("Start going back.");
        do {
            Transition transition = currentBreadcrum.pop();
            if (transition.getNodeFrom() != null && !transition.getScreenFrom().equals(currentScreen)) {
                // Load the node.
                Log.logDebug("Returning to already initialized tab " + transition.getScreenFrom().toString());
                currentScreen = transition.getScreenFrom();
                mapEnumTab.get(currentTab).setContent(transition.getNodeFrom());
                return;
            }

            if (transition.getScreenFrom() != null && !transition.getScreenFrom().equals(currentScreen)) {
                currentScreen = transition.getScreenFrom();
                // Load the screen.
                Log.logDebug("Start returning to tab " + transition.getScreenFrom().toString());
                loadScreen(transition.getScreenFrom(), object);
                Log.logDebug("End returning to tab " + transition.getScreenFrom().toString());
                return;
            }

        } while (!currentBreadcrum.empty());
    }

    private boolean loadScreen(ScreenEnum screenEnum, Object object) {
        // Load the correct screen.
        FXMLLoader loader = FxmlLocation.getInstance().getLoaderForScreen(screenEnum);
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Log.logError("Could not load screen " + screenEnum.toString(), e);
            return false;
        }

        // Set the object, if it is non-null.
        if (object != null) {
            Screen screen = loader.getController();
            screen.setObject(object);
        }

        // Show tab on screen.
        mapEnumTab.get(currentTab).setContent(root);
        return true;
    }

    public void setTabHome(Tab tabHome) {
        mapEnumTab.put(TabEnum.HOME, tabHome);
    }

    public void setTabVolunteer(Tab tabVolunteer) {
        mapEnumTab.put(TabEnum.VOLUNTEERS, tabVolunteer);
    }

    public void setTabStudent(Tab tabStudent) {
        mapEnumTab.put(TabEnum.STUDENTS, tabStudent);
    }

    public void setTabReport(Tab tabReport) {
        mapEnumTab.put(TabEnum.REPORTS, tabReport);
    }
}
