package com.scouting_app_2026.fragments;

import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.UIElements.ButtonTimeToggle;
import com.scouting_app_2026.UIElements.Checkbox;
import com.scouting_app_2026.UIElements.ImageButton;
import com.scouting_app_2026.UIElements.RadioGroup;
import com.scouting_app_2026.databinding.TeleopFragmentBinding;
import com.scouting_app_2026.datapointIDs.DatapointID;
import com.scouting_app_2026.datapointIDs.NonDataIDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class TeleopFragment extends DataFragment {
    private TeleopFragmentBinding binding;
    private Long teleopStart;

    public TeleopFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = TeleopFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        undoStack.setMatchPhaseTeleop();

        new RadioGroup(
                new ArrayList<>(Arrays.asList(
                        DatapointID.teleopEnteredRed.getID(),
                        DatapointID.teleopEnteredNeutral.getID(),
                        DatapointID.teleopEnteredBlue.getID())),
                binding.locationTeleop,undoStack);


        new ButtonTimeToggle(DatapointID.teleopCollected.getID(),
                binding.collectingButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new ButtonTimeToggle(DatapointID.teleopShuttled.getID(),
                binding.shuttlingButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new ButtonTimeToggle(DatapointID.teleopScored.getID(),
                binding.scoringButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new ButtonTimeToggle(DatapointID.teleopScored.getID(),
                binding.immobileButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new ButtonTimeToggle(DatapointID.teleopOutpost.getID(),
                binding.outpostButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new ButtonTimeToggle(DatapointID.teleopDefense.getID(),
                binding.defendingButtonTeleop, undoStack, requireActivity().getColor(R.color.dark_red));

        new Checkbox(DatapointID.teleopHangAttempted.getID(), binding.hangAttemptedCheckbox, false, true, undoStack);

        RadioGroup firstActiveHub = new RadioGroup(DatapointID.autonRedWin.getID(), binding.teamStartTeleop);
        undoStack.addElement(firstActiveHub);

        ImageButton undoButton = new ImageButton(NonDataIDs.TeleopUndo.getID(), binding.undoButton);
        undoButton.setOnClickFunction(undoStack::undo);

        ImageButton redoButton = new ImageButton(NonDataIDs.TeleopRedo.getID(), binding.redoButton);
        redoButton.setOnClickFunction(undoStack::redo);

        Button backButton = new Button(NonDataIDs.TeleopNext.getID(), binding.backButton);
        backButton.setOnClickFunction(() -> ftm.teleopBack());

        Button submitButton = new Button(NonDataIDs.TeleopBack.getID(), binding.nextButton);
        submitButton.setOnClickFunction(() -> ftm.teleopNext());
    }

    /**
     * Called every time teleop is opened to make sure the teleop start
     * popup is shown before teleop starts.
     */
    public void openTeleop() {
        if(teleopStart == null) {
            ftm.showTeleopStart();
            undoStack.disableAll();
        }
    }
    public void startTeleop() {
        this.teleopStart = Calendar.getInstance(Locale.US).getTimeInMillis();
        undoStack.enableAll();
    }

    public void endTeleop() {
        undoStack.disableAll();
    }
    public long getTeleopStart() {
        return this.teleopStart;
    }

    public void updateTeamNumber(int teamNumber) {
        binding.teamNumber.setText(String.valueOf(teamNumber));
    }

    @NonNull
    @Override
    public String toString() {
        return "TeleopFragment";
    }
}
