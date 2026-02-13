package com.scouting_app_2026.UIElements;

import android.content.res.ColorStateList;

import java.util.ArrayList;


public class ButtonTimeToggle extends UIElement {
    private final android.widget.Button button;
    private final ArrayList<Runnable> onSelectFunctions = new ArrayList<>();
    private final ArrayList<Runnable> onDeselectFunctions = new ArrayList<>();
    private final UndoStack undoStack;
    private final ColorStateList normalColor;
    private final ColorStateList altColor;
    private boolean colorSwapped = false;

    public ButtonTimeToggle(int datapointID, android.widget.Button button, UndoStack undoStack, ColorStateList altColor) {
        super(datapointID);
        this.button = button;
        this.undoStack = undoStack;
        this.undoStack.addElement(this);
        button.setBackgroundTintList(button.getBackgroundTintList());
        normalColor = button.getBackgroundTintList();
        this.altColor = altColor;
        this.button.setOnClickListener(View1 -> clicked());
    }

    @Override
    public void clicked() {
        super.clicked();

        swapColors();
        this.specialClicked();

        undoStack.addTimestamp(this);
    }

    /**
     * Runs functions that are only run on select or deselect
     */
    private void specialClicked() {
        // if the button was just selected then iterate through the onSelectFunctions and vice versa
        for(Runnable onClickFunction : (colorSwapped ? onSelectFunctions : onDeselectFunctions)) {
            onClickFunction.run();
        }
    }

    public void setOnSelectFunction(Runnable onClickFunction) {
        onSelectFunctions.add(onClickFunction);
    }

    public void setOnDeselectFunction(Runnable onClickFunction) {
        onDeselectFunctions.add(onClickFunction);
    }

    @Override
    public void undo() {
        swapColors();
        specialClicked();
    }

    @Override
    public void redo() {
        swapColors();
        specialClicked();
    }

    @Override
    public boolean getIndependent() {
        return false;
    }

    private void swapColors() {
        colorSwapped = !colorSwapped;
        if (colorSwapped) {
            button.setBackgroundTintList(altColor);
        }
        else {
            button.setBackgroundTintList(normalColor);
        }
    }

    private void setColor(int color) {
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void enable() {
        button.setEnabled(true);
    }

    @Override
    public void disable(boolean override) {
        if(disableable || override) {
            button.setEnabled(false);
        }
    }
}