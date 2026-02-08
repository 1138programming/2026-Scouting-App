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
import com.scouting_app_2026.databinding.TeleopStartFragmentBinding;
import com.scouting_app_2026.datapointIDs.NonDataIDs;
import com.scouting_app_2026.fragments.TeleopFragment;

import java.util.Objects;

public class TeleopStart extends Fragment {
    TeleopStartFragmentBinding binding;

    public TeleopStart() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = TeleopStartFragmentBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = new Button(
                NonDataIDs.TeleopStartBack.getID(), binding.backButton);
        backButton.setOnClickFunction(() -> ftm.teleopStartBack());

        Button startButton = new Button(
                NonDataIDs.TeleopStartStart.getID(), binding.startButton);
        startButton.setOnClickFunction(() -> ((TeleopFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("TeleopFragment"))).startTeleop());
        startButton.setOnClickFunction(((MainActivity)requireActivity())::teleopStart);
        startButton.setOnClickFunction(() -> ftm.teleopStartStart());
    }

    @NonNull
    @Override
    public String toString() {
        return "TeleopStartFragment";
    }
}
