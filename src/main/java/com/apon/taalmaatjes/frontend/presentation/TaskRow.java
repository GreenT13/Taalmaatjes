package com.apon.taalmaatjes.frontend.presentation;

import javafx.beans.property.SimpleStringProperty;

public class TaskRow {
    // TODO: rewrite this class so it is "normal".
    private SimpleStringProperty extId;
    private SimpleStringProperty dateToBeFinished;
    private SimpleStringProperty title;
    private SimpleStringProperty nameVolunteer;
    private SimpleStringProperty description;

    public TaskRow(String extId, String dateToBeFinished, String title, String nameVolunteer, String description) {
        this.extId = new SimpleStringProperty(extId);
        this.dateToBeFinished = new SimpleStringProperty(dateToBeFinished);
        this.title = new SimpleStringProperty(title);
        this.nameVolunteer = new SimpleStringProperty(nameVolunteer);
        this.description = new SimpleStringProperty(description);
        setDescription(description);
    }

    public String getExtId() { return extId.get(); }
    public void setExtId(String extId) { this.extId.set(extId); }

    public String getDateToBeFinished() {
        return dateToBeFinished.get();
    }
    public void setDateToBeFinished(String dateToBeFinished) {
        this.dateToBeFinished.set(dateToBeFinished);
    }

    public String getTitle() {
        return title.get();
    }
    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getNameVolunteer() { return nameVolunteer.get(); }
    public void setNameVolunteer(String nameVolunteer) { this.nameVolunteer.set(nameVolunteer); }

    public String getDescription() {
        return description.get();
    }
    public void setDescription(String description) {
        if (description != null) {
            description = description.replace('\n', ' ');
        }

        this.description.set(description);
    }
}
