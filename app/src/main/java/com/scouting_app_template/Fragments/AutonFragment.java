package com.scouting_app_template.Fragments;

import static com.scouting_app_template.MainActivity.context;
import static com.scouting_app_template.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_template.DatapointIDs.NonDataIDs;
import com.scouting_app_template.R;
import com.scouting_app_template.UIElements.Button;
import com.scouting_app_template.UIElements.ButtonTimeToggle;
import com.scouting_app_template.UIElements.ImageButton;
import com.scouting_app_template.databinding.AutonFragmentBinding;

import java.util.Calendar;
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

        ButtonTimeToggle testButton = new ButtonTimeToggle(NonDataIDs.Default.getID(),
                binding.testButton, undoStack, context.getColor(R.color.dark_red));

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
