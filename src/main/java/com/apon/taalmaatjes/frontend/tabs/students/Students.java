package com.apon.taalmaatjes.frontend.tabs.students;

import com.apon.taalmaatjes.backend.api.StudentAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.Person;
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
public class Students implements Screen {
    private boolean isVisible = false;

    @FXML
    private FlowPane flowPaneAdvancedSearch;

    @FXML
    private TableView<Person> tableViewResult;

    @FXML
    private TextField textFieldSearch;

    @FXML
    ComboBox<String> comboIsLookingForVolunteer, comboIsGroup, comboHasMatch;

    @FXML
    HBox hboxError; @FXML
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

    @SuppressWarnings("Duplicates")
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
        // Clear selection model when out of the ChangeListener (so addVolunteer runLater).
        // https://stackoverflow.com/questions/23098483/javafx-tableview-clear-selection-gives-nullpointerexception
        Platform.runLater(() -> tableViewResult.getSelectionModel().clearSelection());

        // TransitionHandler to detail screen.
        TransitionHandler.getInstance().goToScreen(ScreenEnum.STUDENTS_DETAIL, person.getExtId(),
                true, true);
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
    public void handleActionPressEnter(ActionEvent actionEvent) {
        search();
    }

    private void search() {
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
    }

    /**
     * Toggle the visibility of flowPaneAdvancedSearch.
     * @param actionEvent Unused.
     */
    @FXML
    public void handleActionToggleAdvancedSearch(ActionEvent actionEvent) {
        isVisible = !isVisible;
        flowPaneAdvancedSearch.setVisible(isVisible);
    }

    @FXML
    public void goToScreenAddStudent(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.STUDENTS_ADD, null,
                false, true);
    }


    @Override
    public void setObject(Object o) {
        // Do nothing.
    }
}
