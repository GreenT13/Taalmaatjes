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

    private Map<TabEnum, ScreenEnum> tabStartScreen;

    // Store the frontend tabs in this map, so we can set content.
    private Map<TabEnum, Tab> mapEnumTab;

    // Track screens using breadcrum.
    private Stack<Transition> currentBreadcrum;
    private TabEnum currentTab;
    private ScreenEnum currentScreen;

    private TransitionHandler() {
        tabStartScreen = new HashMap();
        tabStartScreen.put(TabEnum.VOLUNTEERS, ScreenEnum.VOLUNTEERS_OVERVIEW);
        tabStartScreen.put(TabEnum.STUDENTS, ScreenEnum.STUDENTS_OVERVIEW);
        tabStartScreen.put(TabEnum.TASKS, ScreenEnum.TASKS_OVERVIEW);
        tabStartScreen.put(TabEnum.REPORTS, ScreenEnum.REPORT);

        mapEnumTab = new HashMap();
        currentBreadcrum = new Stack();
        currentTab = TabEnum.VOLUNTEERS;
        currentScreen = tabStartScreen.get(currentTab);
    }

    private void initializeTab(TabEnum tabEnum) {
        currentBreadcrum = new Stack();
        currentTab = tabEnum;
        currentScreen = tabStartScreen.get(tabEnum);
        loadScreen(currentScreen, null);
    }

    /**
     * Change to the breadcrum from a different tab.
     * @param tabEnum The tab you changed to.
     */
    public void goToTab(TabEnum tabEnum) {
        Log.logDebug("Start transitioned to tab " + tabEnum.toString());
        // Reset the complete breadcrum
        initializeTab(tabEnum);
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
                !currentBreadcrum.peek().getScreen().equals(currentScreen))) {
            Transition transition = new Transition();
            transition.setScreen(currentScreen);

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
        currentScreen =  screenEnum;

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
            if (transition.getNode() != null && !transition.getScreen().equals(currentScreen)) {
                // Load the node.
                Log.logDebug("Returning to already initialized tab " + transition.getScreen().toString());
                currentScreen = transition.getScreen();
                mapEnumTab.get(currentTab).setContent(transition.getNode());
                return;
            }

            if (transition.getScreen() != null && !transition.getScreen().equals(currentScreen)) {
                currentScreen =  transition.getScreen();
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

    public void setTabVolunteer(Tab tabVolunteer) {
        mapEnumTab.put(TabEnum.VOLUNTEERS, tabVolunteer);
    }

    public void setTabStudent(Tab tabStudent) {
        mapEnumTab.put(TabEnum.STUDENTS, tabStudent);
    }

    public void setTabTask(Tab tabTask) {
        mapEnumTab.put(TabEnum.TASKS, tabTask);
    }

    public void setTabReport(Tab tabReport) {
        mapEnumTab.put(TabEnum.REPORTS, tabReport);
    }
}
