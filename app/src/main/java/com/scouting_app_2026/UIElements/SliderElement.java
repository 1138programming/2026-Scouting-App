package com.scouting_app_2026.UIElements;

import com.google.android.material.slider.Slider;

public class SliderElement extends UIElement {
    private final Slider slider;
    private final UndoStack undoStack;
    public SliderElement(int datapointID, Slider slider, UndoStack undoStack) {
        super(datapointID);
        this.slider = slider;
        this.slider.addOnChangeListener((slider1, value, fromUser) -> SliderElement.this.clicked());
        this.undoStack = undoStack;
        this.undoStack.addElement(this);
    }

    public SliderElement(int datapointID, Slider slider) {
        super(datapointID);
        this.slider = slider;
        this.slider.addOnChangeListener((slider1, value, fromUser) -> SliderElement.this.clicked());
        this.undoStack = null;
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
        return Float.toString(slider.getValue());
    }

    public void setLabels(String[] labels) {
        slider.setValueFrom(0);
        slider.setValueTo(labels.length-1);
        this.setStepSize(1);

        slider.setLabelFormatter(value -> labels[(int)value]);
    }
}
