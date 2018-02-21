package com.apon.taalmaatjes.frontend.tabs.volunteers.detail.log;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class EditLog implements Screen {
    private String volunteerExtId;

    private final static String CSS = "<head><style>p { display: inline; }</style></head>";
    private final static String HTML_BEFORE = "<body style=\"color: rgb(104,117,128);\" contenteditable=\"false\"";
    private final static String HTML_AFTER = CSS + "<body style=\"color: rgb(104,117,128); padding: 0px; margin: 0px;\" contenteditable=\"false\"";

    @FXML
    HTMLEditor htmlEditor;

    @FXML HBox hboxError; @FXML Label labelError;

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();
    }

    private void showError(@Nullable Result result) {
        hboxError.setVisible(true);
        if (result != null) {
            labelError.setText(MessageResource.getInstance().getValue(result.getErrorMessage()));
        } else {
            labelError.setText("Something went horribly wrong.");
        }
    }

    private void hideError() {
        hboxError.setVisible(false);
    }


    @Override
    public void setObject(Object volunteerExtId) {
        this.volunteerExtId = (String) volunteerExtId;

        Result result = VolunteerAPI.getInstance().getVolunteer(this.volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        prefill((VolunteerReturn) result.getResult());
    }

    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        // Retrieve the volunteer and just edit the log.
        Result result = VolunteerAPI.getInstance().getVolunteer(volunteerExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();

        // For some reason, the contenteditable does not set to false.
        String log = htmlEditor.getHtmlText().replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        log = log.replace(HTML_BEFORE, HTML_AFTER);
        Log.logDebug("AFDFESKFN UIEOFS");
        Log.logDebug(log);
        volunteerReturn.setLog(log);

        result = VolunteerAPI.getInstance().updateVolunteer(volunteerReturn);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back to the detail screen (only place we could've come from).
        // Could just insert a transition, but there is not real need to at this point.
        TransitionHandler.getInstance().goBack(volunteerExtId);
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(volunteerExtId);
    }

    private void prefill(VolunteerReturn volunteerReturn) {
        // Set margin

        if (volunteerReturn.getLog() != null) {
            htmlEditor.setHtmlText(volunteerReturn.getLog().replace(HTML_AFTER, HTML_BEFORE));
        } else {
            htmlEditor.setHtmlText("<html>" + CSS + HTML_BEFORE + "></body></html>");
        }

        Log.logDebug("XXDFAFZZXC");
        Log.logDebug(htmlEditor.getHtmlText());
    }

}
