package com.apon.taalmaatjes.frontend.tabs.students.detail;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.NameUtil;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.Nullable;

public class DetailStudent implements Screen {
    private String studentExtId;

    @FXML
    TextField labelName, labelIsGroup, labelGroupIdentification, labelHasQuit;

    @FXML
    VBox vboxMatch;

    @Override
    public void setObject(Object studentExtId) {
        this.studentExtId = (String) studentExtId;
        initializeValues();
    }

    @FXML
    HBox hboxError;
    @FXML
    Label labelError;

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

    /**
     * Controller is initialized before volunteerId is set, therefore we don't use @FXML here.
     */
    private void initializeValues() {
        Result result = StudentAPI.getInstance().getStudent(studentExtId);
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        StudentReturn studentReturn = (StudentReturn) result.getResult();

        // Set the name.
        String name = String.valueOf(studentReturn.getExternalIdentifier()) + ": ";
        if (!studentReturn.getGroup()) {
            if (studentReturn.getFirstName() != null) {
                name += studentReturn.getFirstName() + " ";
            }
            if (studentReturn.getInsertion() != null) {
                name += studentReturn.getInsertion()  + " ";
            }
        }
        name += studentReturn.getLastName();
        labelName.setText(name);

        // Set is group
        labelIsGroup.setText(StringUtil.getOutputString(studentReturn.getGroup()));

        labelGroupIdentification.setText(StringUtil.getOutputString(studentReturn.getGroupIdentification()));
        labelHasQuit.setText(StringUtil.getOutputString(studentReturn.getHasQuit()));

        for (VolunteerMatchReturn volunteerMatchReturn : studentReturn.getListVolunteerMatch()) {
            addMatchLine(volunteerMatchReturn);
        }
    }

    @FXML
    public void goBack() {
        TransitionHandler.getInstance().goBack();
    }

    private void addMatchLine(VolunteerMatchReturn volunteerMatchReturn) {
        Result result = VolunteerAPI.getInstance().getVolunteer(volunteerMatchReturn.getVolunteerExtId());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        String volunteerName = NameUtil.getVolunteerName((VolunteerReturn) result.getResult());

        Label label = new Label();
        label.getStyleClass().add("labelMatch");
        String text = "Is begeleid van " + StringUtil.getOutputString(volunteerMatchReturn.getDateStart()) + " tot ";
        if (volunteerMatchReturn.getDateEnd() == null) {
            text += "nu";
        } else {
            text += volunteerMatchReturn.getDateEnd();
        }
        text += " door " + volunteerName + ".";

        label.setText(text);
        vboxMatch.getChildren().add(label);
    }

    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        TextUtils.setWidthToContent(labelName);
        TextUtils.setWidthToContent(labelIsGroup);
        TextUtils.setWidthToContent(labelGroupIdentification);
        TextUtils.setWidthToContent(labelHasQuit);
    }

    @SuppressWarnings("unused")
    @FXML
    public void goToScreenEditStudent(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.STUDENTS_ADD, studentExtId,
                false, true);
    }
}
