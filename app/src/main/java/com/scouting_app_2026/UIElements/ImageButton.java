package com.scouting_app_2026.UIElements;

import android.content.res.ColorStateList;

import java.util.Objects;

public class ImageButton extends UIElement {
    private final android.widget.ImageButton button;
    private int color;

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
    public ImageButton(int datapointID, android.widget.ImageButton button) {
        super(datapointID);
        this.button = button;
        this.color = Objects.requireNonNull(button.getBackgroundTintList()).getDefaultColor();
        button.setOnClickListener(view -> clicked());
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
