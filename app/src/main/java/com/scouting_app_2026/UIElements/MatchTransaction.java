package com.scouting_app_2026.UIElements;

public class MatchTransaction<T extends UIElement> {
    private final T element;
    private final int datapointID;
    private final int timestamp;
    private final boolean stopping;
    public MatchTransaction(T element, int timestamp, boolean stopping) {
        this.element = element;
        this.datapointID = this.element.getID();
        this.timestamp = timestamp;
        this.stopping = stopping;
    }

    public UIElement getElement() {
        return element;
    }

    public int getDatapointID() {
        return datapointID;
    }

    public int getTimestamp() {
        return timestamp;
    }
    public boolean isStopping() {
        return stopping;
    }

    public boolean undo() {
        this.element.undo();
        return stopping;
    }

    public boolean redo() {
        this.element.redo();
        return stopping;
    }
}
