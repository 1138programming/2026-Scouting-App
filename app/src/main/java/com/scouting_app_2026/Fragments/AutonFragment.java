package com.scouting_app_2026.Fragments;

import static com.scouting_app_2026.MainActivity.context;
import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.DatapointIDs.DatapointID;
import com.scouting_app_2026.DatapointIDs.NonDataIDs;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.UIElements.ButtonTimeToggle;
import com.scouting_app_2026.UIElements.ImageButton;
import com.scouting_app_2026.UIElements.Spinner;
import com.scouting_app_2026.databinding.AutonFragmentBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AutonFragment extends DataFragment {
    AutonFragmentBinding binding;
    private Long autonStart;

    public AutonFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = AutonFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        undoStack.setMatchPhaseAuton();

        List<CharSequence> fuelScoredAuton = Arrays.asList(requireActivity().getResources().getStringArray(R.array.fuel_scored_auton));
        Spinner fuelScoredSpinner = new Spinner(0, binding.fuelScoredAuton,false);
        fuelScoredSpinner.updateSpinnerList(new ArrayList<>(fuelScoredAuton));


        ButtonTimeToggle collectingButtonAuton = new ButtonTimeToggle(DatapointID.autonCollected.getID(),
                binding.collectingButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));

        ButtonTimeToggle shuttlingButtonAuton = new ButtonTimeToggle(DatapointID.autonShuttled.getID(),
                binding.shuttlingButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));

        ButtonTimeToggle scoringButtonAuton = new ButtonTimeToggle(DatapointID.autonScored.getID(),
                binding.scoringButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));

        ButtonTimeToggle immobileButtonAuton = new ButtonTimeToggle(DatapointID.autonImmobile.getID(),
                binding.immobileButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));

        ButtonTimeToggle outpostButtonAuton = new ButtonTimeToggle(DatapointID.autonOutpost.getID(),
                binding.outpostButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));

        ButtonTimeToggle depotButtonAuton = new ButtonTimeToggle(DatapointID.autonDepot.getID(),
                binding.depotButtonAuton, undoStack, requireActivity().getColor(R.color.dark_red));


        ImageButton undoButton = new ImageButton(NonDataIDs.AutonUndo.getID(), binding.undoButton);
        undoButton.setOnClickFunction(undoButton::undo);

        ImageButton redoButton = new ImageButton(NonDataIDs.AutonRedo.getID(), binding.redoButton);
        redoButton.setOnClickFunction(undoButton::redo);

        Button backButton = new Button(NonDataIDs.AutonBack.getID(), binding.backButton);
        backButton.setOnClickFunction(() -> ftm.autonBack());

        Button nextButton = new Button(NonDataIDs.AutonNext.getID(), binding.nextButton);
        nextButton.setOnClickFunction(() -> ftm.autonNext());
        nextButton.setOnClickFunction(() -> ((TeleopFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("TeleopFragment"))).teleopOpen());
    }

    public void autonOpen() {
        if(autonStart == null) {
            ftm.showAutonStart();
        }
    }

    /**
     * Called every time auton is opened to make sure the auton start
     * popup is shown before auton starts.
     */
    public void startAuton() {
        this.autonStart = Calendar.getInstance(Locale.US).getTimeInMillis();
    }

    public long getAutonStart() {
        return this.autonStart;
    }

    public void updateTeamNumber(int teamNumber) {
        binding.teamNumber.setText(String.valueOf(teamNumber));
    }

    @NonNull
    @Override
    public String toString() {
        return "AutonFragment";
    }
}
