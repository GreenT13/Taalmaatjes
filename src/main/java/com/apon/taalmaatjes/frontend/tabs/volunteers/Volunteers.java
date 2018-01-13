package com.apon.taalmaatjes.frontend.tabs.volunteers;

import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.facade.VolunteerFacade;
import com.apon.taalmaatjes.frontend.FrontendContext;
import com.apon.taalmaatjes.frontend.presentation.Person;
import com.apon.taalmaatjes.frontend.transition.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class Volunteers {
    VolunteerFacade volunteerFacade;
    boolean isVisible = true;

    @FXML
    FlowPane flowPaneAdvancedSearch;

    @FXML
    TableView<Person> tableViewResult;

    @FXML
    public void initialize() {
        // Initialize the table.
        ((TableColumn)tableViewResult.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        ((TableColumn)tableViewResult.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        ((TableColumn)tableViewResult.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        // Add line to make sure that the space of the invisible panel is removed.
        flowPaneAdvancedSearch.managedProperty().bind(flowPaneAdvancedSearch.visibleProperty());

        // Set default visibility.
        flowPaneAdvancedSearch.setVisible(isVisible);

        // Fill the table.
        volunteerFacade = new VolunteerFacade(FrontendContext.getInstance().getContext());
        fillTable(volunteerFacade.get50MostRecent());
    }

    public void fillTable(List<VolunteerPojo> list) {
        ObservableList<Person> data = FXCollections.observableArrayList();

        for (VolunteerPojo volunteerPojo : list) {
            data.add(new Person(volunteerPojo.getFirstname(),
                    volunteerPojo.getLastname(),
                    volunteerPojo.getEmail()));
        }

        tableViewResult.setItems(data);
    }

    @FXML
    public void clickedOnTable(SortEvent<Person> sortEvent) {
        System.out.println("Actioned");
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
