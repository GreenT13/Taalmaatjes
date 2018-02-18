package com.apon.taalmaatjes.frontend.tabs.volunteers;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.VolunteerRow;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import com.apon.taalmaatjes.frontend.transition.ScreenEnum;
import com.apon.taalmaatjes.frontend.transition.TransitionHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class Volunteers implements Screen {
    private boolean isVisible = false;

    @FXML
    private FlowPane flowPaneAdvancedSearch;

    @FXML
    private TableView<VolunteerRow> tableViewResult;

    @FXML
    private TextField textFieldSearch, inputCity;

    @FXML
    ComboBox<String> comboIsActive, comboTraining, comboHasMatch;

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

    @SuppressWarnings("Duplicates")
    @FXML
    public void initialize() {
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        // Initialize the table.
        ((TableColumn)tableViewResult.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("extId"));
        ((TableColumn)tableViewResult.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("firstName"));
        ((TableColumn)tableViewResult.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("lastName"));
        ((TableColumn)tableViewResult.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("email"));

        // Add line to make sure that the space of the invisible panel is removed.
        flowPaneAdvancedSearch.managedProperty().bind(flowPaneAdvancedSearch.visibleProperty());

        // Set default visibility.
        flowPaneAdvancedSearch.setVisible(isVisible);

        // Fill the table.
        handleActionSearch(null);

        // Add listener to when an item is clicked.
        tableViewResult.getSelectionModel().selectedItemProperty().addListener(
            (ChangeListener) (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if(tableViewResult.getSelectionModel().getSelectedItem() != null) {
                    // Handle actions in different function.
                    clickedOnRow((VolunteerRow) newValue);
                }
            });

        // Update search when typing.
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> handleActionSearch(null));
        inputCity.textProperty().addListener((observable, oldValue, newValue) -> handleActionSearch(null));
    }

    private void clickedOnRow(VolunteerRow volunteerRow) {
        // Clear selection model when out of the ChangeListener (so addVolunteer runLater).
        // https://stackoverflow.com/questions/23098483/javafx-tableview-clear-selection-gives-nullpointerexception
        Platform.runLater(() -> tableViewResult.getSelectionModel().clearSelection());

        // TransitionHandler to detail screen.
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_DETAIL, volunteerRow.getExtId(),
                true, true);
    }

    private void fillTable(List<VolunteerReturn> list) {
        ObservableList<VolunteerRow> data = FXCollections.observableArrayList();

        for (VolunteerReturn volunteerReturn : list) {
            data.add(new VolunteerRow(volunteerReturn.getExternalIdentifier(),
                    volunteerReturn.getFirstName(),
                    volunteerReturn.getLastName(),
                    volunteerReturn.getEmail()));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    private void handleActionSearch(ActionEvent actionEvent) {
        // Don't do an advanced search if we have the advanced bar collapsed.
        Result result;
        if (!isVisible) {
            result = VolunteerAPI.getInstance().advancedSearch(textFieldSearch.getText(),
                    TextUtils.getComboValue(comboIsActive.getValue()), null, null, null);
        } else {
            result = VolunteerAPI.getInstance().advancedSearch(textFieldSearch.getText(),
                    TextUtils.getComboValue(comboIsActive.getValue()),
                    TextUtils.getComboValue(comboTraining.getValue()),
                    TextUtils.getComboValue(comboHasMatch.getValue()),
                    inputCity.getText());
        }

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        fillTable((List<VolunteerReturn>) result.getResult());
    }

    /**
     * Toggle the visibility of flowPaneAdvancedSearch.
     * @param actionEvent Unused.
     */
    @FXML
    public void handleActionToggleAdvancedSearch(ActionEvent actionEvent) {
        isVisible = !isVisible;
        flowPaneAdvancedSearch.setVisible(isVisible);
        handleActionSearch(null);
    }

    @FXML
    public void goToScreenAddVolunteer(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.VOLUNTEERS_ADD, null,
                false, true);
    }

    @Override
    public void setObject(Object o) {
        // Do nothing.
    }
}
