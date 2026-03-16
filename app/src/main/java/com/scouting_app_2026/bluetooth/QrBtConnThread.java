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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QrBtConnThread {
    private static BluetoothSocket socket;
    private static final String fileName = "mac.txt";
    public QrBtConnThread() {

    }

    /**
     * Creates a BluetoothConnectedThread based on a given MAC address and port.
     *
     * @param mac The MAC address of the device
     */
     @SuppressLint("MissingPermission")
     public static void bluetoothConnect(String mac, MainActivity mainActivity) {
        Thread thread = new Thread(() -> {
            if (mainActivity.permissionManager.permissionNotGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.e(TAG, "need permission for Bluetooth_Connect");
            }
            BluetoothAdapter adapter = ((BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            adapter.cancelDiscovery();

            BluetoothSocket tmp;
            BluetoothDevice device = adapter.getRemoteDevice(mac);

            saveMacToFile(mac, mainActivity);

            try {
//            Method method = device.getClass().getMethod("createInsecureRfcommSocket", int.class);
//            tmp = (BluetoothSocket) method.invoke(device, port);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket = tmp;
            } catch (IOException e) {
                Log.e(TAG, "connect method failed", e);
                return;
            }

            if (socket == null) return;

            try {
                Log.e(TAG, "badlet?");
                socket.connect();
                Log.e(TAG, "ROBERTBADLETTTTT");
            } catch (IOException e) {
                Log.e(TAG, "Timed out/error", e);
                // Unable to connect; close the socket and return.
                cancel();
                return;
            }
            new BluetoothConnectedThread(socket, mainActivity).start();
        });
        thread.start();
     }

     private static void saveMacToFile(String mac, MainActivity activity) {
         File folderDir = new File(activity.getFilesDir().getPath() + "/settings");
         if (!folderDir.isDirectory()) {
             if (!folderDir.mkdir()) {
                 Log.e(TAG, "File System is Broken");
                 return;
             }
         }

         try {
             File targetFile = new File(folderDir, fileName);
             if (!targetFile.exists()) {
                 if (!targetFile.createNewFile()) {
                     throw new IOException("Unable to create new file");
                 }
             }
             FileWriter fileWriter = new FileWriter(targetFile, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             bufferedWriter.write(mac);

             bufferedWriter.close();
         }
         catch(IOException e) {
             Log.e(TAG, "Failed to write Mac to file", e);
         }
     }

     public static void clearMac(MainActivity activity) {
         File folderDir = new File(activity.getFilesDir().getPath() + "/settings");
         if (!folderDir.isDirectory()) {
             if (!folderDir.mkdir()) {
                 Log.e(TAG, "File System is Broken");
                 return;
             }
         }

         try {
             File targetFile = new File(folderDir, fileName);
             if (!targetFile.exists()) {
                 if (!targetFile.createNewFile()) {
                     throw new IOException("Unable to create new file");
                 }
             }
             FileWriter fileWriter = new FileWriter(targetFile, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             bufferedWriter.write("");

             bufferedWriter.close();
         }
         catch(IOException e) {
             Log.e(TAG, "Failed to write Mac to file", e);
         }
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
