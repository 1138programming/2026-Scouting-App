package com.scouting_app_2026.JSON;

public class TemplateContext {
    private static TemplateContext instance;
    private String compID;
    private int scouterID;
    private String matchID;
    private int teamID;
    private int allianceID;

    private TemplateContext() {

    }

    public static synchronized TemplateContext getInstance() {
        if(instance == null) {
            instance = new TemplateContext();
        }
        return instance;
    }

    public String getCompID() {
        if(compID == null) {
            return "1992cmp";
        }
        return compID;
    }

    public void setCompID(String compID) {
        this.compID = compID;
    }

    public String getScouterID() {
        return Integer.toString(scouterID);
    }

    public void setScouterID(int scouterID) {
        this.scouterID = scouterID;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getTeamID() {
        return Integer.toString(teamID);
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public String getAllianceID() {
        return Integer.toString(allianceID);
    }

    public void setAllianceID(int allianceID) {
        this.allianceID = allianceID;
    }
}