package com.scouting_app_template.Fragments.Popups;

import static com.scouting_app_template.MainActivity.context;
import static com.scouting_app_template.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_template.DatapointIDs.NonDataIDs;
import com.scouting_app_template.MainActivity;
import com.scouting_app_template.UIElements.Button;
import com.scouting_app_template.databinding.ConfirmSubmitFragmentBinding;

public class ConfirmSubmit extends Fragment {
    ConfirmSubmitFragmentBinding binding;

    public ConfirmSubmit() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = ConfirmSubmitFragmentBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cancelButton = new Button(
                NonDataIDs.ConfirmSubmitCancel.getID(), binding.cancelButton);
        cancelButton.setOnClickFunction(() -> ftm.confirmSubmitBack());

        Button startButton = new Button(
                NonDataIDs.ConfirmSubmitSubmit.getID(), binding.submitButton);
        startButton.setOnClickFunction(this::matchSubmit);
    }

    private void matchSubmit() {
        ((MainActivity)context).sendMatchData();
        ((MainActivity)context).recreateFragments();
    }

    @NonNull
    @Override
    public String toString() {
        return "ConfirmSubmitFragment";
    }
}
