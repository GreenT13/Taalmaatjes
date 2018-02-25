package com.apon.taalmaatjes.frontend.transition;

import javafx.scene.Node;

public class Transition {
    private ScreenEnum screen;
    private Node node;
    private Object object;

    public Transition() { }

    public ScreenEnum getScreen() {
        return screen;
    }

    public void setScreen(ScreenEnum screen) {
        this.screen = screen;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
