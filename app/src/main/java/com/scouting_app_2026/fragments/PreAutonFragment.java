package com.scouting_app_2026.fragments;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.ftm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.scouting_app_2026.JSON.TemplateContext;
import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.UIElements.Checkbox;
import com.scouting_app_2026.UIElements.ImageButton;
import com.scouting_app_2026.UIElements.RadioCheckboxGroup;
import com.scouting_app_2026.UIElements.RadioGroup;
import com.scouting_app_2026.UIElements.Spinner;
import com.scouting_app_2026.databinding.PreAutonFragmentBinding;
import com.scouting_app_2026.datapointIDs.DatapointID;
import com.scouting_app_2026.datapointIDs.NonDataIDs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreAutonFragment extends DataFragment {
    private PreAutonFragmentBinding binding;
    private Spinner scouterNameSpinner;
    private Spinner matchNumberSpinner;
    private RadioGroup teamColorButtons;
    private Spinner teamNumberSpinner;
    private int scouterIndex;
    private int matchIndex;
    private boolean successfulDeviceNameParse = false;
    private int selectedColor;
    private int driverStationNumber;
    private int qualNum = 100;
    private int playoffNum = 13;
    private int finalsNum = 3;
    private ArrayList<Integer> scouterIDs = new ArrayList<>(List.of(-1));
    private boolean currentFlippedStatus = false;

    public PreAutonFragment() {
        this.scouterIndex = 0;
        this.matchIndex = 0;
    }
    public PreAutonFragment(int scouterIndex, int matchIndex) {
        this.scouterIndex = scouterIndex;
        this.matchIndex = matchIndex;
    }

    /* When the fragment binding is created we override the function so we
    * can get the binding in this class to use. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = PreAutonFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scouterNameSpinner = new Spinner(NonDataIDs.ScouterName.getID(), binding.nameOfScouterSpinner, true);
        scouterNameSpinner.setOnClickFunction(() -> ((MainActivity) requireContext()).updateTabletInformation());

        matchNumberSpinner = new Spinner(NonDataIDs.MatchNumber.getID(), binding.matchNumberSpinner, false);
        matchNumberSpinner.setOnClickFunction(() -> ((MainActivity) requireContext()).updateTabletInformation());
        updateMatches();

        teamColorButtons = new RadioGroup(NonDataIDs.TeamColor.getID(), binding.teamColorSwitch);
        teamColorButtons.setOnClickFunction(this::updateTeamColor);
        teamColorButtons.setOnClickFunction(() -> ((MainActivity) requireContext()).updateTabletInformation());

        teamNumberSpinner = new Spinner(NonDataIDs.TeamNumber.getID(), binding.teamNumberSpinner, false);
        teamNumberSpinner.setOnClickFunction(() -> ((MainActivity) requireContext()).updateTabletInformation());
        teamNumberSpinner.setOnClickFunction(() -> ((MainActivity) requireContext()).updateTeamNumber(Integer.parseInt(binding.teamNumberSpinner.getSelectedItem().toString())));

        RadioCheckboxGroup startingPositionGroup = new RadioCheckboxGroup(DatapointID.startPos.getID());

        RadioGroup startingPosition = new RadioGroup(NonDataIDs.StartPosRadio.getID(), binding.startingLocation);
            startingPositionGroup.addElement(startingPosition);

        Checkbox noShowCheckbox = new Checkbox(NonDataIDs.NoShow.getID(), binding.noShowCheckbox, true,true, "noShow");
            startingPositionGroup.addElement(noShowCheckbox);

            startingPositionGroup.elementSelected(noShowCheckbox);

            undoStack.addElement(startingPositionGroup);

        Button nextButton = new Button(NonDataIDs.PreAutonNext.getID(), binding.nextButton);
        nextButton.setOnClickFunction(() -> {
            boolean noShow = noShowCheckbox.isChecked();
            if(noShow) {
                ftm.preAutonPost();
            }
            else {
                ftm.preAutonNext();
                ((AutonFragment) Objects.requireNonNull(
                        getParentFragmentManager().findFragmentByTag("AutonFragment"))).openAuton();
            }
            ((PostMatchFragment) Objects.requireNonNull(
                    getParentFragmentManager().findFragmentByTag("PostMatchFragment"))).setNoShow(noShow);
        });

        ImageButton button = new ImageButton(NonDataIDs.ArchiveHamburger.getID(), binding.archiveButton);
        button.setOnClickFunction(() -> ftm.preAutonMenu());

        updateTeamColor();
    }

    /* When the fragment is completely created, we test so see
    * if we are connected and if so we send our basic info. */
    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).updateBtScoutingInfo();
        attemptDeviceNameParse();

        if(scouterIndex < scouterNameSpinner.getLength()) {
            scouterNameSpinner.setIndex(scouterIndex);
        }
        scouterNameSpinner.setOnClickFunction(this::updateScouterIndex);

        if(matchIndex < matchNumberSpinner.getLength()) {
            matchNumberSpinner.setIndex(matchIndex);
        }
        matchNumberSpinner.setOnClickFunction(this::updateMatchIndex);
    }

    /* Makes it so the toString() function for this class
    * return the name of the class. */
    @NonNull
    @Override
    public String toString() {
        return "PreAutonFragment";
    }

    /** Generates an ArrayList filled with the necessary qualifying
     * and playoffs matches for an entire comp. */
    private ArrayList<CharSequence> generateMatches(int qualNumber, int playoffsNumber, int finalsNumber, int replayLevel) {
        ArrayList<CharSequence> matchNumbers = new ArrayList<>();
        StringBuilder temp;
        //creates spinner for match number
        for(int i = 1; i<=qualNumber; i++) {
            matchNumbers.add(addReplays("Q", i, replayLevel));
        }
        for(int i = 1; i<=playoffsNumber; i++) {
            matchNumbers.add(addReplays("P", i, replayLevel));
        }
        for(int i = 1; i<=finalsNumber; i++) {
            matchNumbers.add(addReplays("F", i, replayLevel));
        }
        return matchNumbers;
    }

    private String addReplays(String prefix, int matchNumber, int replayLevel) {
        StringBuilder temp = new StringBuilder(prefix + matchNumber);
        for(int j = 0; j < replayLevel; j++) {
            temp.append("r");
        }
        return temp.toString();
    }

    public void updateTeamColor() {
        Context context = requireContext();

        switch(teamColorButtons.getValue()) {
            case "RED":
                binding.startingPosImage.setImageResource(R.drawable.redside);

                binding.leftStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_red));
                binding.leftStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.red_text_toggle));

                binding.middleLeftStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_red));
                binding.middleLeftStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.red_text_toggle));

                binding.rightStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_red));
                binding.rightStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.red_text_toggle));

                binding.robotDriverStation.setText(R.string.red_general_position);

                selectedColor = currentFlippedStatus ? 1 : 0;
                break;
            case "BLUE":
                binding.startingPosImage.setImageResource(R.drawable.blueside);

                binding.leftStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_blue));
                binding.leftStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.blue_text_toggle));

                binding.middleLeftStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_blue));
                binding.middleLeftStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.blue_text_toggle));

                binding.rightStart.setBackground(AppCompatResources.getDrawable(context,R.drawable.start_toggle_blue));
                binding.rightStart.setTextColor(AppCompatResources.getColorStateList(context, R.color.blue_text_toggle));

                binding.robotDriverStation.setText(R.string.blue_general_position);

                selectedColor = currentFlippedStatus ? 0 : 1;
                break;
        }
    }

    public byte[] getTabletInformation() {
        StringBuilder tabletInfo = new StringBuilder();

        String driverStationPos;
        if (selectedColor == 0) {
            driverStationPos = (currentFlippedStatus) ? "Red" : "Blue";
        }
        else {
            driverStationPos = (!currentFlippedStatus) ? "Red" : "Blue";
        }
        if(driverStationNumber >= 0 && driverStationNumber <= 3) {
            driverStationPos = driverStationPos + " " + driverStationNumber;
        }

        tabletInfo.append(scouterNameSpinner.getValue());
        tabletInfo.append(" : ");
        tabletInfo.append(driverStationPos);
        tabletInfo.append(" : ");
        tabletInfo.append(matchNumberSpinner.getValue());
        tabletInfo.append(" : ");
        tabletInfo.append(teamNumberSpinner.getValue());
        tabletInfo.append(" : ");
        tabletInfo.append(((MainActivity)requireActivity()).getCurrentState());

        if(tabletInfo.length() > 2) {
            return tabletInfo.toString().getBytes();
        }
        else {
            return "Error".getBytes();
        }
    }

    public void setScoutingInfo(ArrayList<ArrayList<CharSequence>> list) {
        this.scouterIDs = new ArrayList<>(List.of(this.scouterIDs.get(0)));
        for (CharSequence scouterNum : list.get(1)) {
            String curr = scouterNum.toString();
            this.scouterIDs.add(Integer.valueOf(curr));
        }

        scouterNameSpinner.updateSpinnerList(list.get(0), requireContext());
        scouterNameSpinner.setIndex(scouterIndex);

        teamNumberSpinner.updateSpinnerList(list.get(2), requireContext());

        ArrayList<Integer> matches = new ArrayList<>();
        for(CharSequence i : list.get(3)) {
            matches.add(Integer.valueOf(i.toString()));
        }
        updateMatches(matches.get(0), matches.get(1), matches.get(2));
    }

    public void updateMatches() {
        int replayLevel = ((MainActivity)requireActivity()).getReplayLevel();
        int currIndex = matchNumberSpinner.getSelectedIndex();
        matchNumberSpinner.updateSpinnerList(generateMatches(qualNum, playoffNum, finalsNum, replayLevel), requireContext());
        matchNumberSpinner.setIndex(currIndex);
    }

    private void updateMatches(int quals, int playoffs, int finals) {
        int replayLevel = ((MainActivity)requireActivity()).getReplayLevel();

        this.qualNum = quals;
        this.playoffNum = playoffs;
        this.finalsNum = finals;

        matchNumberSpinner.updateSpinnerList(generateMatches(quals, playoffs, finals, replayLevel), requireContext());
    }

    public void setBtStatus(boolean status) {
        binding.btConnectionStatus.setText(
                getResources().getString(status ? R.string.connected_status_title : R.string.disconnected_status_title),
                TextView.BufferType.NORMAL);
    }

    public String getFileTitle() {
        return scouterNameSpinner.getValue() + " Match #"+matchNumberSpinner.getValue() + " Team #" + teamNumberSpinner.getValue();
    }

    public void updateTemplateContext() {
        TemplateContext context = TemplateContext.getInstance();

        int scouterNameIndex = scouterNameSpinner.getSelectedIndex();
        if(scouterNameIndex >= 0) {
            context.setScouterID(scouterIDs.get(scouterNameIndex));
        }
        else {
            context.setScouterID(-1);
        }

        context.setMatchID(getMatch());
        String teamNumber = teamNumberSpinner.getValue();
        if(!teamNumber.isEmpty()) {
            context.setTeamID(Integer.parseInt(teamNumber));
        }

        if(successfulDeviceNameParse) {
            /*
                selectedColor = 0 -> red
                selectedColor = 1 -> blue

                allianceID = 1 -> red 1
                allianceID = 2 -> red 2
                allianceID = 3 -> red 3
                allianceID = 4 -> blue 1
                allianceID = 5 -> blue 2
                allianceID = 6 -> blue 3
                allianceID = 7 -> red (generic)
                allianceID = 8 -> blue (generic)
             */

            int allianceID = (selectedColor == 0) ? driverStationNumber : driverStationNumber + 3;
            context.setAllianceID(allianceID);
        }
        else {

            String allianceName = teamColorButtons.getValue();
            switch (allianceName) {
                case "RED":
                    context.setAllianceID(7);
                    break;
                case "BLUE":
                    context.setAllianceID(8);
            }
        }
    }

    private void attemptDeviceNameParse() {
        successfulDeviceNameParse = true;
        String deviceName = ((MainActivity)requireActivity()).getDeviceName();
        Log.d(TAG, deviceName);
        String[] temp = deviceName.split(" ");
        if(temp.length >= 2) try {
            driverStationNumber = Integer.parseInt(temp[temp.length-1]);

            if(driverStationNumber > 3 || driverStationNumber < 1) {
                throw new NumberFormatException("Drive station number is outside range");
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Unable to parse device name: ", e);
            successfulDeviceNameParse = false;
            driverStationNumber = -1;
        }
        else {
            successfulDeviceNameParse = false;
        }

        if (successfulDeviceNameParse) switch(temp[temp.length-2]) {
            case "Red":
                selectedColor = currentFlippedStatus ? 0 : 1;
                lockColor();
                break;
            case "Blue":
                selectedColor = currentFlippedStatus ? 1 : 0;
                lockColor();
                break;
            default:
                successfulDeviceNameParse = false;
        }

        if(successfulDeviceNameParse) {
            Log.d(TAG, "success!");
            String colorStatus = temp[temp.length-2] + " " + driverStationNumber;
            requireActivity().runOnUiThread(() -> binding.robotDriverStation.setText(colorStatus));
        }
    }

    private void lockColor() {
        ((RadioButton)binding.teamColorSwitch.getChildAt(selectedColor)).setChecked(true);
        binding.teamColorSwitch.getChildAt(0).setEnabled(false);
        binding.teamColorSwitch.getChildAt(1).setEnabled(false);
    }

    private void updateScouterIndex() {
        this.scouterIndex = scouterNameSpinner.getSelectedIndex();
    }

    private void updateMatchIndex() {
        this.matchIndex = matchNumberSpinner.getSelectedIndex();
    }

    public String getPos() {
        return teamColorButtons.getValue();
    }

    public void flipField(boolean fieldFlipped) {

        if(currentFlippedStatus != fieldFlipped) {
            currentFlippedStatus = fieldFlipped;

            ArrayList<android.widget.RadioButton> colorButtons = teamColorButtons.getButtons();
            int id = teamColorButtons.getSelected();
            teamColorButtons.unselect();

            teamColorButtons.removeAllButton();
            for(int i = colorButtons.size()-1; i >= 0; i--) {
                teamColorButtons.addButton(colorButtons.get(i));
            }
            teamColorButtons.setSelected(id == 0 ? 1 : 0);
        }
    }

    public int getScouterIndex() {
        return scouterIndex;
    }

    public int getMatchIndex() {
        return matchIndex;
    }
    public void decrementMatchIndex() {
        matchIndex--;
    }

    public boolean isNoShow() {
        return binding.noShowCheckbox.isChecked();
    }
    private String getMatch() {
        String matchValue = matchNumberSpinner.getValue();

        return matchValue.toLowerCase();
    }
}
