package com.scouting_app_template.JSON;

import static com.scouting_app_template.MainActivity.TAG;
import static com.scouting_app_template.MainActivity.defaultTimestamp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    private final JSONObject jsonTemplate;
    private final JSONArray masterJSON = new JSONArray();
    public JSONManager(JSONObject jsonTemplate) {
        this.jsonTemplate = jsonTemplate;
    }

    public void addDatapoint(int datapointID, String value, int timestamp) {
        JSONObject temp;
        try {
            temp = new JSONObject(jsonTemplate.toString());
        } catch (JSONException e) {
            Log.wtf(TAG, "Something horrible has gone wrong when creating new template JSON");
            return;
        }

        try {
            temp.put("DatapointID", datapointID);
            temp.put("DatapointValue", value);
            temp.put("DatapointTimestamp", timestamp);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to add datapoint", e);
            return;
        }
        masterJSON.put(temp);
    }

    public void addDatapoint(int datapointID, String value) {
        addDatapoint(datapointID, value, defaultTimestamp);
    }

    public JSONArray getJSON() {
        return masterJSON;
    }
}
