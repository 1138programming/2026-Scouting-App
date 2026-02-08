package com.scouting_app_2026.UIElements;

import java.util.ArrayList;

public class UIElement {
    protected int datapointID;
    protected boolean disableable = true;
    private final ArrayList<Runnable> onClickFunctions = new ArrayList<>();

    /**
     * Base constructor for UI Elements that are going to be
     * undone or redone.
     */
    public UIElement(int datapointID) {
        this.datapointID = datapointID;
    }

    public int getID() {
        return datapointID;
    }
    public void setID(int datapointID) {
        this.datapointID = datapointID;
    }
    public String getValue() {
        return "true";
    }

    public void clicked() {
        for(Runnable onClickFunction : onClickFunctions) {
            onClickFunction.run();
        }
    }
    public void setOnClickFunction(Runnable onClickFunction) {
        onClickFunctions.add(onClickFunction);
    }
    public void undo() {

    }

    public void redo() {

    }

    public boolean getIndependent() {
        return true;
    }

    public void enable() {

    }

    public void disable(boolean override) {

    }

    public void disableable(boolean disableable) {
        this.disableable = disableable;
    }
}
