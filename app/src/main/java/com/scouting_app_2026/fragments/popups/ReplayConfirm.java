package com.scouting_app_2026.fragments.popups;

import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.databinding.ConfirmReplayFragmentBinding;
import com.scouting_app_2026.datapointIDs.NonDataIDs;

public class ReplayConfirm extends Fragment {
    ConfirmReplayFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = ConfirmReplayFragmentBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cancelButton = new Button(
                NonDataIDs.PracticeClose.getID(), binding.cancelButton);
        cancelButton.setOnClickFunction(() -> ftm.replayClose());

        Button startButton = new Button(
                NonDataIDs.PracticeConfirm.getID(), binding.confirmButton);
        startButton.setOnClickFunction(() -> ftm.replayClose());
        startButton.setOnClickFunction(((MainActivity)requireActivity())::increaseReplayLevel);
    }

    @NonNull
    @Override
    public String toString() {
        return "ReplayConfirm";
    }
}
