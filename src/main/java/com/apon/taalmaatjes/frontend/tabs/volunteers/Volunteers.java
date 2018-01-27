package com.apon.taalmaatjes.frontend.tabs.volunteers;

import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Person;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class Volunteers {
    private boolean isVisible = false;

    @FXML
    private FlowPane flowPaneAdvancedSearch;

    @FXML
    private TableView<Person> tableViewResult;

    @FXML
    private TextField textFieldSearch;

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
        hboxError.managedProperty().bind(hboxError.visibleProperty());
        hideError();

        // Initialize the table.
        ((TableColumn)tableViewResult.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Person, String>("extId"));
        ((TableColumn)tableViewResult.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        ((TableColumn)tableViewResult.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        ((TableColumn)tableViewResult.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        // Add line to make sure that the space of the invisible panel is removed.
        flowPaneAdvancedSearch.managedProperty().bind(flowPaneAdvancedSearch.visibleProperty());

        // Set default visibility.
        flowPaneAdvancedSearch.setVisible(isVisible);

        // Fill the table.
        search();

        // Add listener to when an item is clicked.
        tableViewResult.getSelectionModel().selectedItemProperty().addListener(
            (ChangeListener) (observableValue, oldValue, newValue) -> {
                //Check whether item is selected and set value of selected item to Label
                if(tableViewResult.getSelectionModel().getSelectedItem() != null) {
                    // Handle actions in different function.
                    clickedOnRow((Person) newValue);
                }
            });
    }

    private void clickedOnRow(Person person) {
        // Clear selection model when out of the ChangeListener (so add runLater).
        // https://stackoverflow.com/questions/23098483/javafx-tableview-clear-selection-gives-nullpointerexception
        Platform.runLater(() -> tableViewResult.getSelectionModel().clearSelection());

        // Transition to detail screen.
        Transition.getInstance().volunteerDetail(person.getExtId());
    }

    private void fillTable(List<VolunteerReturn> list) {
        ObservableList<Person> data = FXCollections.observableArrayList();

        for (VolunteerReturn volunteerReturn : list) {
            data.add(new Person(volunteerReturn.getExternalIdentifier(),
                    volunteerReturn.getFirstName(),
                    volunteerReturn.getLastName(),
                    volunteerReturn.getEmail()));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) {
        search();
    }

    @FXML
    public void search() {
        Result result = VolunteerAPI.getInstance().searchByInput(textFieldSearch.getText());
        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        fillTable((List<VolunteerReturn>) result.getResult());
    }

    /**
     * Toggle the visibility of flowPaneAdvancedSearch.
     * @param actionEvent
     */
    @FXML
    public void toggleAdvancedSearch(ActionEvent actionEvent) {
        isVisible = !isVisible;
        flowPaneAdvancedSearch.setVisible(isVisible);
    }

    @FXML
    public void addVolunteer(ActionEvent actionEvent) {
        Transition.getInstance().volunteerAdd();
    }
}
