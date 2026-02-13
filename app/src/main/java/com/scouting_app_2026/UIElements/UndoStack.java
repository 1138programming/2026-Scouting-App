package com.scouting_app_2026.UIElements;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.autonLengthMs;
import static com.scouting_app_2026.MainActivity.teleopLengthMs;
import static com.scouting_app_2026.datapointIDs.ReversedDatapointIDs.reversedDatapointIDs;

import android.util.Log;
import android.widget.Toast;

import com.scouting_app_2026.JSON.JSONManager;
import com.scouting_app_2026.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

/**
 *
 */
public class UndoStack {
    private final Stack<UIElement> inputStack = new Stack<>();
    private final Stack<Integer> timestamps = new Stack<>();
    private Stack<UIElement> redoStack = new Stack<>();
    private final Stack<Integer> redoTimestamps = new Stack<>();
    private final HashMap<Integer, UIElement> allElements = new HashMap<>();
    private final ArrayList<UIElement> disableOnlyElements = new ArrayList<>();
    private final MainActivity mainActivity;
    private boolean matchPhaseAuton;

    public UndoStack(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void addElement(UIElement element) {
        allElements.put(element.getID(), element);
    }

    public void addDisableOnlyElement(UIElement element) {
        disableOnlyElements.add(element);
    }

    public UIElement getElement(int datapointID) {
        return allElements.get(datapointID);
    }

    public void addTimestamp(UIElement element) {
        if(!allElements.containsKey(element.getID())) {
            Log.e(TAG, "Element not added to undoStack", new Throwable().fillInStackTrace());
            addElement(element);
        }

        inputStack.add(element);
        timestamps.add((int) (Calendar.getInstance(Locale.US).getTimeInMillis()-(mainActivity.getCurrStartTime())));

        redoStack = new Stack<>();
    }

    public JSONArray getTimestamps(JSONObject datapointTemplate) {
        JSONManager manager = new JSONManager(datapointTemplate);

        for(UIElement element : inputStack) {
            Log.d(TAG, String.valueOf(element.getID()));
        }

        ArrayList<ButtonTimeToggle> buttonToggleList = new ArrayList<>();
        ArrayList<Integer> toggleStartTimestamps = new ArrayList<>();

        //saves each timestamped datapoint to the JSON
        for(UIElement element : inputStack) {
            if(element instanceof ButtonTimeToggle) {
                if(buttonToggleList.contains(element)) {
                    int index = buttonToggleList.indexOf(element);
                    manager.addDatapoint(buttonToggleList.remove(index).getID(),
                            String.valueOf(Math.abs(timestamps.pop()-toggleStartTimestamps.get(index))),
                            toggleStartTimestamps.remove(index));
                }
                else {
                    buttonToggleList.add((ButtonTimeToggle) element);
                    toggleStartTimestamps.add(timestamps.pop());
                }
            }
            else {
                manager.addDatapoint(element.getID(), element.getValue(), timestamps.pop());
            }
        }
        if(!buttonToggleList.isEmpty()) {
            int periodTimeLength = matchPhaseAuton ? autonLengthMs : teleopLengthMs;
            for(ButtonTimeToggle button : buttonToggleList) {
                int index = buttonToggleList.indexOf(button);
                manager.addDatapoint(button.getID(),
                        String.valueOf((periodTimeLength-toggleStartTimestamps.get(index))),
                        toggleStartTimestamps.get(index));
            }
        }

        //saves each non-timestamped datapoint to the JSON
        for(UIElement element : allElements.values()) {
            if(element.getIndependent()) {
                manager.addDatapoint(element.getID(), element.getValue());
            }
        }
        return manager.getJSON();
    }

    /**
     *
     */
    public void undo() {
        if(inputStack.isEmpty()) return;

        UIElement element = inputStack.pop();

        element.undo();
        redoStack.push(element);
        redoTimestamps.push(timestamps.pop());
        Toast.makeText(mainActivity, "Undid " + reversedDatapointIDs.get(element.getID()), Toast.LENGTH_SHORT).show();

    }

    /**
     *
     */
    public void redo() {
        if(redoStack.isEmpty()) return;

        UIElement element = redoStack.pop();

        element.redo();
        inputStack.push(element);
        timestamps.push(redoTimestamps.pop());
        Toast.makeText(mainActivity, "Redid " + reversedDatapointIDs.get(element.getID()), Toast.LENGTH_SHORT).show();

    }

    public void setMatchPhaseAuton() {
        matchPhaseAuton = true;
    }

    public void setMatchPhaseTeleop() {
        matchPhaseAuton = false;
    }

    public void disableScouting() {
        for(UIElement element : allElements.values()) {
            element.disable(false);
        }
        for(UIElement element : disableOnlyElements) {
            element.disable(false);
        }
    }

    public void disableAll() {
        for(UIElement element : allElements.values()) {
            element.disable(true);
        }
        for(UIElement element : disableOnlyElements) {
            element.disable(true);
        }
    }

    public void enableAll() {
        for(UIElement element : allElements.values()) {
            element.enable();
        }
        for(UIElement element : disableOnlyElements) {
            element.enable();
        }
    }
}
