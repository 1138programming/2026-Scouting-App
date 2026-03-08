package com.scouting_app_2026.fragments.popups;

import static com.scouting_app_2026.MainActivity.ftm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_2026.UIElements.Button;
import com.scouting_app_2026.databinding.ConfirmSubmitAllBinding;
import com.scouting_app_2026.datapointIDs.NonDataIDs;
import com.scouting_app_2026.fragments.ArchiveFragment;

import java.util.Objects;

public class ConfirmSubmitAll extends Fragment {

    ConfirmSubmitAllBinding binding;

    public ConfirmSubmitAll() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = ConfirmSubmitAllBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = new Button(
                NonDataIDs.ArchiveCancel.getID(), binding.cancelButton);
        backButton.setOnClickFunction(() -> ftm.submitAllClose());

        Button submitButton = new Button(
                NonDataIDs.ArchiveConfirm.getID(), binding.submitButton);
        submitButton.setOnClickFunction(() -> ((ArchiveFragment) Objects.requireNonNull(
                getParentFragmentManager().findFragmentByTag("ArchiveFragment"))).submitAll());
    }

    @NonNull
    @Override
    public String toString() {
        return "ConfirmSubmitAllFragment";
    }
}
