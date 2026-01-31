package com.scouting_app_template.Fragments.Popups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_template.Fragments.AutonFragment;
import com.scouting_app_template.MainActivity;
import com.scouting_app_template.UIElements.Button;

import static com.scouting_app_template.MainActivity.context;
import static com.scouting_app_template.MainActivity.ftm;

import com.scouting_app_template.DatapointIDs.NonDataIDs;
import com.scouting_app_template.databinding.AutonStartFragmentBinding;

import java.util.Objects;

public class AutonStart extends Fragment {
    AutonStartFragmentBinding binding;

    public AutonStart() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = AutonStartFragmentBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = new Button(
                NonDataIDs.AutonStartBack.getID(), binding.backButton);
        backButton.setOnClickFunction(() -> ftm.autonStartBack());

        Button startButton = new Button(
                NonDataIDs.AutonStartStart.getID(), binding.startButton);
        startButton.setOnClickFunction(() -> ((AutonFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("AutonFragment"))).startAuton());
        startButton.setOnClickFunction(((MainActivity)context)::autonStart);
        startButton.setOnClickFunction(() -> ftm.autonStartStart());
    }

    @NonNull
    @Override
    public String toString() {
        return "AutonStartFragment";
    }
}
