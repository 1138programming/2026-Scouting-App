package com.scouting_app_template.Fragments.Popups;

import static com.scouting_app_template.MainActivity.context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.scouting_app_template.MainActivity;
import com.scouting_app_template.databinding.FragmentBlockerBinding;

public class BlockerFragment extends Fragment {
    FragmentBlockerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentBlockerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.layout.setOnClickListener(View1 -> Toast.makeText(context,
                "This part of the  ", Toast.LENGTH_LONG).show());
        binding.layout.setOnClickListener(View1 -> ((MainActivity)context).updateFragments());
    }

    @NonNull
    @Override
    public String toString() {
        return "BlockerFragment";
    }
}
