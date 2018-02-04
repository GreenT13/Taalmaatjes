package com.apon.taalmaatjes.frontend.transition;

import javafx.scene.Node;

public class Transition {
    private ScreenEnum screenFrom;
    private ScreenEnum screenTo;
    private Node nodeFrom;

    public Transition() { }

    public ScreenEnum getScreenFrom() {
        return screenFrom;
    }

    public void setScreenFrom(ScreenEnum screenFrom) {
        this.screenFrom = screenFrom;
    }

    public ScreenEnum getScreenTo() {
        return screenTo;
    }

    public void setScreenTo(ScreenEnum screenTo) {
        this.screenTo = screenTo;
    }

    public Node getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(Node nodeFrom) {
        this.nodeFrom = nodeFrom;
    }
}
