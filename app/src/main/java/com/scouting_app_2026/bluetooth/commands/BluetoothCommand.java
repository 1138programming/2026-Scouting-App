package com.scouting_app_2026.bluetooth.commands;

public class BluetoothCommand {
    public final CommandType type;
    public final byte[] bytes;

    public BluetoothCommand(CommandType type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }
}
