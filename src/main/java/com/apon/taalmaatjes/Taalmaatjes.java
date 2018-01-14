package com.apon.taalmaatjes;

import com.apon.taalmaatjes.backend.database.update.VersionManagement;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.transition.FxmlLocation;
import com.guigarage.flatterfx.FlatterFX;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Taalmaatjes extends Application {

    public static void main(String[] args) {
        // First update the database.
        try {
            VersionManagement.getInstance().runUpdates(FrontendContext.getInstance().getContext());
        } catch (Exception e) {
            Log.error("Could not update the database.", e);
            return;
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Set title of the program.
        primaryStage.setTitle("Taalmaatjes");

        // Set correct stylesheets.
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(FxmlLocation.TAALMAATJES + ".css");

        // Load the Main.fxml on the screen.
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource(FxmlLocation.MAIN + ".fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Use the FlatterFX css-theme (the flatterfx.css is not used!).
        FlatterFX.style();
    }

}
