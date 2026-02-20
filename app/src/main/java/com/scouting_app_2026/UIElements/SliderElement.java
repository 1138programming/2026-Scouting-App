package com.scouting_app_2026.UIElements;

import com.google.android.material.slider.Slider;

public class SliderElement extends UIElement {
    private final Slider slider;

    public SliderElement(int datapointID, Slider slider, UndoStack undoStack) {
        super(datapointID);

        this.slider = slider;
        this.slider.addOnChangeListener((slider1, value, fromUser) -> SliderElement.this.clicked());
        undoStack.addElement(this);
    }

    @Override
    public void clicked() {
        super.clicked();
    }

    public void setStepSize(float stepSize) {
        slider.setStepSize(stepSize);
    }

    @Override
    public String getValue() {
        return Integer.toString((int)slider.getValue());
    }

    public void setLabels(String[] labels) {
        slider.setValueFrom(0);
        slider.setValueTo(labels.length-1);
        this.setStepSize(1);

        slider.setLabelFormatter(value -> labels[(int)value]);
    }
}
