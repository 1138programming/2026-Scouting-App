package com.scouting_app_2026.bluetooth;

import static com.scouting_app_2026.MainActivity.TAG;
import static com.scouting_app_2026.bluetooth.commands.CommandType.CHECK_LISTS;
import static com.scouting_app_2026.bluetooth.commands.CommandType.CHECK_MATCHES;
import static com.scouting_app_2026.bluetooth.commands.CommandType.SEND_TABLET_INFO;
import static com.scouting_app_2026.bluetooth.commands.CommandType.UPLOAD_MATCH;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.scouting_app_2026.JSON.MurmurHash;
import com.scouting_app_2026.JSON.UpdateScoutingInfo;
import com.scouting_app_2026.MainActivity;
import com.scouting_app_2026.bluetooth.commands.BluetoothCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BluetoothConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final MainActivity mainActivity;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final BlockingQueue<BluetoothCommand> commandQueue = new LinkedBlockingQueue<>();
    private ByteBuffer byteBuffer;
    private byte[] buffer;
    private final String ack = "ACK";
    private int timeoutMs = 20000;
    private final byte[] byteAck = ack.getBytes(StandardCharsets.UTF_8);
    private volatile boolean running = true;
    /**
     * 
     */
    public BluetoothConnectedThread(BluetoothSocket socket, MainActivity mainActivity) {
        this.socket = socket;
        this.mainActivity = mainActivity;

        //creates temporary input and output stream objects
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
        }
        catch(IOException e) {
            Log.e(TAG, "Input Stream Error: ", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        }
        catch(IOException e) {
            Log.e(TAG, "Output Stream Error: ", e);
        }

        //sets actual variables to temp versions
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    @Override
    public void run() {
        mainActivity.setConnectedThread(this);
        mainActivity.setConnectivity(true);
        mainActivity.runOnUiThread(mainActivity::updateBtScoutingInfo);

        while(running) {
            try {
                BluetoothCommand command = commandQueue.take();

                switch (command.type) {
                    case UPLOAD_MATCH:
                        handleSendMatch(command.bytes);
                        Log.d(TAG, "handleSendInformation");
                        break;
                    case SEND_TABLET_INFO:
                        handleSendTabletInfo(command.bytes);
                        Log.d(TAG, "handleCheckLists");
                        break;

                    case CHECK_MATCHES:
                        handleCheckSubmittedMatches(command.bytes);
                        Log.d(TAG, "handleCheckLists");
                        break;

                    case CHECK_LISTS:
                        handleCheckLists();
                        Log.d(TAG, "handleCheckLists");
                        break;
                }

            } catch (Exception e) {
                Log.e(TAG, "Error in BT thread", e);
                cancel();
                break;
            }
        }
    }

    private void resetByteBuffer(int capacity) {
        byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Only reads and stores the next message in {@code buffer}. {@code numBytes}
     * is used to set the length of {@code buffer}, not to read that amount of bytes.
     * */
    private void read(int numBytes) throws CommErrorException {
        buffer = new byte[numBytes];
        long readStart = Calendar.getInstance(Locale.US).getTimeInMillis();
        try {
            while (inputStream.available() < numBytes) {
                if (Calendar.getInstance(Locale.US).getTimeInMillis() - readStart > timeoutMs) {
                    throw new CommErrorException();
                }
            }
            inputStream.read(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Failure to read:", e);
            throw new CommErrorException();
        }
    }
    /**
     * Called to read specifically an {@code "ack"} and
     * throws an error if {@code read()} fails or ack is incorrect
     */
    private void readAck() throws CommErrorException {
        read(3);

        String message = new String(buffer, StandardCharsets.UTF_8);
        if(!message.equals(ack)) {
            throw new CommErrorException();
        }
    }
    /**
     * Called to read specifically an {@code "ack"} and
     * throws an error if {@code read()} fails or ack is incorrect
     */
    private void sendAck() throws CommErrorException {
        write(byteAck);
    }

    /**
     * Just write a {@code byte[]} to central computer
     */
    private void write(byte[] bytes) throws CommErrorException{
        try {
            outputStream.write(bytes);
            outputStream.flush();
        }
        catch(IOException e) {
            Log.e(TAG, "Failure to write:", e);
            throw new CommErrorException();
        }
    }

    /**
     * @param code used to specify what information is going to be sent or received <p>
     * &nbsp;&nbsp;1 - send match data<p>
     * &nbsp;&nbsp;2 - send tablet information<p>
     * &nbsp;-1 - check if lists of teams and matches are up to date <p>
     * &nbsp;-2 - update lists of scouters, teams, and matches <p>
     *      {@code IMPORTANT} numbers -1 and -2 shouldn't be used with this function.
     *             Use {@link BluetoothConnectedThread#checkListsRequest()}  and {@link BluetoothConnectedThread#updateLists()} instead as needed
     * sends information
     *
     */
    private void sendBytes(byte[] bytes, int code) throws CommErrorException {
        write(new byte[]{(byte)code});
        readAck();
        resetByteBuffer(4);
        write(byteBuffer.putInt(bytes.length).array());
        readAck();
        write(bytes);
    }
    private void readBytes(int code) throws CommErrorException {
        write(new byte[]{(byte)code});
        resetByteBuffer(4);
        read(4);

        int byteLength = byteBuffer.put(buffer).getInt(0);
        sendAck();

        resetByteBuffer(byteLength);
        read(byteLength);
        sendAck();
    }
    public void sendMatchRequest(byte[] bytes) {
        commandQueue.offer(new BluetoothCommand(UPLOAD_MATCH, bytes));
    }
    private void handleSendMatch(byte[] bytes) {
        try {
            sendBytes(bytes, 1);
        }
        catch(CommErrorException e) {
            Log.e(TAG, "Communication exchange failed", e);
            cancel();
        }
    }

    public void sendTabletInfoRequest(byte[] bytes) {
        commandQueue.offer(new BluetoothCommand(SEND_TABLET_INFO, bytes));
    }
    private void handleSendTabletInfo(byte[] bytes) {
        try {
            sendBytes(bytes, 2);
        }
        catch(CommErrorException e) {
            Log.e(TAG, "Communication exchange failed", e);
            cancel();
        }
    }

    public void checkListsRequest() {
        commandQueue.offer(new BluetoothCommand(CHECK_LISTS, null));
    }
    private void handleCheckLists() {
        int byteLength;
        try {
            write(new byte[]{-1});
            read(4);

            resetByteBuffer(4);
            byteLength = byteBuffer.put(buffer).getInt(0);
            sendAck();

            resetByteBuffer(byteLength);
            read(byteLength);
            sendAck();

            Log.d(TAG, "Murmur Hash: \"" + MurmurHash.makeHash((new UpdateScoutingInfo(mainActivity)).getDataFromFile().getBytes(StandardCharsets.UTF_8)) + "\"");

            int receivedHash = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN) .getInt();
            if(receivedHash != MurmurHash.makeHash((new UpdateScoutingInfo(mainActivity)).getDataFromFile().getBytes(StandardCharsets.UTF_8))) {
                updateLists();
            }
            mainActivity.runOnUiThread(mainActivity::updateLists);
        }
        catch(CommErrorException e) {
            Log.e(TAG, "Communication exchange failed", e);
            cancel();
        }
    }

    private void updateLists() {
        int listLength;
        try {
            write(new byte[]{-2});

            resetByteBuffer(4);
            read(4);
            listLength = byteBuffer.put(buffer).getInt(0);
            sendAck();
            read(listLength);
            sendAck();

            (new UpdateScoutingInfo(mainActivity)).saveToFile(new String(buffer, StandardCharsets.UTF_8));
        }
        catch(CommErrorException | IOException e) {
            Log.e(TAG, "Communication exchange failed", e);
            cancel();
        }

    }

    public void checkSubmittedMatchesRequest(byte[] localMatches) {
        commandQueue.offer(new BluetoothCommand(CHECK_MATCHES,localMatches));
    }

    private void handleCheckSubmittedMatches(byte[] bytes) {
        int byteLength;
        try {
            sendBytes(bytes, -3);
            readBytes(3);

            byteLength = byteBuffer.put(buffer).getInt(0);
            sendAck();

            resetByteBuffer(byteLength);
            read(byteLength);
            sendAck();



            Log.d(TAG, "Murmur Hash: \"" + MurmurHash.makeHash((new UpdateScoutingInfo(mainActivity)).getDataFromFile().getBytes(StandardCharsets.UTF_8)) + "\"");

            int receivedHash = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN) .getInt();
            if(receivedHash != MurmurHash.makeHash((new UpdateScoutingInfo(mainActivity)).getDataFromFile().getBytes(StandardCharsets.UTF_8))) {
                updateLists();
            }
            mainActivity.runOnUiThread(mainActivity::updateLists);
        }
        catch(CommErrorException e) {
            Log.e(TAG, "Communication exchange failed", e);
            cancel();
        }
    }

    /**
     * @param timeoutMs time in ms for how long to set the timeout
     */
    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public boolean isConnected() {
        if(ping()) {
            return true;
        }
        else {
            cancel();
            return false;
        }
    }

    private boolean ping() {
        int byteLength;
        try {
            write(new byte[]{-1});
            read(4);

            resetByteBuffer(4);
            byteLength = byteBuffer.put(buffer).getInt(0);
            sendAck();

            resetByteBuffer(byteLength);
            read(byteLength);
            sendAck();
        }
        catch(CommErrorException e) {
            return false;
        }
        return true;
    }

    /**
     * used to flush stream and close socket
     */
    public void cancel() {
        mainActivity.setConnectivity(false);
        running = false;
        interrupt();
        try {
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            socket.close();
        }
        catch(IOException e) {
            Log.e(TAG, "failed flush stream and close socket: ", e);
        }
    }
}
