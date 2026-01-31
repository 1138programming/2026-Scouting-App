package com.scouting_app_template.UIElements;

import android.widget.RadioButton;

import com.scouting_app_template.MainActivity;


public class RadioGroup extends UIElement {
    private final android.widget.RadioGroup radioGroup;
    private boolean unchecking = false;
    public RadioGroup(int datapointID, android.widget.RadioGroup radioGroup) {
        super(datapointID);
        this.radioGroup = radioGroup;
        this.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(!unchecking && (checkedId != -1)) {
                super.clicked();
            }
            else {
                unchecking = false;
            }
        });
    }

    public void unselect() {
        unchecking = true;
        radioGroup.clearCheck();
    }

    @Override
    public String getValue() {
        return ((RadioButton)((MainActivity)MainActivity.context).findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
    }
}
