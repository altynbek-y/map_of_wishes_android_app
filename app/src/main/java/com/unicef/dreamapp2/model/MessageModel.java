package com.unicef.dreamapp2.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
* @author Iman Augustine
 *
 * Chat model.
 *
 * */

public class MessageModel {
    // Global variables
    public String chatId = ""; // Chat id
    public String messageId; // Message id
    public String senderId; // Sender id
    public String message; // Message text
    public String messageType; // Message type
    public long timestamp; // Time stamp

    @Exclude
    public String localMediaUrl = "";

    @Exclude
    public int id = 0;

    @Exclude
    public long blockTime = 0;

    public MessageModel(){
    }

    public MessageModel(String senderId, String messageText){
        this.senderId = senderId; // Sender id
        this.message = messageText; // Message text
    }

    public Map<String, String> getTimestamp() { return ServerValue.TIMESTAMP; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Exclude
    public long getTimestampLong() { return timestamp; }

}
