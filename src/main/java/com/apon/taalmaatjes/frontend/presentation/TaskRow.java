package com.apon.taalmaatjes.frontend.presentation;

import javafx.beans.property.SimpleStringProperty;

public class TaskRow {
    // TODO: rewrite this class so it is "normal".
    private SimpleStringProperty extId;
    private SimpleStringProperty title;
    private SimpleStringProperty description;

    public TaskRow(String extId, String title, String description) {
        this.extId = new SimpleStringProperty(extId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        setDescription(description);
    }

    public String getExtId() {
        return extId.get();
    }
    public void setExtId(String extId) {
        this.extId.set(extId);
    }

    public String getTitle() {
        return title.get();
    }
    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }
    public void setDescription(String description) {
        description = description.replace('\n', ' ');

        this.description.set(description);
    }
}
