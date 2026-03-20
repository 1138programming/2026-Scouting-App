package com.scouting_app_2026;

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
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.JSON.FileSaver;
import com.scouting_app_2026.JSON.TemplateContext;
import com.scouting_app_2026.JSON.UpdateScoutingInfo;
import com.scouting_app_2026.bluetooth.BluetoothConnectedThread;
import com.scouting_app_2026.bluetooth.BluetoothSDPThread;
import com.scouting_app_2026.extras.MatchTiming;
import com.scouting_app_2026.extras.PermissionManager;
import com.scouting_app_2026.fragments.AdminFragment;
import com.scouting_app_2026.fragments.ArchiveFragment;
import com.scouting_app_2026.fragments.AutonFragment;
import com.scouting_app_2026.fragments.DataFragment;
import com.scouting_app_2026.fragments.FragmentTransManager;
import com.scouting_app_2026.fragments.PostMatchFragment;
import com.scouting_app_2026.fragments.PreAutonFragment;
import com.scouting_app_2026.fragments.QrCodeFragment;
import com.scouting_app_2026.fragments.SettingsFragment;
import com.scouting_app_2026.fragments.TeleopFragment;
import com.scouting_app_2026.fragments.popups.ArchiveConfirm;
import com.scouting_app_2026.fragments.popups.AutonStart;
import com.scouting_app_2026.fragments.popups.ConfirmSubmit;
import com.scouting_app_2026.fragments.popups.MenuFragment;
import com.scouting_app_2026.fragments.popups.PracticeConfirm;
import com.scouting_app_2026.fragments.popups.ReplayConfirm;
import com.scouting_app_2026.fragments.popups.ResetFragment;
import com.scouting_app_2026.fragments.popups.ConfirmSubmitAll;
import com.scouting_app_2026.fragments.popups.TeleopStart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Team1138ScoutingApp";
    public static final UUID MY_UUID = UUID.fromString("0007EA11-1138-1000-5465-616D31313338");
    @SuppressLint("StaticFieldLeak")
    public BluetoothConnectedThread connectedThread;
    public static FragmentTransManager ftm;
    public final ArrayList<Fragment> fragments = new ArrayList<>();
    public PreAutonFragment preAuton = new PreAutonFragment();
    public AutonFragment auton = new AutonFragment();
    public TeleopFragment teleop = new TeleopFragment();
    public AutonStart autonStart = new AutonStart();
    public TeleopStart teleopStart = new TeleopStart();
    public PostMatchFragment postMatch = new PostMatchFragment();
    public ConfirmSubmit confirmSubmit = new ConfirmSubmit();
    public ArchiveFragment archiveFragment = new ArchiveFragment();
    public ArchiveConfirm archiveConfirmSubmit = new ArchiveConfirm();
    public ConfirmSubmitAll confirmSubmitAll = new ConfirmSubmitAll();
    public MenuFragment menuFragment = new MenuFragment();
    public ResetFragment resetFragment = new ResetFragment();
    public PracticeConfirm practiceConfirm = new PracticeConfirm();
    public ReplayConfirm replayConfirm = new ReplayConfirm();
    public QrCodeFragment qrCodeFragment = new QrCodeFragment();
    public SettingsFragment settingsFragment = new SettingsFragment();
    public AdminFragment adminFragment = new AdminFragment();
    public final PermissionManager permissionManager = new PermissionManager(this);
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
    private final AtomicBoolean connectivity = new AtomicBoolean(false);
    private boolean practice = false;
    private int replayLevel = 0;
    private String qrCodeContents = "";

    /**
     * Updates the variable that tracks Bluetooth Connectivity
     */
    public void setConnectivity(boolean connectivity) {
        this.connectivity.set(connectivity);
        updateConnectivity();
    }

    public boolean getConnectivity() {
        return this.connectivity.get();
    }

    /**
     * Called when GUI element needs to be updated. This is not a switch,
     * it looks at {@code connectivity} to make sure GUI element is accurate.
     */
    private void updateConnectivity() {
        runOnUiThread(() -> preAuton.setBtStatus(connectivity.get()));

        if(!connectivity.get()) {
            clearQrCode();
            connectedThread = null;
        }
        qrCodeFragment.updateQrCode();
    }

    private void addFragmentsToManager() {
        fragments.add(preAuton);
        fragments.add(auton);
        fragments.add(teleop);
        fragments.add(autonStart);
        fragments.add(teleopStart);
        fragments.add(postMatch);
        fragments.add(archiveFragment);
        fragments.add(archiveConfirmSubmit);
        fragments.add(confirmSubmit);
        fragments.add(resetFragment);
        fragments.add(menuFragment);
        fragments.add(practiceConfirm);
        fragments.add(confirmSubmitAll);
        fragments.add(replayConfirm);
        fragments.add(qrCodeFragment);
        fragments.add(settingsFragment);
        fragments.add(adminFragment);

        ftm = new FragmentTransManager(fragments, this);
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
        preAuton.updateTemplateContext();
        if(!connectivity.get()) return;
        byte[] info = preAuton.getTabletInformation();
        connectedThread.sendTabletInfoRequest(info);
    }
    public void updateBtScoutingInfo() {
        if (connectedThread != null) {
            connectedThread.runIfConnected(this::updateListsRunnable);
        }
        else {
            updateLists();
        }
    }
    private void updateListsRunnable(boolean connectivity) {
        if(connectivity) {
            connectedThread.checkListsRequest();
        }
        else {
            updateLists();
        }
    }
    public void updateLists() {
        ArrayList<ArrayList<CharSequence>> splitData = (new UpdateScoutingInfo(this)).getSplitFileData();
        if (!splitData.isEmpty() && !splitData.get(0).isEmpty()) {
            preAuton.setScoutingInfo(splitData);
            TemplateContext.getInstance().setCompID((String)splitData.get(4).get(0));
        }
    }

    public void recreateFragments() {
        currentState = gameState.preAuton;
        MatchTiming.cancel();
        practice = false;
        replayLevel = 0;

        fragments.clear();
        qrCodeFragment = new QrCodeFragment();
        fragments.add(qrCodeFragment);
        preAuton = new PreAutonFragment(preAuton.getScouterIndex(), preAuton.getMatchIndex()+1);
        fragments.add(preAuton);
        auton = new AutonFragment();
        fragments.add(auton);
        teleop = new TeleopFragment();
        fragments.add(teleop);
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
        confirmSubmitAll = new ConfirmSubmitAll();
        fragments.add(confirmSubmitAll);
        menuFragment = new MenuFragment();
        fragments.add(menuFragment);
        resetFragment = new ResetFragment();
        fragments.add(resetFragment);
        practiceConfirm = new PracticeConfirm();
        fragments.add(practiceConfirm);
        replayConfirm = new ReplayConfirm();
        fragments.add(replayConfirm);
        settingsFragment = new SettingsFragment();
        fragments.add(settingsFragment);
        adminFragment = new AdminFragment();
        fragments.add(adminFragment);

        ftm = new FragmentTransManager(fragments, this);
        updateTabletInformation();
        updateConnectivity();
    }
    public void sendSavedData(File file) {
        if(connectivity.get()) {
            connectedThread.runIfConnected(connected -> {
                if(connected) {
                    try {
                        connectedThread.sendMatchRequest(Files.readAllBytes(file.toPath()));
                    } catch (IOException e) {
                        Log.e(TAG, "failed to read from file to submit match", e);
                    }
                }
                else {
                    if(BluetoothSDPThread.bluetoothConnect(this, false)) {
                        while(!connectivity.get()) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                        try {
                            connectedThread.sendMatchRequest(Files.readAllBytes(file.toPath()));
                        } catch (IOException e) {
                            Log.e(TAG, "failed to read from file to submit match", e);
                        }
                    }
                }
            });
        }
        else {
            if(BluetoothSDPThread.bluetoothConnect(this, false)) {
                while(!connectivity.get()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                sendSavedData(file);
            }
            else {
                Toast.makeText(this,"Not connected to Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendMatchData() {
        if(practice) return;

        JSONObject jsonFile = new JSONObject();
        JSONArray jsonArray;
        JSONArray jsonCollection = new JSONArray();
        try {
            if (preAuton.isNoShow()) {
                jsonCollection = preAuton.getFragmentMatchData();
            }
            else {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof DataFragment) {
                        jsonArray = ((DataFragment) fragment).getFragmentMatchData();
                        Log.d(TAG, jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonCollection.put(jsonArray.getJSONObject(i));
                        }
                    }
                }

            }
            jsonFile.put("scoutingData", jsonCollection);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to compile match data from each fragment to send.\n" + e);
            return;
        }

        FileSaver.saveFile(jsonFile.toString(), preAuton.getFileTitle(), this);

        if(connectivity.get()) {
            connectedThread.sendMatchRequest(jsonFile.toString().getBytes(StandardCharsets.UTF_8));
        }
        else {
            if(BluetoothSDPThread.bluetoothConnect(this, false)) {
                while(!connectivity.get()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                connectedThread.sendMatchRequest(jsonFile.toString().getBytes(StandardCharsets.UTF_8));
            }
            else {
                Toast.makeText(this, "Data has not been uploaded because bluetooth isn't connected", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void smartUpload(String localMatches) {
        connectedThread.smartUploadRequest(localMatches.getBytes(StandardCharsets.UTF_8));
    }

    public void submitNeededMatches(String[] neededMatches) {
        int index;
        File temp;

        for(String curr : neededMatches) {
            index = Integer.parseInt(curr);

            temp = archiveFragment.getFile(index);
            try {
//                Log.w(TAG,curr);
                connectedThread.sendMatchRequest(Files.readAllBytes(temp.toPath()));
            } catch (IOException e) {
                Log.e(TAG, "Failed to read from file", e);
            }
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

    public void autonStart() {
        currentState = gameState.autonStarted;
        updateTabletInformation();
        MatchTiming.scheduleRunAfterAuto(this::autonStop, this);
    }

    public void autonStop() {
        if(currentState == gameState.autonStarted) {
            currentState = gameState.autonStopped;
        }
        runOnUiThread(auton::endAuton);
    }

    public int getAutonStartPos() {
        String pos = preAuton.getPos();

        if(settingsFragment.getFieldFlipped()) {
            if(pos.equals("RED")) {
                return 0;
            }
            else {
                return 2;
            }
        }
        else {
            if(pos.equals("BLUE")) {
                return 0;
            }
            else {
                return 2;
            }
        }
    }

    public void teleopStart() {
        currentState = gameState.teleopStarted;
        updateTabletInformation();
        MatchTiming.scheduleRunAfterTeleop(this::teleopStop, this);
    }

    public void teleopStop() {
        currentState = gameState.postMatch;
        updateTabletInformation();
        runOnUiThread(teleop::endTeleop);
    }

    public int getTeleopStartPos() {
        String pos = auton.getAutonPos();

        if(pos.equals("Neutral")) {
            return 1;
        }
        else {
            if(settingsFragment.getFieldFlipped()) {
                if(pos.equals("Red")) {
                    return 0;
                }
                else {
                    return 2;
                }
            }
            else {
                if(pos.equals("Blue")) {
                    return 0;
                }
                else {
                    return 2;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        if(permissionManager.permissionNotGranted(BLUETOOTH_CONNECT)) {
            return "ERROR";
        }
        else {
            return ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getName();
        }
    }

    public void updateTeamNumber(int teamNumber) {
        auton.updateTeamNumber(teamNumber);
        teleop.updateTeamNumber(teamNumber);
        postMatch.updateTeamNumber(teamNumber);
    }

    public void togglePractice() {
        practice = !practice;
        menuFragment.updatePractice(practice);
    }

    public boolean getPractice() {
        return practice;
    }

    public int getReplayLevel() {
        return replayLevel;
    }

    public void increaseReplayLevel() {
        replayLevel++;
        preAuton.updateMatches();
    }

    public void setQrCode(String contents) {
        this.qrCodeContents = contents;
    }

    public void clearQrCode() {
        qrCodeContents = "";
    }

    public String getQrCodeContents() {
        return qrCodeContents;
    }

    public void flipField(boolean fieldFlipped) {
        preAuton.flipField(fieldFlipped);
        auton.flipField(fieldFlipped);
        teleop.flipField(fieldFlipped);
//        preAuton.attemptDeviceNameParse();
    }

    public String getCurrentState() {
        switch(currentState) {
            case preAuton:
                return "Pre-Auton";
            case autonStarted:
            case autonStopped:
                return "Auton";
            case teleopStarted:
                return "Teleop";
            case postMatch:
                return "PostMatch";
            default:
                return "Nothing";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragmentsToManager();

        addPermissions();
        permissionManager.requestPermissions();
        BluetoothSDPThread.bluetoothConnectAsync(this, false);
    }
}