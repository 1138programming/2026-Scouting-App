package com.scouting_app_2026.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.UIElements.UndoStack;

import org.json.JSONArray;
import org.json.JSONException;

public class DataFragment extends Fragment {
    protected UndoStack undoStack;

    public DataFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        undoStack = new UndoStack((MainActivity)requireActivity());
    }

    public JSONArray getFragmentMatchData() throws JSONException {
        return undoStack.getTimestamps(((MainActivity)requireActivity()).getBaseJSON());

    }
}
