package com.apon.taalmaatjes.frontend.tabs.volunteers.detail.instance;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AddVolunteerInstance implements Screen {
    private String volunteerExtId;

    @FXML
    DatePicker inputDateStart, inputDateEnd;

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
    }


    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        // Save the match.
        Result result = VolunteerAPI.getInstance().addInstance(getReturn());
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

    private VolunteerInstanceReturn getReturn() {
        VolunteerInstanceReturn volunteerInstanceReturn = new VolunteerInstanceReturn();
        volunteerInstanceReturn.setVolunteerExternalIdentifier(volunteerExtId);
        volunteerInstanceReturn.setDateStart(DateTimeUtil.convertLocalDateToSqlDate(inputDateStart.getValue()));
        volunteerInstanceReturn.setDateEnd(DateTimeUtil.convertLocalDateToSqlDate(inputDateEnd.getValue()));

        return volunteerInstanceReturn;
    }
}
