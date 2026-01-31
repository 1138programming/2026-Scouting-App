package com.scouting_app_template.Fragments;

import static com.scouting_app_template.MainActivity.context;
import static com.scouting_app_template.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_template.DatapointIDs.DatapointID;
import com.scouting_app_template.DatapointIDs.NonDataIDs;
import com.scouting_app_template.JSON.JSONManager;
import com.scouting_app_template.MainActivity;
import com.scouting_app_template.UIElements.Button;
import com.scouting_app_template.UIElements.SliderElement;
import com.scouting_app_template.databinding.PostMatchFragmentBinding;

import org.json.JSONArray;
import org.json.JSONException;


public class PostMatchFragment extends DataFragment {
    private PostMatchFragmentBinding binding;
    private SliderElement confidenceSlider;

    public PostMatchFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = PostMatchFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = new Button(NonDataIDs.PostMatchBack.getID(), binding.returnToTeleop);
        backButton.setOnClickFunction(() -> ftm.postMatchBack());

        Button submitButton = new Button(NonDataIDs.PostMatchSubmit.getID(), binding.submitButton);
        submitButton.setOnClickFunction(() -> ftm.matchSubmit());

        confidenceSlider = new SliderElement(DatapointID.scouterConfidence.getID(), binding.confidenceSlider);
    }

    public void updateTeamNumber(int teamNumber) {
        binding.teamNumber.setText(String.valueOf(teamNumber));
    }

    @NonNull
    @Override
    public String toString() {
        return "PostMatchFragment";
    }

    @Override
    public JSONArray getFragmentMatchData() throws JSONException {
        JSONManager jsonManager = new JSONManager(((MainActivity)context).getBaseJSON());
        JSONArray jsonCollection = super.getFragmentMatchData();

        jsonManager.addDatapoint(DatapointID.scouterConfidence.getID(), confidenceSlider.getValue());

        JSONArray jsonArray = jsonManager.getJSON();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonCollection.put(jsonArray.getJSONObject(i));
        }

        return jsonCollection;
    }
}
