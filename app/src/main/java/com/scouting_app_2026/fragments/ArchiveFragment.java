package com.scouting_app_2026.fragments;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.databinding.ArchiveFragmentBinding;
import com.scouting_app_2026.datapointIDs.NonDataIDs;
import com.scouting_app_2026.fragments.popups.ArchiveConfirm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArchiveFragment extends DataFragment {
    ArchiveFragmentBinding binding;
    File folderDir;
    private File lastSelectedFile;
    private File[] files;

    public ArchiveFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = ArchiveFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        folderDir = new File(requireContext().getFilesDir().getPath() + "/scoutingData");
        if (!folderDir.isDirectory()) {
            if (!folderDir.mkdir()) {
                Log.e(TAG,"Unable to make directory: \"" + folderDir.getPath() + "\"");
            }
        }

        Button backButton = new Button(NonDataIDs.ArchiveClose.getID(), binding.closeArchive);
        backButton.setOnClickFunction(() -> ftm.archiveFragmentBack());

        binding.submitAll.setOnClickListener(view1 -> ftm.archiveSubmitAll());

        File[] files = folderDir.listFiles();
        if(files != null) {
            String[] fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
            }

            ArrayList<String> namesList = new ArrayList<>();
            Collections.addAll(namesList, fileNames);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_layout, namesList);
            for(int i = 0; i < adapter.getCount(); i++) {
                if(!Objects.requireNonNull(adapter.getItem(i)).contains(".json")) {
                    adapter.remove(adapter.getItem(i));
                    i--;
                }
            }
            binding.submissionList.setAdapter(adapter);
            binding.submissionList.setOnItemClickListener((parent, view1, position, id)
                    -> setLastSelected(binding.submissionList.getItemAtPosition(position)));
        }
    }

    public void submitFile() {
        ftm.archiveSubmitCancel();
        ((MainActivity)requireActivity()).sendSavedData(lastSelectedFile);
    }
    private void setLastSelected(Object listEntry) {
        ftm.archiveSubmit();
        lastSelectedFile = new File(folderDir, listEntry.toString());
        ((ArchiveConfirm) Objects.requireNonNull(getParentFragmentManager()
                .findFragmentByTag("ArchiveConfirmFragment"))).setFileName(listEntry.toString());
    }

    public void submitAll() {
        ftm.submitAllClose();

        MainActivity mainActivity = ((MainActivity)requireActivity());

        if(!mainActivity.getConnectivity()) {
            Toast.makeText(requireContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] files = folderDir.listFiles();
        if(files != null) {
            for (File file : files) {
                mainActivity.sendSavedData(file);
            }
        }
    }

    // check scouter, comp id, match id, team number
    public void smartSubmit() {
        ftm.submitAllClose();

        MainActivity mainActivity = ((MainActivity)requireActivity());

        if(!mainActivity.getConnectivity()) {
            Toast.makeText(requireContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        files = folderDir.listFiles();
        if(files != null) {
            String[] fileInfo = new String[files.length];
            StringBuilder everyMatchInfo = new StringBuilder();
            String compID;
            String teamID;
            String matchID;

            Pattern pattern = Pattern.compile("\"CompID\":\"([^,]+)\".*?\"MatchID\":\"([^,]+)\".*?\"TeamID\":\"([^,]+)\"");
            Matcher matcher;

            for(File file : files) {
                String text;
                try {
                    text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                }
                catch(IOException e) {
                    Log.e(TAG, "Unable to read file", e);
                    return;
                }
                /*
                * Group(0) - whole match
                * Group(1) - CompID
                * Group(2) - MatchID
                * Group(3) - TeamID
                * */
                matcher = pattern.matcher(text);

                if(matcher.find()) {
                    compID = matcher.group(1);
                    teamID = matcher.group(3);
                    matchID = matcher.group(2);
                }
                else {
                    Log.e(TAG, "regex not found in file");
                    continue;
                }

                everyMatchInfo.append(compID).append(";").append(teamID).append(";").append(matchID).append("\n");
            }
            String matchInfo = everyMatchInfo.toString();
            if(!matchInfo.isEmpty()) {
                mainActivity.smartUpload(matchInfo);
            }
        }
    }

    //do not use this cursed abomination if you can help it
    private String[] splitJson(String string) {
        String transactionList = string.substring(string.indexOf('[')+1, string.indexOf(']'));
        String[] splitList = transactionList.split("\\},\\{");
        splitList[0] = splitList[0].substring(1);
        splitList[splitList.length-1] = splitList[splitList.length-1].substring(0, splitList[splitList.length-1].length()-1);
        return splitList;
    }

    private String readFile(File file) {
        return "";
    }

    public File getFile(int index) {
        return files[index];
    }

    @NonNull
    @Override
    public String toString() {
        return "ArchiveFragment";
    }
}
