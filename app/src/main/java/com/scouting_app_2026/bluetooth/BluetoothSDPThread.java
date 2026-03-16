package com.scouting_app_2026.bluetooth;

import static com.scouting_app_2026.MainActivity.MY_UUID;
import static com.scouting_app_2026.MainActivity.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.scouting_app_2026.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BluetoothSDPThread {
    private static BluetoothSocket socket;
    private static final String fileName = "mac.txt";
    private static boolean dirExists;

    @SuppressLint("MissingPermission")
    public static void bluetoothConnectAsync(MainActivity mainActivity, boolean scanOnFail) {
        Thread thread = new Thread(() -> {
            bluetoothConnect(mainActivity, scanOnFail);
        });
        thread.start();
    }

    @SuppressLint("MissingPermission")
    public static boolean bluetoothConnect(MainActivity mainActivity, boolean scanOnFail) {
        if (mainActivity.permissionManager.permissionNotGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.e(TAG, "Need permission for Bluetooth_Connect");
            return false;
        }
        BluetoothAdapter adapter = ((BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        adapter.cancelDiscovery();

        String mac = getMacAddress(mainActivity);
        if(!dirExists) {
            return false;
        }

        if(mac.isEmpty()) {
            if(scanOnFail) mainActivity.menuFragment.scanCode();
            return false;
        }

        BluetoothSocket tmp;
        BluetoothDevice device = adapter.getRemoteDevice(mac);

        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket = tmp;
        } catch (IOException e) {
            Log.e(TAG, "connect method failed", e);
            return false;
        }

        if (socket == null) return false;

        try {
            Log.e(TAG, "badlet?!");
            socket.connect();
            Log.e(TAG, "ROBERTBADLETTTTT!");
        } catch (IOException e) {
            Log.e(TAG, "Timed out/error", e);
            // Unable to connect; close the socket and return.
            QrBtConnThread.clearMac(mainActivity);
            cancel();
            return false;
        }
        new BluetoothConnectedThread(socket, mainActivity).start();
        return true;
    }

    private static String getMacAddress(MainActivity activity) {
        File folderDir = new File(activity.getFilesDir().getPath() + "/settings");
        if (!folderDir.isDirectory()) {
            if (!folderDir.mkdir()) {
                dirExists = false;
                Log.e(TAG, "File System is Broken");
                return "";
            }
        }
        dirExists = true;

        try {
            File targetFile = new File(folderDir, fileName);
            return readFromFile(targetFile);
        }
        catch(IOException e) {
            Log.e(TAG, "Unable to create file", e);
            return "";
        }
    }

    private static String readFromFile(File file) throws IOException {
        if (!file.exists()) {
            if(!file.createNewFile()) {
                Log.e(TAG, "Unable to create file");
                throw new IOException();
            }
        }

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File doesn't exist", e);
            return "";
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader((inputStreamReader));
        String line = reader.readLine();
        while(line != null) {
            sb.append(line).append('\n');
            line = reader.readLine();
        }
        if(sb.length() > 0) {
            //removes last "\n"
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();
    }

    public static void cancel() {
        try {
            socket.close();
            Log.e(TAG, "socket closed");
        } catch (IOException e) {
            Log.e(TAG, "couldn't close", e);
        }
    }
}
