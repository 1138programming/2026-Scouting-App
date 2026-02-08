package com.scouting_app_2026.UIElements;

import static com.scouting_app_2026.MainActivity.TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;


public class RadioGroup extends UIElement {
    private final android.widget.RadioGroup radioGroup;
    private final UndoStack undoStack;
    private final Stack<RadioButton> radioInputStack = new Stack<>();
    private final Stack<RadioButton> radioRedoStack = new Stack<>();
    private boolean unchecking = false;
    private boolean stackChange = false;
    public RadioGroup(int datapointID, android.widget.RadioGroup radioGroup) {
        super(datapointID);
        this.radioGroup = radioGroup;
        this.undoStack = null;
        this.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(!unchecking && (checkedId != -1)) {
                super.clicked();
            }
            else {
                unchecking = false;
            }
        });
    }

    public RadioGroup(ArrayList<Integer> datapointIDs, android.widget.RadioGroup radioGroup, UndoStack undoStack) {
        super(0);
        this.radioGroup = radioGroup;
        this.undoStack = undoStack;
        int buttonNumber = radioGroup.getChildCount();
        if(buttonNumber == datapointIDs.size()) {
            for (int i = 0; i < buttonNumber; i++) {
                new RadioButton(datapointIDs.get(i), (android.widget.RadioButton)radioGroup.getChildAt(i), this.undoStack);
            }
        }
        else {
            Log.e(TAG, "Radio group button number doesn't match number of datapoint IDs");
        }
    }

    @Override
    public void undo() {
        radioRedoStack.push(radioInputStack.pop());
        try {
            radioGroup.check(radioInputStack.peek().getButton().getId());
        }
        catch(EmptyStackException e) {
            radioGroup.clearCheck();
        }
    }

    @Override
    public void redo() {
        radioGroup.check(radioRedoStack.peek().getButton().getId());
        radioInputStack.push(radioRedoStack.pop());
    }

    public void unselect() {
        unchecking = true;
        radioGroup.clearCheck();
    }

    @Override
    public String getValue() {
        if(radioGroup.getCheckedRadioButtonId() == -1) {
            return "None selected";
        }
        else return ((android.widget.RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
    }

    public class RadioButton extends UIElement {
        private final android.widget.RadioButton radioButton;
        private RadioButton(int datapointID, android.widget.RadioButton radioButton, UndoStack undoStack) {
            super(datapointID);

            undoStack.addElement(this);

            this.radioButton = radioButton;
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(!stackChange && isChecked) {
                    undoStack.addTimestamp(this);
                    radioInputStack.push(this);
                }
            });
        }

        @Override
        public void undo() {
            stackChange = true;
            RadioGroup.this.undo();
            stackChange = false;
        }

        @Override
        public void redo() {
            stackChange = true;
            RadioGroup.this.redo();
            stackChange = false;
        }

        public android.widget.RadioButton getButton() {
            return radioButton;
        }

        @Override
        public String getValue() {
            return radioButton.getText().toString();
        }

        @Override
        public boolean getIndependent() {
            return false;
        }
    }

}
