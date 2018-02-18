package com.apon.taalmaatjes.frontend.tabs.report;

import com.apon.taalmaatjes.backend.api.ReportAPI;
import com.apon.taalmaatjes.backend.api.TaalmaatjesAPI;
import com.apon.taalmaatjes.backend.api.returns.ReportReturn;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class Report implements Screen {
    @FXML
    VBox vboxResult;

    @FXML
    TextArea textAreaResult;

    @FXML
    TextField labelActiveVolunteers, labelActiveMatches;

    @FXML
    DatePicker datePickerStart, datePickerEnd;

    @FXML HBox hboxError; @FXML Label labelError;

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

    @FXML
    public void initialize() {
        // Hide the result, which gets shown when result button is clicked.
        // Add line to make sure that the space is removed.
        vboxResult.managedProperty().bind(vboxResult.visibleProperty());
        vboxResult.setVisible(false);

        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        TextUtils.setWidthToContent(labelActiveVolunteers);
        TextUtils.setWidthToContent(labelActiveMatches);

        // Set the values of the two labels.
        Result result = TaalmaatjesAPI.getInstance().countActiveVolunteersToday();
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        Integer count = (Integer) result.getResult();
        labelActiveVolunteers.setText(count.toString());

        result = TaalmaatjesAPI.getInstance().countActiveMatches();
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        count = (Integer) result.getResult();
        labelActiveMatches.setText(count.toString());
    }

    @FXML
    public void handleActionCreateReport(ActionEvent actionEvent) {
        // Retrieve the needed information.
        Result result = ReportAPI.getInstance().createReport(
                DateTimeUtil.convertLocalDateToSqlDate(datePickerStart.getValue()),
                DateTimeUtil.convertLocalDateToSqlDate(datePickerEnd.getValue()));

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }
        ReportReturn report = (ReportReturn) result.getResult();

        vboxResult.setVisible(true);
        // Clear the text first.
        textAreaResult.setText("");
        textAreaResult.appendText(
                "Aantal nieuwe vrijwilligers: " + report.getNrOfNewVolunteers() + "\n" +
                "Aantal actieve vrijwilligers: " + report.getNrOfActiveVolunteers() + "\n" +
                "Aantal nieuwe cursisten: " + report.getNrOfNewStudents() + "\n" +
                "Aantal actieve cursisten: " + report.getNrOfActiveStudents() + "\n" +
                "Aantal nieuwe groepen: " + report.getNrOfNewGroups() + "\n" +
                "Aantal actieve groepen: " + report.getNrOfActiveGroups() + "\n");

        hideError();
    }

    @Override
    public void setObject(Object o) {
        // Do nothing.
    }
}
