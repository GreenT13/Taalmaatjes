package com.apon.taalmaatjes;

import com.apon.taalmaatjes.backend.api.TaalmaatjesAPI;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.update.VersionManagement;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.frontend.transition.FxmlLocation;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Taalmaatjes extends Application {
    private static boolean hasErrors = false;

    public static void main(String[] args) {
        // First update the database.
        try {
            Context context = new Context();
            if (VersionManagement.getInstance().runUpdates(context)) {
                hasErrors = true;
            }
            context.close();
        } catch (Exception e) {
            Log.logError("Could not update the database.", e);
            return;
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (hasErrors) {
            new Alert(Alert.AlertType.ERROR,"Er is iets mis gegaan met de database. " +
                    "Verwijder de database ofn eem contact op met de ontwikkelaar.").show();
            return;
        }

        // Set title of the program.
        primaryStage.setTitle("Taalmaatjes " + TaalmaatjesAPI.getInstance().getVersionNumber().getResult() +
                " (" + TaalmaatjesAPI.getInstance().getReleaseDate().getResult() + ")");

        // Set correct stylesheets (before loading any screen so .css variables are loaded).
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(FxmlLocation.TAALMAATJES + ".css");

        // Load the Main.fxml on the screen.
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(FxmlLocation.MAIN + ".fxml")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return;
        }
        Scene sceneRoot = new Scene(root);
        primaryStage.setScene(sceneRoot);
        primaryStage.show();

        // This next line is probably not needed (in comments in case I need it later).
        //sceneRoot.getStylesheets().add(getClass().getResource("Taalmaatjes.css").toExternalForm());
    }

}
