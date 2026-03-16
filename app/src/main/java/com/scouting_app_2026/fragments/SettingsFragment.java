package com.scouting_app_2026.fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.databinding.SettingsFragmentBinding;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.ftm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SettingsFragment extends Fragment {
    SettingsFragmentBinding binding;
    private final String fileName = "settings.txt";
    private File folderDir;
    private boolean dirExists;
    // settings
    private final int numSettings = 2;
    private boolean fieldFlipped = false;
    private boolean rightHanded = true;

    public SettingsFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();

        this.folderDir = new File(requireActivity().getFilesDir().getPath() + "/settings");

        if (!folderDir.isDirectory()) {
            if (!folderDir.mkdir()) {
                dirExists = false;
                Log.e(TAG, "File System is Broken");
                return;
            }
        }
        dirExists = true;

        String fileText;
        try {
            File targetFile = new File(folderDir, fileName);
            fileText = readFromFile(targetFile);
        }
        catch(IOException e) {
            Log.e(TAG, "Unable to create file", e);
            return;
        }

        String[] splitFile = fileText.split("\n");
        if(!fileText.isEmpty()) {
            for(int i = 0; i < numSettings; i++) {
                if(splitFile.length > i) {
                    setSetting(i, splitFile[i]);
                }
            }
        }
        if(splitFile.length != numSettings) saveSettingsToFile();
        updateFields();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = SettingsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.flipButton.setOnClickListener(View1 -> flipField());

        binding.closeMenu.setOnClickListener(view1 -> ftm.settingsClose());
    }

    private void flipField() {
        fieldFlipped = !fieldFlipped;
        saveSettingsToFile();
        updateFields();
    }

    private void updateFields() {
        ((MainActivity)requireActivity()).flipField(fieldFlipped);

        if(fieldFlipped) {
            binding.imageView.setVisibility(INVISIBLE);
            binding.reversedImage.setVisibility(VISIBLE);
        }
        else {
            binding.imageView.setVisibility(VISIBLE);
            binding.reversedImage.setVisibility(INVISIBLE);
        }
    }

    private String readFromFile(File file) throws IOException {
        if (!file.exists()) {
            if(!file.createNewFile()) {
                Log.e(TAG, "Unable to create file");
                throw new IOException();
            }
        }

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File doesn't exist", e);
            return "";
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader((inputStreamReader));
        String line = reader.readLine();
        while(line != null) {
            sb.append(line).append('\n');
            line = reader.readLine();
        }
        if(sb.length() > 0) {
            //removes last "\n"
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();
    }

    private void saveSettingsToFile() {
        if(!dirExists) return;

        try {
            File targetFile = new File(folderDir, fileName);
            if (!targetFile.exists()) {
                if (!targetFile.createNewFile()) {
                    throw new IOException("Unable to create new file");
                }
            }
            FileWriter fileWriter = new FileWriter(targetFile, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(getSettings());

            bufferedWriter.close();
        }
        catch(IOException e) {
            Log.e(TAG, "Failed to write settings to file", e);
        }
    }

    private void setSetting(int i, String setting) {
        switch(i) {
            case 0:
                fieldFlipped = setting.equals("1");
                break;
            case 1:
                rightHanded = setting.equals("1");
                break;
        }
    }

    private String getSetting(int i) {
        switch(i) {
            case 0:
                if(fieldFlipped) return "1";
                else return "0";
            case 1:
                if(rightHanded) return "1";
                else return "0";
            default:
                return "";
        }
    }

    private String getSettings() {
        StringBuilder settings = new StringBuilder();
        for(int i = 0; i < numSettings; i++) {
            settings.append(getSetting(i));

            if(i+1 < numSettings) settings.append('\n');
        }

        return settings.toString();
    }

    public boolean getFieldFlipped() {
        return fieldFlipped;
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsFragment";
    }
}
