package com.scouting_app_template.Bluetooth;

/**
 * thrown when an error occurs in the middle of communication
 */
public class CommErrorException extends Exception{

    public CommErrorException() {

    }
    public CommErrorException(String message) {
        super(message);
    }
}
