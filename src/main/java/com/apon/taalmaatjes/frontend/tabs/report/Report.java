package com.apon.taalmaatjes.frontend.tabs.report;

import com.apon.taalmaatjes.backend.api.ReportAPI;
import com.apon.taalmaatjes.backend.api.returns.ReportReturn;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Report {
    @FXML
    VBox vboxResult;

    @FXML
    TextArea textAreaResult;

    @FXML
    DatePicker datePickerStart, datePickerEnd;

    @FXML HBox hboxError; @FXML Label labelError;

    public void showError(Result result) {
        hboxError.setVisible(true);
        labelError.setText(MessageResource.getInstance().getValue(result.getErrorMessage()));
    }

    public void hideError() {
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
    }

    @FXML
    public void createReport(ActionEvent actionEvent) {
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

}
