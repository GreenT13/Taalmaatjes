package com.apon.taalmaatjes.frontend.transition;

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

        mapEnumFxml.put(ScreenEnum.STUDENTS_OVERVIEW, STUDENTS_OVERVIEW);
        mapEnumFxml.put(ScreenEnum.STUDENTS_ADD, STUDENTS_ADD);
        mapEnumFxml.put(ScreenEnum.STUDENTS_DETAIL, STUDENTS_DETAIL);
    }

    // Used in other places.
    public final static String TAALMAATJES = "com/apon/taalmaatjes/Taalmaatjes";
    public final static String MAIN = "com/apon/taalmaatjes/frontend/Main";

    // Home

    // Volunteers
    private final static String VOLUNTEERS_OVERVIEW = "com/apon/taalmaatjes/frontend/tabs/volunteers/Volunteers";
    private final static String VOLUNTEERS_DETAIL = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/DetailVolunteer";
    private final static String VOLUNTEERS_ADD = "com/apon/taalmaatjes/frontend/tabs/volunteers/add/AddVolunteer";
    private final static String VOLUNTEERS_ADD_MATCH = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/match/AddVolunteerMatch";
    private final static String VOLUNTEERS_ADD_INSTANCE = "com/apon/taalmaatjes/frontend/tabs/volunteers/detail/instance/AddVolunteerInstance";

    // Students
    private final static String STUDENTS_OVERVIEW = "com/apon/taalmaatjes/frontend/tabs/students/Students";
    private final static String STUDENTS_ADD = "com/apon/taalmaatjes/frontend/tabs/students/add/AddStudent";
    private final static String STUDENTS_DETAIL = "com/apon/taalmaatjes/frontend/tabs/students/detail/DetailStudent";

    // Reports

    /**
     * Return the loader based on the screenEnum.
     * @param screenEnum Screen to load.
     * @return The loader.
     */
    public FXMLLoader getLoaderForScreen(ScreenEnum screenEnum) {
        return new FXMLLoader(getClass().getClassLoader().getResource(mapEnumFxml.get(screenEnum) + ".fxml"));
    }
}
