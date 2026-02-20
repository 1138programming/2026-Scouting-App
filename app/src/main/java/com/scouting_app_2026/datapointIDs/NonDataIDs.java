package com.scouting_app_2026.datapointIDs;

public enum NonDataIDs {
    Default,
    ScouterName,
    MatchNumber,
    TeamColor,
    NoShow,
    StartPosRadio,
    TeamNumber,
    PreAutonNext,
    ArchiveHamburger,
    AutonStartBack,
    AutonStartStart,
    AutonUndo,
    AutonRedo,
    AutonScored,
    AutonMissed,
    AutonBack,
    AutonNext,
    TeleopStartBack,
    TeleopStartStart,
    TeleopUndo,
    TeleopRedo,
    TeleopScored,
    TeleopMissed,
    TeleopBack,
    TeleopNext,
    PostMatchBack,
    PostMatchSubmit,
    ConfirmSubmitCancel,
    ConfirmSubmitSubmit,
    ButtonStack,
    ArchiveClose,
    ArchiveCancel,
    ArchiveConfirm,
    ResetCancel,
    ResetConfirm,
    PracticeClose,
    PracticeConfirm,
    AdminCompIDSpinner;

    public int getID() {
        return -ordinal();
    }
}
