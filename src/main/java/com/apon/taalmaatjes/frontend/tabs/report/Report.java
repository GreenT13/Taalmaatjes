package com.apon.taalmaatjes.frontend.tabs.report;

import com.apon.taalmaatjes.backend.facade.ReportResult;
import com.apon.taalmaatjes.backend.facade.ReportingFacade;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.frontend.FrontendContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Report {
    ReportingFacade reportingFacade;

    @FXML
    VBox vboxResult;

    @FXML
    TextArea textAreaResult;

    @FXML
    DatePicker datePickerStart, datePickerEnd;

    @FXML
    public void initialize() {
        reportingFacade = new ReportingFacade(FrontendContext.getInstance().getContext());
        // Hide the result, which gets shown when result button is clicked.
        // Add line to make sure that the space is removed.
        vboxResult.managedProperty().bind(vboxResult.visibleProperty());
        vboxResult.setVisible(false);
    }

    @FXML
    public void createReport(ActionEvent actionEvent) {
        // Retrieve the needed information.
        ReportResult report = reportingFacade.createReport(
                DateTimeUtil.convertLocalDateToSqlDate(datePickerStart.getValue()),
                DateTimeUtil.convertLocalDateToSqlDate(datePickerEnd.getValue()));

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
    }
}
