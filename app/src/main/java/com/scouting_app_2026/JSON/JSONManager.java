package com.scouting_app_2026.JSON;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.MainActivity.defaultTimestamp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    private final JSONArray masterJSON = new JSONArray();
    public JSONManager() {

    }

    public void addDatapoint(int datapointID, String value, int timestamp) {
        TemplateContext context =  TemplateContext.getInstance();
        JSONObject temp = new JSONObject();

        try {
            temp.put("CompID", context.getCompID());
            temp.put("MatchID", context.getMatchID());
            temp.put("DatapointID", datapointID);
            temp.put("ScouterID", context.getScouterID());
            temp.put("TeamID", context.getTeamID());
            temp.put("AllianceID", context.getAllianceID());
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