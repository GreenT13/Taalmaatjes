package com.apon.taalmaatjes.frontend.transition;

import com.apon.taalmaatjes.frontend.presentation.Screen;
import javafx.fxml.FXMLLoader;

import java.util.HashMap;
import java.util.Map;

public class FxmlLocation {
    private static FxmlLocation ourInstance = new FxmlLocation();

    public static FxmlLocation getInstance() {
        return ourInstance;
    }

    private Map<ScreenEnum, String> mapEnumFxml;

    private FxmlLocation() {
        // Initialize the map that goes from enum to fxml.
        mapEnumFxml = new HashMap();
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_OVERVIEW, VOLUNTEERS_OVERVIEW);
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_DETAIL, VOLUNTEERS_DETAIL);
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_ADD, VOLUNTEERS_ADD);
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_ADD_MATCH, VOLUNTEERS_ADD_MATCH);
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_ADD_INSTANCE, VOLUNTEERS_ADD_INSTANCE);
        mapEnumFxml.put(ScreenEnum.VOLUNTEERS_EDIT_LOG, VOLUNTEERS_EDIT_LOG);

        mapEnumFxml.put(ScreenEnum.STUDENTS_OVERVIEW, STUDENTS_OVERVIEW);
        mapEnumFxml.put(ScreenEnum.STUDENTS_ADD, STUDENTS_ADD);
        mapEnumFxml.put(ScreenEnum.STUDENTS_DETAIL, STUDENTS_DETAIL);

        mapEnumFxml.put(ScreenEnum.TASKS_OVERVIEW, TASKS_OVERVIEW);
        mapEnumFxml.put(ScreenEnum.TASKS_ADD, TASKS_ADD);
        mapEnumFxml.put(ScreenEnum.TASKS_DETAIL, TASKS_DETAIL);

        mapEnumFxml.put(ScreenEnum.REPORT, REPORT);
    }

    // Used in other places.
    public final static String TAALMAATJES = "com/apon/taalmaatjes/Taalmaatjes";
    public final static String MAIN = "com/apon/taalmaatjes/frontend/Main";

    // Volunteers
    private final static String VOLUNTEERS_OVERVIEW = "com/apon/taalmaatjes/frontend/tabs/volunteers/Volunteers";
    private final static String VOLUNTEERS_DETAIL = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/DetailVolunteer";
    private final static String VOLUNTEERS_ADD = "com/apon/taalmaatjes/frontend/tabs/volunteers/add/AddVolunteer";
    private final static String VOLUNTEERS_ADD_MATCH = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/match/AddVolunteerMatch";
    private final static String VOLUNTEERS_ADD_INSTANCE = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/instance/AddVolunteerInstance";
    private final static String VOLUNTEERS_EDIT_LOG = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/log/EditLog";

    // Students
    private final static String STUDENTS_OVERVIEW = "com/apon/taalmaatjes/frontend/tabs/students/Students";
    private final static String STUDENTS_ADD = "com/apon/taalmaatjes/frontend/tabs/students/add/AddStudent";
    private final static String STUDENTS_DETAIL = "com/apon/taalmaatjes/frontend/tabs/students/detail/DetailStudent";

    // Tasks
    private final static String TASKS_OVERVIEW = "com/apon/taalmaatjes/frontend/tabs/task/Tasks";
    private final static String TASKS_ADD = "com/apon/taalmaatjes/frontend/tabs/task/add/AddTask";
    private final static String TASKS_DETAIL = "com/apon/taalmaatjes/frontend/tabs/task/detail/DetailTask";

    // Reports
    private final static String REPORT = "com/apon/taalmaatjes/frontend/tabs/report/Report";

    /**
     * Return the loader based on the screenEnum.
     * @param screenEnum Screen to load.
     * @return The loader.
     */
    public FXMLLoader getLoaderForScreen(ScreenEnum screenEnum) {
        return new FXMLLoader(getClass().getClassLoader().getResource(mapEnumFxml.get(screenEnum) + ".fxml"));
    }
}
