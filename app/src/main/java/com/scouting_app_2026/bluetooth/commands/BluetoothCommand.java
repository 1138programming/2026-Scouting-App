package com.scouting_app_2026.bluetooth.commands;

import java.util.function.Consumer;

import javax.security.auth.callback.Callback;

public class BluetoothCommand {
    public final CommandType type;
    public final byte[] bytes;
    public final Consumer<Boolean> callback;

    public BluetoothCommand(CommandType type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
        this.callback = null;
    }

    public BluetoothCommand(CommandType type, byte[] bytes, Consumer<Boolean> callback) {
        this.type = type;
        this.bytes = bytes;
        this.callback = callback;
    }
}
