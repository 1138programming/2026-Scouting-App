package com.scouting_app_2026.UIElements;

import static com.scouting_app_2026.MainActivity.datapointEventValue;

import android.content.res.ColorStateList;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Button extends UIElement {
    private final android.widget.Button button;
    private final UndoStack undostack;
    private final boolean dataTracking;
    private final int titleLength;
    private int currValue;
    private int maxValue = 99;
    private int minValue = 0;
    private int color;

    /**
     * This constructor is used to create an independent button and takes a
     * binding since it's the only datapoint for the given button binding. It
     * is also a data button so it takes an UndoStack.
     *
     * @param datapointID datapointID of the button
     * @param button binding of the button
     * @param undoStack undoStack of the given fragment
     */
    public Button(int datapointID, @Nullable android.widget.Button button, UndoStack undoStack) {
        super(datapointID);
        this.button = button;
        this.undostack = undoStack;
        this.dataTracking = true;
        if(this.button != null) {
            this.titleLength = this.button.length() - 1;
            this.color = Objects.requireNonNull(this.button.getBackgroundTintList()).getDefaultColor();
            this.button.setOnClickListener(view -> clicked());
        }
        else {
            this.titleLength = 0;
        }
    }

    /**
     * This constructor is used to create an independent button and takes a
     * binding since it's the only datapoint for the given button binding. It
     * doesn't take an UndoStack as it doesn't track data and is for UI purposes
     * only.
     *
     * @param datapointID datapointID of the button (should be negative given that
     *                    the button doesn't store data)
     * @param button binding of the button
     */
    public Button(int datapointID, @Nullable android.widget.Button button) {
        super(datapointID);
        this.button = button;
        this.undostack = null;
        this.dataTracking = false;
        if(this.button != null) {
            this.titleLength = this.button.length() - 1;
            this.color = Objects.requireNonNull(this.button.getBackgroundTintList()).getDefaultColor();
            button.setOnClickListener(view -> clicked());
        }
        else {
            this.titleLength = 0;
        }
    }

    @Override
    public void clicked() {
        if(increment()) {
            undostack.addTimestamp(this, true);
        }
        super.clicked();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        if(button != null) {
            button.setBackgroundTintList(ColorStateList.valueOf(this.color));
        }
    }

    /**
     * Called by {@link UndoStack} to decrease the value displayed on the button.
     */
    @Override
    public void undo() {
        decrement();
    }

    /**
     * Called by {@link UndoStack} to increase the value displayed on the button.
     */
    @Override
    public void redo() {
        increment();
    }

    /**
     *
     * @return Returns the default datapoint event value.
     */
    @Override
    public String getValue() {
        return datapointEventValue;
    }

    /**
     *
     * @return Returns the current value of the counter
     */
    public int getCounter() {
        return currValue;
    }

    @Override
    public boolean getIndependent() {
        return false;
    }

    public void setCounter(int value) {
        currValue = Math.min(value, maxValue);
        if(button != null) {
            CharSequence temp = button.getText().subSequence(0,titleLength) + String.valueOf(currValue);
            button.setText(temp);
        }
    }

    public boolean isDataTracking() {
        return dataTracking;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * Called to increment value of the button. If the value exceeds a maximum
     * value it remains at that value.
     * @return Returns a {@code boolean} for whether or not the button's value
     * was updated or not due to it being at max value.
     */
    private boolean increment() {
        if(!dataTracking) return false;

        boolean updated = false;

        if (currValue < maxValue) {
            currValue++;
            updated = true;
        }

        if(button != null) {
            String title = (String) button.getText();
            title = title.substring(0,titleLength-1) + currValue;
            button.setText(title);
        }
        return updated;
    }

    /**
     * Called to decrement value of the button. If the value goes below a minimum value
     * (zero by default), it remains at the minimum. This doesn't have a {@code boolean}
     * to track if the decrement was successful because this is only used by {@link Button#undo()}
     */
    private void decrement() {
        if(currValue > minValue) {
            currValue--;
        }

        if(button != null) {
            String title = (String) button.getText();
            title = title.substring(0,titleLength) + currValue;
            button.setText(title);
        }
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
