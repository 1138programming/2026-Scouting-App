package com.scouting_app_2026.Fragments.Popups;

import static com.scouting_app_2026.MainActivity.context;
import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.DatapointIDs.NonDataIDs;
import com.scouting_app_2026.Fragments.PreAutonFragment;
import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.databinding.ConfirmResetFragmentBinding;

import java.util.Objects;

public class ResetFragment extends Fragment {
    ConfirmResetFragmentBinding binding;

    public ResetFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = ConfirmResetFragmentBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cancelButton = new Button(
                NonDataIDs.ResetCancel.getID(), binding.cancelButton);
        cancelButton.setOnClickFunction(() -> ftm.resetCancel());

        Button startButton = new Button(
                NonDataIDs.ResetConfirm.getID(), binding.confirmButton);
        startButton.setOnClickFunction(((PreAutonFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("PreAutonFragment")))::decrementMatchIndex);
        startButton.setOnClickFunction(((MainActivity)context)::recreateFragments);
    }

    @NonNull
    @Override
    public String toString() {
        return "ConfirmResetFragment";
    }
}
