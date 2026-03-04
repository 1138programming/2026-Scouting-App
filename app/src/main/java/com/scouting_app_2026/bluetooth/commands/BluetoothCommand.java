package com.scouting_app_2026.bluetooth.commands;

public class BluetoothCommand {
    public final CommandType type;
    public final byte[] bytes;
    public final int code;

    public BluetoothCommand(CommandType type, byte[] bytes, int code) {
        this.type = type;
        this.bytes = bytes;
        this.code = code;
    }
}
