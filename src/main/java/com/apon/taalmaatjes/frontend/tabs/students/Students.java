package com.apon.taalmaatjes.frontend.tabs.students;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Person;
import com.apon.taalmaatjes.frontend.presentation.TextUtils;
import com.apon.taalmaatjes.frontend.transition.Transition;
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

import java.util.List;

public class Students {
    private boolean isVisible = false;

    @FXML
    private FlowPane flowPaneAdvancedSearch;

    @FXML
    private TableView<Person> tableViewResult;

    @FXML
    private TextField textFieldSearch, inputCity;

    @FXML
    ComboBox<String> comboIsLookingForVolunteer, comboIsGroup, comboHasMatch;

    @FXML
    HBox hboxError; @FXML
    Label labelError;

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
        Transition.getInstance().studentDetail(person.getExtId());
    }

    private void fillTable(List<StudentReturn> list) {
        ObservableList<Person> data = FXCollections.observableArrayList();

        for (StudentReturn studentReturn : list) {
            data.add(new Person(studentReturn.getExternalIdentifier(),
                    studentReturn.getFirstName(),
                    studentReturn.getLastName(),
                    null));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) {
        search();
    }

    @FXML
    public void search() {
        // Don't do an advanced search if we have the advanced bar collapsed.
        Result result;
        if (!isVisible) {
            result = StudentAPI.getInstance().advancedSearch(textFieldSearch.getText(),
                    null, null, null);
        } else {
            result = StudentAPI.getInstance().advancedSearch(textFieldSearch.getText(),
                    TextUtils.getComboValue(comboIsLookingForVolunteer.getValue()),
                    TextUtils.getComboValue(comboIsGroup.getValue()),
                    TextUtils.getComboValue(comboHasMatch.getValue()));
        }

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        fillTable((List<StudentReturn>) result.getResult());
        return;
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
    public void addStudent(ActionEvent actionEvent) {
        Transition.getInstance().studentAdd();
    }


}