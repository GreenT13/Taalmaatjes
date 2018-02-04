package com.apon.taalmaatjes.frontend.presentation;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class TextUtils {

    public static void setWidthToContent(TextField tf) {
        tf.setMinWidth(Region.USE_PREF_SIZE);
        tf.setMaxWidth(Region.USE_PREF_SIZE);
        tf.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> tf.setPrefWidth(getWidth(tf, tf.getText())));
        });

        Platform.runLater(() -> tf.setPrefWidth(getWidth(tf, tf.getText())));

        // Make sure that right clicking does not show up context menu.
        tf.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
    }

    private static double getWidth(TextField tf, String currText) {
        Text text = new Text(currText);
        text.setFont(tf.getFont()); // Set the same font, so the size is the same
        double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                + tf.getPadding().getLeft() + tf.getPadding().getRight() // Add the padding of the TextField
                + 2d; // Add some spacing
        return width + 5;
    }

    public static Boolean getComboValue(String value) {
        if (value == null || value.equals("")) {
            // Empty
            return null;
        } else if (value.equals("Ja")) {
            return true;
        } else if (value.equals("Nee")) {
            return false;
        }

        return null;
    }

}
