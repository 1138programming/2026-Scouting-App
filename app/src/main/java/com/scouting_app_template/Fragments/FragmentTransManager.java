package com.scouting_app_template.Fragments;

import static com.scouting_app_template.MainActivity.context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.scouting_app_template.MainActivity;
import com.scouting_app_template.R;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentTransManager {
    private FragmentTransaction ft;
    private final FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();

    public FragmentTransManager(ArrayList<Fragment> fragments) {
        ((MainActivity) context).setContentView(R.layout.activity_main);

        ft = fm.beginTransaction();
        for (Fragment fragment : fragments) {
            ft.add(R.id.main_fragment, fragment, fragment.toString());
        }
        ft.show(fragments.get(0));
        for(int i = 1; i < fragments.size(); i++) {
            ft.hide(fragments.get(i));
        }
        ft.commitNow();
    }

    private void showFragment(String tag) {
        ft.show(Objects.requireNonNull(fm.findFragmentByTag(tag)));
    }

    private void hideFragment(String tag) {
        ft.hide(Objects.requireNonNull(fm.findFragmentByTag(tag)));
    }

    public void preAutonNext() {
        ft = fm.beginTransaction();
        hideFragment("PreAutonFragment");
        showFragment("AutonFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void showAutonStart() {
        ft = fm.beginTransaction();
        showFragment("AutonStartFragment");
        ft.commitNow();
    }

    public void autonStartBack() {
        ft = fm.beginTransaction();
        hideFragment("AutonStartFragment");
        hideFragment("AutonFragment");
        showFragment("PreAutonFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void autonStartStart() {
        ft = fm.beginTransaction();
        hideFragment("AutonStartFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void autonBack() {
        ft = fm.beginTransaction();
        hideFragment("AutonFragment");
        showFragment("PreAutonFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void autonNext() {
        ft = fm.beginTransaction();
        hideFragment("AutonFragment");
        showFragment("TeleopFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void showTeleopStart() {
        ft = fm.beginTransaction();
        showFragment("TeleopStartFragment");
        ft.commitNow();
    }

    public void teleopStartBack() {
        ft = fm.beginTransaction();
        hideFragment("TeleopStartFragment");
        hideFragment("TeleopFragment");
        showFragment("AutonFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void teleopStartStart() {
        ft = fm.beginTransaction();
        hideFragment("TeleopStartFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void teleopBack() {
        ft = fm.beginTransaction();
        hideFragment("TeleopFragment");
        showFragment("AutonFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void teleopNext() {
        ft = fm.beginTransaction();
        hideFragment("TeleopFragment");
        showFragment("PostMatchFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void postMatchBack() {
        ft = fm.beginTransaction();
        hideFragment("PostMatchFragment");
        showFragment("TeleopFragment");
        ft.commitNow();
        ((MainActivity)context).updateFragments();
    }

    public void matchSubmit() {
        ft = fm.beginTransaction();
        showFragment("ConfirmSubmitFragment");
        ft.commitNow();
    }

    public void confirmSubmitBack() {
        ft = fm.beginTransaction();
        hideFragment("ConfirmSubmitFragment");
        ft.commitNow();
    }

    public void showBlocker() {
        ft = fm.beginTransaction();
        showFragment("BlockerFragment");
        ft.commitNow();
    }

    public void hideBlocker() {
        ft = fm.beginTransaction();
        hideFragment("BlockerFragment");
        ft.commitNow();
    }

    public void preAutonMenu() {
        ft = fm.beginTransaction();
        showFragment("MenuFragment");
        ft.commitNow();
    }

    public void menuClose() {
        ft = fm.beginTransaction();
        hideFragment("MenuFragment");
        ft.commitNow();
    }

    public void menuReset() {
        ft = fm.beginTransaction();
        hideFragment("MenuFragment");
        showFragment("ConfirmResetFragment");
        ft.commitNow();
    }

    public void resetCancel() {
        ft = fm.beginTransaction();
        hideFragment("ConfirmResetFragment");
        ft.commitNow();
    }

    public void menuArchive() {
        ft = fm.beginTransaction();
        hideFragment("MenuFragment");
        hideFragment("PreAutonFragment");
        showFragment("ArchiveFragment");
        ft.commitNow();
    }

    public void archiveSubmit() {
        ft = fm.beginTransaction();
        showFragment("ArchiveConfirmFragment");
        ft.commitNow();
    }

    public void archiveSubmitCancel() {
        ft = fm.beginTransaction();
        hideFragment("ArchiveConfirmFragment");
        ft.commitNow();
    }

    public void archiveFragmentBack() {
        ft = fm.beginTransaction();
        hideFragment("ArchiveFragment");
        showFragment("PreAutonFragment");
        ft.commitNow();
    }
}
