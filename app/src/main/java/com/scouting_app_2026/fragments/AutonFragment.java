package com.scouting_app_2026.fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.UIElements.ButtonTimeToggle;
import com.scouting_app_2026.UIElements.Checkbox;
import com.scouting_app_2026.UIElements.ImageButton;
import com.scouting_app_2026.UIElements.RadioGroup;
import com.scouting_app_2026.UIElements.Spinner;
import com.scouting_app_2026.databinding.AutonFragmentBinding;
import com.scouting_app_2026.datapointIDs.DatapointID;
import com.scouting_app_2026.datapointIDs.NonDataIDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AutonFragment extends DataFragment {
    private AutonFragmentBinding binding;
    private RadioGroup robotLocation;
    private Long autonStart;
    private boolean currentFlippedStatus = false;

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
        Spinner fuelScoredSpinner = new Spinner(DatapointID.autonNumScored.getID(), binding.fuelScoredAuton,false);
        fuelScoredSpinner.updateSpinnerList(new ArrayList<>(fuelScoredAuton), requireContext());
        fuelScoredSpinner.alwaysActive(true);
        undoStack.addElement(fuelScoredSpinner);

        robotLocation = new RadioGroup(
                new ArrayList<>(Arrays.asList(
                    DatapointID.autonEnteredBlue.getID(),
                    DatapointID.autonEnteredNeutral.getID(),
                    DatapointID.autonEnteredRed.getID())),
                binding.locationAuton,undoStack);

        new ButtonTimeToggle(DatapointID.autonCollected.getID(),
                binding.collectingButtonAuton, undoStack, requireActivity().getColorStateList(R.color.green_button_toggle));

        new ButtonTimeToggle(DatapointID.autonShuttled.getID(),
                binding.shuttlingButtonAuton, undoStack, requireActivity().getColorStateList(R.color.green_button_toggle));

        new ButtonTimeToggle(DatapointID.autonScored.getID(),
                binding.scoringButtonAuton, undoStack, requireActivity().getColorStateList(R.color.green_button_toggle));

        ButtonTimeToggle immobileButton = new ButtonTimeToggle(DatapointID.autonImmobile.getID(),
                binding.immobileButtonAuton, undoStack, requireActivity().getColorStateList(R.color.red_button_toggle));
        immobileButton.setOnSelectFunction(undoStack::disableScouting);
        immobileButton.setOnDeselectFunction(undoStack::enableAll);
        immobileButton.disableable(false);

        new ButtonTimeToggle(DatapointID.autonOutpost.getID(),
                binding.outpostButtonAuton, undoStack, requireActivity().getColorStateList(R.color.green_button_toggle));

        new ButtonTimeToggle(DatapointID.autonDepot.getID(),
                binding.depotButtonAuton, undoStack, requireActivity().getColorStateList(R.color.green_button_toggle));

        new Checkbox(DatapointID.autonHangAttempted.getID(), binding.hangAttemptedCheckbox, false, true, undoStack);

        new Checkbox(DatapointID.autonHangSuccessful.getID(), binding.hangSuccessfulCheckbox, false, true, undoStack);

        ImageButton undoButton = new ImageButton(NonDataIDs.AutonUndo.getID(), binding.undoButton);
        undoButton.setOnClickFunction(undoStack::undo);
        undoStack.addDisableOnlyElement(undoButton);
        undoButton.disableable(false);

        ImageButton redoButton = new ImageButton(NonDataIDs.AutonRedo.getID(), binding.redoButton);
        redoButton.setOnClickFunction(undoStack::redo);
        undoStack.addDisableOnlyElement(redoButton);
        redoButton.disableable(false);

        Button backButton = new Button(NonDataIDs.AutonBack.getID(), binding.backButton);
        backButton.setOnClickFunction(() -> ftm.autonBack());

        Button nextButton = new Button(NonDataIDs.AutonNext.getID(), binding.nextButton);
        nextButton.setOnClickFunction(() -> ftm.autonNext());
        nextButton.setOnClickFunction(() -> ((TeleopFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("TeleopFragment"))).openTeleop());

        undoStack.disableAll();
    }

    /**
     * Called every time auton is opened to make sure the auton start
     * popup is shown before auton starts.
     */
    public void openAuton() {
        if(autonStart == null) {
            ftm.showAutonStart();
        }
    }

    public void startAuton() {
        this.autonStart = Calendar.getInstance(Locale.US).getTimeInMillis();
        undoStack.enableAll();

        ((MainActivity)requireActivity()).autonStart();

        robotLocation.setSelectedByIndex(((MainActivity)requireActivity()).getAutonStartPos());
    }

    public void endAuton() {
        undoStack.disableAll();
    }

    public String getAutonPos() {
        return robotLocation.getValue();
    }

    public long getAutonStart() {
        return this.autonStart;
    }

    public void updateTeamNumber(int teamNumber) {
        binding.teamNumber.setText(String.valueOf(teamNumber));
    }

    public void flipField(boolean fieldFlipped) {
        if(fieldFlipped) {
            binding.imageView.setVisibility(INVISIBLE);
            binding.reversedImage.setVisibility(VISIBLE);
        }
        else {
            binding.imageView.setVisibility(VISIBLE);
            binding.reversedImage.setVisibility(INVISIBLE);
        }
        flipRadioButtons(fieldFlipped);
    }

    public void flipRadioButtons(boolean fieldFlipped) {
        ArrayList<android.widget.RadioButton> radioButtons = robotLocation.getButtons();

        if(currentFlippedStatus != fieldFlipped) {
            currentFlippedStatus = fieldFlipped;

            robotLocation.removeAllButton();
            for(int i = radioButtons.size()-1; i >= 0; i--) {
                robotLocation.addButton(radioButtons.get(i));
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "AutonFragment";
    }
}
