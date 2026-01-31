package com.scouting_app_template.UIElements;

import static com.scouting_app_template.MainActivity.TAG;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.scouting_app_template.MainActivity;
import com.scouting_app_template.R;

import java.util.ArrayList;

public class Spinner extends UIElement {
    private final android.widget.Spinner spinner;
    private final boolean addOther;
    private String currSelected = "";
    public Spinner(int datapointID, android.widget.Spinner spinner, boolean addOther) {
        super(datapointID);
        this.spinner = spinner;
        this.addOther = addOther;
        if(addOther) updateSpinnerList(new ArrayList<>());
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSelected = spinner.getSelectedItem().toString();
                clicked();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setSelection(0);
            }
        });
    }

    @Override
    public void clicked() {
        super.clicked();
    }

    @Override
    public String getValue() {
        try {
            return spinner.getSelectedItem().toString();
        }
        catch(Exception e) {
            Log.e(TAG, "Failed to get value from Spinner: ", e);
            return "";
        }
    }

    public int getSelectedIndex() {
        return spinner.getSelectedItemPosition();
    }
    public void setIndex(int index) {
        spinner.setSelection(index);
    }
    public int getLength() {
        return spinner.getCount();
    }

    public void updateSpinnerList(ArrayList<CharSequence> providedList) {
        ArrayList<CharSequence> spinnerList = new ArrayList<>();
        if(addOther) spinnerList.add("Other");
        spinnerList.addAll(providedList);
        ArrayAdapter<CharSequence> listAdapter
                = new ArrayAdapter<>(MainActivity.context, R.layout.spinner_layout, spinnerList);
        listAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(listAdapter);
        int index = listAdapter.getPosition(currSelected);
        if(index != -1) {
            spinner.setSelection(index);
        }
    }
}
