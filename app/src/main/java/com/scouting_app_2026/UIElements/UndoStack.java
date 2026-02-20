package com.scouting_app_2026.UIElements;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.autonLengthMs;
import static com.scouting_app_2026.MainActivity.teleopLengthMs;

import android.util.Log;

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
    private final Stack<MatchTransaction<? extends UIElement>> inputStack = new Stack<>();
    private Stack<MatchTransaction<? extends UIElement>> redoStack = new Stack<>();
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

    public void addTimestamp(UIElement element, boolean stopping) {
        if(!allElements.containsKey(element.getID())) {
            Log.e(TAG, "Element not added to undoStack", new Throwable().fillInStackTrace());
            addElement(element);
        }

        int timestamp = (int)((Calendar.getInstance(Locale.US).getTimeInMillis() - (mainActivity.getCurrStartTime())));
        inputStack.add(new MatchTransaction<>(element, timestamp, stopping));

        redoStack = new Stack<>();
    }

    public JSONArray getTimestamps() {
        JSONManager manager = new JSONManager();

//        for(MatchTransaction<? extends UIElement> transaction : inputStack) {
//            Log.d(TAG, String.valueOf(transaction.getDatapointID()));
//        }

        HashMap<UIElement, MatchTransaction<? extends UIElement>> openToggles = new HashMap<>();

        //saves each timestamped datapoint to the JSON
        for(MatchTransaction<? extends UIElement> currTransaction : inputStack) {
            UIElement currElement = currTransaction.getElement();

            if(currElement instanceof ButtonTimeToggle) {
                //check if a certain toggle is currently open
                if(openToggles.containsKey(currElement)) {
                    //find and remove the start transaction
                    MatchTransaction<? extends UIElement> startTransaction = openToggles.remove(currElement);

                    assert startTransaction != null;
                    int duration = currTransaction.getTimestamp() - startTransaction.getTimestamp();

                    manager.addDatapoint(
                            currTransaction.getDatapointID(),
                            String.valueOf(duration),
                            startTransaction.getTimestamp());
                }
                else {
                    openToggles.put(currElement, currTransaction);
                }
            }
            else {
                manager.addDatapoint(currTransaction.getDatapointID(), currElement.getValue(), currTransaction.getTimestamp());
            }
        }
        if(!openToggles.isEmpty()) {
            int periodTimeLength = matchPhaseAuton ? autonLengthMs : teleopLengthMs;
            for(MatchTransaction<? extends UIElement> remaining : openToggles.values()) {
                manager.addDatapoint(
                        remaining.getDatapointID(),
                        String.valueOf((periodTimeLength-remaining.getTimestamp())),
                        remaining.getTimestamp());
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

        MatchTransaction<? extends UIElement> transaction = inputStack.pop();

        if(!transaction.undo()) {
            this.undo();
        }
        redoStack.push(transaction);

//        Toast.makeText(mainActivity, "Undid " + reversedDatapointIDs.get(transaction.getDatapointID()), Toast.LENGTH_SHORT).show();
    }


    /**
     *
     */
    public void redo() {
        if(redoStack.isEmpty()) return;

        MatchTransaction<? extends UIElement> transaction = redoStack.pop();

        if(!transaction.redo()) {
            this.redo();
        }
        inputStack.push(transaction);

//        Toast.makeText(mainActivity, "Redid " + reversedDatapointIDs.get(transaction.getDatapointID()), Toast.LENGTH_SHORT).show();
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

    private boolean arrayContains(ArrayList<MatchTransaction<? extends UIElement>> transactions, UIElement element) {
        return indexOf(transactions, element) != -1;
    }

    private int indexOf(ArrayList<MatchTransaction<? extends UIElement>> transactions, UIElement element) {
        for(int i = 0; i < transactions.size(); i++) {
            if(transactions.get(i).getElement() == element) {
                return i;
            }
        }
        return -1;
    }
}
