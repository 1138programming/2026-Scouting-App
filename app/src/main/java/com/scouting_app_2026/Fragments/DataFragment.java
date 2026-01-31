package com.scouting_app_2026.Fragments;

import androidx.fragment.app.Fragment;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.UIElements.UndoStack;

import org.json.JSONArray;
import org.json.JSONException;

public class DataFragment extends Fragment {
    protected UndoStack undoStack;

    public DataFragment() {
        undoStack = new UndoStack();
    }

    public JSONArray getFragmentMatchData() throws JSONException {
        return undoStack.getTimestamps(((MainActivity) MainActivity.context).getBaseJSON());

    }
}
