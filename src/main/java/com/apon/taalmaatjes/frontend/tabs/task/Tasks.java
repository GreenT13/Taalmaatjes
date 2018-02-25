package com.apon.taalmaatjes.frontend.tabs.task;

import com.apon.taalmaatjes.backend.api.TaskAPI;
import com.apon.taalmaatjes.backend.api.VolunteerAPI;
import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.TaskReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.util.StringUtil;
import com.apon.taalmaatjes.frontend.presentation.*;
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
import javafx.scene.input.KeyEvent;
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
    ComboBox<String> comboVolunteer;

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
        ((TableColumn)tableViewResult.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TaskRow, String>("dateToBeFinished"));
        ((TableColumn)tableViewResult.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("title"));
        ((TableColumn)tableViewResult.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("nameVolunteer"));
        ((TableColumn)tableViewResult.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<VolunteerRow, String>("description"));

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

        // Search when textFieldSearch content is changed.
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> handleActionSearch(null));

        comboVolunteer.getEditor().textProperty().addListener((observable, oldValue, newValue) -> changeList(oldValue, newValue));
    }

    @SuppressWarnings("Duplicates")
    private void changeList(String oldValue, String newValue) {
        if (newValue.contains(":")) {
            return;
        }

        if (newValue.trim().length() == 0) {
            comboVolunteer.setItems(null);
            comboVolunteer.hide();
            hideError();
            return;
        }
        Result result = VolunteerAPI.getInstance().advancedSearch(newValue,null,null,null, null);

        if (result == null || result.hasErrors()) {
            showError(result);
            return;
        }

        // So now we create a list of names to fill in the combobox.
        ObservableList<String> comboVolunteerObservableList = FXCollections.observableArrayList();
        List<VolunteerReturn> volunteerReturns = (List<VolunteerReturn>) result.getResult();
        for (VolunteerReturn volunteerReturn : volunteerReturns) {
            comboVolunteerObservableList.add(volunteerReturn.getExternalIdentifier() + ": " + NameUtil.getVolunteerName(volunteerReturn));
        }

        // Fill the combobox.
        comboVolunteer.setItems(comboVolunteerObservableList);
        comboVolunteer.show();
    }

    private void clickedOnRow(TaskRow taskRow) {
        // Clear selection model when out of the ChangeListener (so addVolunteer runLater).
        // https://stackoverflow.com/questions/23098483/javafx-tableview-clear-selection-gives-nullpointerexception
        Platform.runLater(() -> tableViewResult.getSelectionModel().clearSelection());

        // TransitionHandler to detail screen.
        TransitionHandler.getInstance().goToScreen(ScreenEnum.TASKS_DETAIL, taskRow.getExtId(),
                false, true);
    }

    private void fillTable(List<TaskReturn> list) {
        ObservableList<TaskRow> data = FXCollections.observableArrayList();

        for (TaskReturn taskReturn : list) {
            Result result = VolunteerAPI.getInstance().getVolunteer(taskReturn.getVolunteerExtId());
            if (result == null || result.hasErrors()) {
                showError(result);
                return;
            }
            VolunteerReturn volunteerReturn = (VolunteerReturn) result.getResult();

            data.add(new TaskRow(taskReturn.getTaskExtId(),
                    StringUtil.getOutputString(taskReturn.getDateToBeFinished()),
                    taskReturn.getTitle(),
                    NameUtil.getVolunteerName(volunteerReturn),
                    taskReturn.getDescription()));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    private void handleActionSearchKey(KeyEvent keyEvent) {
        handleActionSearch(null);
    }

    @FXML
    private void handleActionSearch(ActionEvent actionEvent) {
        // Don't do an advanced search if we have the advanced bar collapsed.
        Result result;
        Boolean isFinished = null;
        switch (comboStatus.getValue()) {
            case "Open":
                isFinished = false;
                break;
            case "Afgerond":
                isFinished = true;
                break;
        }
        if (!isVisible || comboVolunteer.getValue() == null) {
            result = TaskAPI.getInstance().advancedSearch(textFieldSearch.getText(), isFinished,null);
        } else {
            result = TaskAPI.getInstance().advancedSearch(textFieldSearch.getText(), isFinished,
                    comboVolunteer.getValue().substring(0, comboVolunteer.getValue().indexOf(":")));
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
        handleActionSearch(null);
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
