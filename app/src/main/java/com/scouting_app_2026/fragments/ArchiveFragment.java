package com.scouting_app_2026.fragments;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.R;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.databinding.ArchiveFragmentBinding;
import com.scouting_app_2026.datapointIDs.NonDataIDs;
import com.scouting_app_2026.fragments.popups.ArchiveConfirm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ArchiveFragment extends DataFragment {
    ArchiveFragmentBinding binding;
    File folderDir;
    private File lastSelectedFile;

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

    @NonNull
    @Override
    public String toString() {
        return "ArchiveFragment";
    }
}
