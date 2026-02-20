package com.scouting_app_2026.fragments;

import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.JSON.JSONManager;
import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.UIElements.SliderElement;
import com.scouting_app_2026.UIElements.Spinner;
import com.scouting_app_2026.databinding.PostMatchFragmentBinding;
import com.scouting_app_2026.datapointIDs.DatapointID;
import com.scouting_app_2026.datapointIDs.NonDataIDs;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostMatchFragment extends DataFragment {
    private PostMatchFragmentBinding binding;

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

        List<CharSequence> hangOptionsPost = Arrays.asList(requireActivity().getResources().getStringArray(R.array.hang_spinner_options));
        Spinner fuelScoredSpinner = new Spinner(DatapointID.teleopHangSuccessful.getID(), binding.hangSpinner,false);
        fuelScoredSpinner.updateSpinnerList(new ArrayList<>(hangOptionsPost), requireContext());
        undoStack.addElement(fuelScoredSpinner);

        List<CharSequence> scoreEstimatePost = Arrays.asList(requireActivity().getResources().getStringArray(R.array.score_estimate_array));
        Spinner scoreEstimateSpinner = new Spinner(DatapointID.teleopNumScored.getID(), binding.scoreEstimate,false);
        scoreEstimateSpinner.updateSpinnerList(new ArrayList<>(scoreEstimatePost), requireContext());
        undoStack.addElement(scoreEstimateSpinner);

        new SliderElement(DatapointID.teleopScoreAccuracy.getID(), binding.accuracySlider, undoStack);

        new SliderElement(DatapointID.scouterConfidence.getID(), binding.confidenceSlider, undoStack);

        Button backButton = new Button(NonDataIDs.PostMatchBack.getID(), binding.returnToTeleop);
        backButton.setOnClickFunction(() -> ftm.postMatchBack());

        Button submitButton = new Button(NonDataIDs.PostMatchSubmit.getID(), binding.submitButton);
        submitButton.setOnClickFunction(() -> ftm.matchSubmit());

        new SliderElement(DatapointID.scouterConfidence.getID(), binding.confidenceSlider, undoStack);
    }

    public void updateTeamNumber(int teamNumber) {
        binding.teamNumber.setText(String.valueOf(teamNumber));
    }

    @NonNull
    @Override
    public String toString() {
        return "PostMatchFragment";
    }
}
