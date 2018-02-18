package com.apon.taalmaatjes.frontend.tabs.task;

import com.apon.taalmaatjes.backend.api.TaskAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.frontend.presentation.MessageResource;
import com.apon.taalmaatjes.frontend.presentation.VolunteerRow;
import com.apon.taalmaatjes.frontend.presentation.Screen;
import com.apon.taalmaatjes.frontend.presentation.TaskRow;
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
public class Tasks implements Screen {

    private boolean isVisible = false;

    @FXML
    private FlowPane flowPaneAdvancedSearch;

    @FXML
    private TableView<TaskRow> tableViewResult;

    @FXML
    private TextField textFieldSearch;

    @FXML
    ComboBox<String> comboStatus;

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
        ((TableColumn)tableViewResult.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TaskRow, String>("extId"));
        ((TableColumn)tableViewResult.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("title"));
        ((TableColumn)tableViewResult.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("description"));

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
                        clickedOnRow((TaskRow) newValue);
                    }
                });
    }

    private void clickedOnRow(TaskRow taskRow) {
        // Clear selection model when out of the ChangeListener (so addVolunteer runLater).
        // https://stackoverflow.com/questions/23098483/javafx-tableview-clear-selection-gives-nullpointerexception
        Platform.runLater(() -> tableViewResult.getSelectionModel().clearSelection());

        // TransitionHandler to detail screen.
        TransitionHandler.getInstance().goToScreen(ScreenEnum.TASKS_DETAIL, taskRow.getExtId(),
                true, true);
    }

    private void fillTable(List<TaskReturn> list) {
        ObservableList<TaskRow> data = FXCollections.observableArrayList();

        for (TaskReturn taskReturn : list) {
            data.add(new TaskRow(taskReturn.getTaskExtId(),
                    taskReturn.getTitle(),
                    taskReturn.getDescription()));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    private void handleActionSearch(ActionEvent actionEvent) {
        // Don't do an advanced search if we have the advanced bar collapsed.
        Result result;
        if (!isVisible) {
            result = TaskAPI.getInstance().advancedSearch(textFieldSearch.getText(),
                    null, null, null);
        } else {
            Boolean isFinished = null;
            Boolean isCancelled = null;
            switch (comboStatus.getValue()) {
                case "Open":
                    isFinished = false;
                    isCancelled = false;
                    break;
                case "Afgehandeld":
                    isFinished = true;
                    isCancelled = false;
                    break;
                case "Geannuleerd":
                    isFinished = false;
                    isCancelled = true;
                    break;
            }
            result = TaskAPI.getInstance().advancedSearch(textFieldSearch.getText(), isFinished, isCancelled,null);
        }

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        fillTable((List<TaskReturn>) result.getResult());
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
    public void goToScreenAddTask(ActionEvent actionEvent) {
        TransitionHandler.getInstance().goToScreen(ScreenEnum.TASKS_ADD, null,
                false, true);
    }

    @Override
    public void setObject(Object o) {
        // Do nothing.
    }

}
