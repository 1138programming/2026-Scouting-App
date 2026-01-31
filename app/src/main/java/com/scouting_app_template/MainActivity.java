package com.scouting_app_template;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_ADVERTISE;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.widget.Toast;

import com.scouting_app_template.Bluetooth.BluetoothConnectedThread;
import com.scouting_app_template.Extras.MatchTiming;
import com.scouting_app_template.Extras.PermissionManager;
import com.scouting_app_template.Fragments.ArchiveFragment;
import com.scouting_app_template.Fragments.AutonFragment;
import com.scouting_app_template.Fragments.DataFragment;
import com.scouting_app_template.Fragments.FragmentTransManager;
import com.scouting_app_template.Fragments.Popups.ArchiveConfirm;
import com.scouting_app_template.Fragments.Popups.BlockerFragment;
import com.scouting_app_template.Fragments.Popups.MenuFragment;
import com.scouting_app_template.Fragments.Popups.ResetFragment;
import com.scouting_app_template.Fragments.PostMatchFragment;
import com.scouting_app_template.Fragments.PreAutonFragment;
import com.scouting_app_template.Fragments.TeleopFragment;
import com.scouting_app_template.JSON.FileSaver;
import com.scouting_app_template.JSON.UpdateScoutingInfo;
import com.scouting_app_template.Fragments.Popups.AutonStart;
import com.scouting_app_template.Fragments.Popups.ConfirmSubmit;
import com.scouting_app_template.Fragments.Popups.TeleopStart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Team1138ScoutingApp";
    public static final UUID MY_UUID = UUID.fromString("0007EA11-1138-1000-5465-616D31313338");
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public BluetoothConnectedThread connectedThread;
    public static FragmentTransManager ftm;
    public ArrayList<Fragment> fragments = new ArrayList<>();
    public PreAutonFragment preAuton = new PreAutonFragment();
    public AutonFragment auton = new AutonFragment();
    public TeleopFragment teleop = new TeleopFragment();
    public BlockerFragment blockerFragment = new BlockerFragment();
    public AutonStart autonStart = new AutonStart();
    public TeleopStart teleopStart = new TeleopStart();
    public PostMatchFragment postMatch = new PostMatchFragment();
    public ConfirmSubmit confirmSubmit = new ConfirmSubmit();
    public ArchiveFragment archiveFragment = new ArchiveFragment();
    public ArchiveConfirm archiveConfirmSubmit = new ArchiveConfirm();
    public MenuFragment menuFragment = new MenuFragment();
    public ResetFragment resetFragment = new ResetFragment();
    public PermissionManager permissionManager = new PermissionManager(this);
    private enum gameState {
        preAuton,
        autonStarted,
        autonStopped,
        teleopStarted,
        postMatch
    }
    private gameState currentState = gameState.preAuton;
    public static final int autonLengthMs = 20000;
    public static final int teleopLengthMs = 140000;
    public static final int timeBufferMs = 3000;
    public static final String datapointEventValue = "";
    public static final int defaultTimestamp = 0;
    private boolean connectivity = false;

    /**
     * Updates the variable that tracks Bluetooth Connectivity
     */
    public void setConnectivity(boolean connectivity) {
        this.connectivity = connectivity;
        updateConnectivity();
    }

    /**
     * Called when GUI element needs to be updated. This is not a switch,
     * it looks at {@code connectivity} to make sure GUI element is accurate.
     */
    private void updateConnectivity() {
        runOnUiThread(() -> preAuton.setBtStatus(connectivity));
    }

    private void addFragmentsToManager() {
        fragments.add(preAuton);
        fragments.add(auton);
        fragments.add(teleop);
        fragments.add(blockerFragment);
        fragments.add(autonStart);
        fragments.add(teleopStart);
        fragments.add(postMatch);
        fragments.add(archiveFragment);
        fragments.add(archiveConfirmSubmit);
        fragments.add(confirmSubmit);
        fragments.add(resetFragment);
        fragments.add(menuFragment);

        ftm = new FragmentTransManager(fragments);
    }

    private void addPermissions() {
        permissionManager.addPermission(BLUETOOTH_CONNECT);
        permissionManager.addPermission(BLUETOOTH_SCAN);
        permissionManager.addPermission(ACCESS_FINE_LOCATION);
        permissionManager.addPermission(BLUETOOTH);
        permissionManager.addPermission(BLUETOOTH_ADMIN);
        permissionManager.addPermission(BLUETOOTH_ADVERTISE);
    }

    public void setConnectedThread(BluetoothConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }
    public void updateTabletInformation() {
        if(!connectivity) return;
        byte[] info = preAuton.getTabletInformation();
        connectedThread.sendInformation(info, 2);
    }
    public void updateBtScoutingInfo() {
        if (!Objects.isNull(connectedThread) && !connectedThread.checkLists()) {
            connectedThread.updateLists();
        }
        ArrayList<ArrayList<CharSequence>> splitData = (new UpdateScoutingInfo()).getSplitFileData();
        if (!splitData.isEmpty() && !splitData.get(0).isEmpty()) {
            preAuton.setScoutingInfo(splitData);
        }
    }
    public JSONObject getBaseJSON() throws JSONException {
        return preAuton.getBaseJSON();
    }
    public void recreateFragments() {
        fragments.clear();
        preAuton = new PreAutonFragment(preAuton.getScouterIndex(), preAuton.getMatchIndex()+1);
        fragments.add(preAuton);
        auton = new AutonFragment();
        fragments.add(auton);
        teleop = new TeleopFragment();
        fragments.add(teleop);
        blockerFragment = new BlockerFragment();
        fragments.add(blockerFragment);
        autonStart = new AutonStart();
        fragments.add(autonStart);
        teleopStart = new TeleopStart();
        fragments.add(teleopStart);
        postMatch = new PostMatchFragment();
        fragments.add(postMatch);
        confirmSubmit = new ConfirmSubmit();
        fragments.add(confirmSubmit);
        archiveFragment = new ArchiveFragment();
        fragments.add(archiveFragment);
        archiveConfirmSubmit = new ArchiveConfirm();
        fragments.add(archiveConfirmSubmit);
        menuFragment = new MenuFragment();
        fragments.add(menuFragment);
        resetFragment = new ResetFragment();
        fragments.add(resetFragment);

        ftm = new FragmentTransManager(fragments);
    }
    public void sendSavedData(File file) {
        if(connectivity) {
            try {
                connectedThread.sendInformation(Files.readAllBytes(file.toPath()), 1);
            } catch (IOException e) {
                Log.e(TAG, "failed to read from file to submit match", e);
            }
        }
        else {
            Toast.makeText(this,"not connected to Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }
    public void sendMatchData() {
        JSONObject jsonFile = new JSONObject();
        JSONArray jsonArray;
        JSONArray jsonCollection = new JSONArray();
        try {
            for (Fragment fragment : fragments) {
                if(fragment instanceof DataFragment) {
                    jsonArray = ((DataFragment)fragment).getFragmentMatchData();
                    Log.d(TAG, jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonCollection.put(jsonArray.getJSONObject(i));
                    }
                }
            }

            jsonFile.put("scoutingData",jsonCollection);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to compile match data from each fragment to send.\n" + e);
            return;
        }

        FileSaver.saveFile(jsonFile.toString(), preAuton.getFileTitle());

        if(connectivity) {
            connectedThread.sendInformation(jsonFile.toString().getBytes(StandardCharsets.UTF_8), 1);
        }
        else {
            Toast.makeText(this, "Data has not been uploaded because bluetooth isn't connected", Toast.LENGTH_LONG).show();
        }
    }

    public long getCurrStartTime() {
        switch(currentState) {
            case autonStarted:
                return auton.getAutonStart();
            case teleopStarted:
                return teleop.getTeleopStart();
            default:
                Log.e(TAG, "Tried to get time while game is not active.");
                return -1;
        }
    }

    public void updateFragments() {
        if((auton.isVisible() && currentState != gameState.autonStarted) ||
                (teleop.isVisible() && currentState != gameState.teleopStarted)) {
            runOnUiThread(ftm::showBlocker);
        }
        else {
            ftm.hideBlocker();
        }
    }

    public void autonStart() {
        currentState = gameState.autonStarted;
        MatchTiming.scheduleRunAfterAuto(this::autonStop);
    }

    public void autonStop() {
        currentState = gameState.autonStopped;
    }

    public void teleopStart() {
        currentState = gameState.teleopStarted;
        MatchTiming.scheduleRunAfterTeleop(this::teleopStop);
    }

    public void teleopStop() {
        currentState = gameState.postMatch;
    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        if(permissionManager.permissionNotGranted(BLUETOOTH_CONNECT)) {
            return "ERROR";
        }
        else {
            return ((BluetoothManager) MainActivity.context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getName();
        }
    }

    public void updateTeamNumber(int teamNumber) {
        auton.updateTeamNumber(teamNumber);
        teleop.updateTeamNumber(teamNumber);
        postMatch.updateTeamNumber(teamNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        addFragmentsToManager();

        addPermissions();
        permissionManager.requestPermissions();
    }
}