package com.scouting_app_template.DatapointIDs;

public enum DatapointID {
    other,
    startPos,
    autonRedWin,
    autonEnteredRed,
    autonEnteredNeutral,
    autonEnteredBlue,
    autonScored,
    autonCollected,
    autonShuttled,
    autonImmobile,
    autonOutpost,
    autonDepot,
    autonNumScored,
    autonHangAttempted,
    autonHangSuccessful,
    teleopEnteredRed,
    teleopEnteredNeutral,
    teleopEnteredBlue,
    teleopScored,
    teleopCollected,
    teleopShuttled,
    teleopImmobile,
    teleopOutpost,
    teleopDefense,
    teleopHangAttempted,
    teleopHangSuccessful,
    teleopNumScored,
    teleopScoreAccuracy,
    scouterConfidence;

    public int getID() {
        return ordinal();
    }
}
