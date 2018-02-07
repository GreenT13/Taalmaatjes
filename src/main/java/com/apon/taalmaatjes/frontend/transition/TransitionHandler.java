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

        // Initialize all starting screen.
        mapCurrentScreen = new HashMap();
        mapCurrentScreen.put(TabEnum.HOME, ScreenEnum.HOME);
        mapCurrentScreen.put(TabEnum.VOLUNTEERS, ScreenEnum.VOLUNTEERS_OVERVIEW);
        mapCurrentScreen.put(TabEnum.STUDENTS, ScreenEnum.STUDENTS_OVERVIEW);
        mapCurrentScreen.put(TabEnum.REPORTS, ScreenEnum.REPORT);

        // Always start on the home screen.
        currentBreadcrum = mapBreadcrum.get(TabEnum.HOME);
        currentTab = TabEnum.HOME;

        // Initialize variable. It will be filled in Main.java using setters in this class.
        mapEnumTab = new HashMap();
    }

    // Map for retrieving tab from enum.
    private Map<TabEnum, Tab> mapEnumTab;

    // Dynamically track the breadcrum.
    private Map<TabEnum, Stack<Transition>> mapBreadcrum;
    private Stack<Transition> currentBreadcrum;
    private Map<TabEnum, ScreenEnum> mapCurrentScreen;
    private TabEnum currentTab;

    /**
     * Change to the breadcrum from a different tab.
     * @param tabEnum The tab you changed to.
     */
    public void goToTab(TabEnum tabEnum) {
        Log.logDebug("Start transitioned to tab " + tabEnum.toString());
        currentBreadcrum = mapBreadcrum.get(tabEnum);
        currentTab = tabEnum;
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

        // Register the transition if it is actually part of the breadcrum path.
        // Only addVolunteer the transition if we actually have a new element to addVolunteer.
        // TODO: what if you didn't save node last time, but you want to now? Is this even desirable?
        if (canGoBack && (currentBreadcrum.isEmpty() ||
                !currentBreadcrum.peek().getScreen().equals(mapCurrentScreen.get(currentTab)))) {
            Transition transition = new Transition();
            transition.setScreen(mapCurrentScreen.get(currentTab));

            // Only addVolunteer a node if you can also go back.
            if (rememberCurrentNode) {
                transition.setNode(mapEnumTab.get(currentTab).getContent());
            }

            currentBreadcrum.push(transition);
        }

        // Transition to next screen.
        if (!loadScreen(screenEnum, object)) {
            // Something went wrong.
            return;
        }
        mapCurrentScreen.put(currentTab, screenEnum);

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
            if (transition.getNode() != null && !transition.getScreen().equals(mapCurrentScreen.get(currentTab))) {
                // Load the node.
                Log.logDebug("Returning to already initialized tab " + transition.getScreen().toString());
                mapCurrentScreen.put(currentTab, transition.getScreen());
                mapEnumTab.get(currentTab).setContent(transition.getNode());
                return;
            }

            if (transition.getScreen() != null && !transition.getScreen().equals(mapCurrentScreen.get(currentTab))) {
                mapCurrentScreen.put(currentTab, transition.getScreen());
                // Load the screen.
                Log.logDebug("Start returning to tab " + transition.getScreen().toString());
                loadScreen(transition.getScreen(), object);
                Log.logDebug("End returning to tab " + transition.getScreen().toString());
                return;
            }

        } while (!currentBreadcrum.empty());
        Log.logError("Something went wrong going back.");
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
