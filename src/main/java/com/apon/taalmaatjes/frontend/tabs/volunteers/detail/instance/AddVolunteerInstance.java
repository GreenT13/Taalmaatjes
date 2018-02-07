package com.apon.taalmaatjes.frontend.tabs.volunteers.detail.instance;

import com.apon.taalmaatjes.backend.api.VolunteerInstanceAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.VolunteerInstanceKey;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AddVolunteerInstance implements Screen {
    private VolunteerInstanceKey volunteerInstanceKey;

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
    public void setObject(Object volunteerInstanceKey) {
        this.volunteerInstanceKey = (VolunteerInstanceKey) volunteerInstanceKey;

        if (this.volunteerInstanceKey.getVolunteerInstanceExtId() == null) {
            return;
        }

        // We are in edit mode. So retrieve information and prefill screen.
        Result result = VolunteerInstanceAPI.getInstance().getVolunteerInstance(this.volunteerInstanceKey.getVolunteerExtId(), this.volunteerInstanceKey.getVolunteerInstanceExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        prefill((VolunteerInstanceReturn) result.getResult());
    }

    @FXML
    public void handleActionSave(ActionEvent actionEvent) {
        // Save or edit the match.
        Result result;
        if (volunteerInstanceKey.getVolunteerInstanceExtId() == null) {
            result = VolunteerInstanceAPI.getInstance().addVolunteerInstance(getReturn());
        } else {
            result = VolunteerInstanceAPI.getInstance().updateVolunteerInstance(getReturn());
        }
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // Go back to the detail screen (only place we could've come from).
        // Could just insert a transition, but there is not real need to at this point.
        TransitionHandler.getInstance().goBack(volunteerInstanceKey.getVolunteerExtId());
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goBack(volunteerInstanceKey.getVolunteerExtId());
    }

    private VolunteerInstanceReturn getReturn() {
        VolunteerInstanceReturn volunteerInstanceReturn = new VolunteerInstanceReturn();
        volunteerInstanceReturn.setVolunteerExtId(volunteerInstanceKey.getVolunteerExtId());
        volunteerInstanceReturn.setExternalIdentifier(volunteerInstanceKey.getVolunteerInstanceExtId());
        volunteerInstanceReturn.setDateStart(DateTimeUtil.convertLocalDateToSqlDate(inputDateStart.getValue()));
        volunteerInstanceReturn.setDateEnd(DateTimeUtil.convertLocalDateToSqlDate(inputDateEnd.getValue()));

        return volunteerInstanceReturn;
    }

    private void prefill(VolunteerInstanceReturn volunteerInstanceReturn) {
        inputDateStart.setValue(volunteerInstanceReturn.getDateStart().toLocalDate());
        if (volunteerInstanceReturn.getDateEnd() != null){
            inputDateEnd.setValue(volunteerInstanceReturn.getDateEnd().toLocalDate());
        }
    }
}
