package com.scouting_app_2026.datapointIDs;

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
