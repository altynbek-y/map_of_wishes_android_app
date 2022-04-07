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

public class ChannelModel {
    // Global variables
    public String chatterName = null; // Chat name
    public String lastMessage = null; // Message id
    public String customerId = null; // Customer id
    public String volunteerId = null; // Volunteer id
    public String customerName = null; // Customer id
    public String volunteerName = null; // Volunteer id
    public int likes = 0;

    @Exclude
    public String localMediaUrl = "";

    @Exclude
    public int id = 0;

    @Exclude
    public long blockTime = 0;

    // Constructor
    public ChannelModel(){ }

    public ChannelModel(String name, String lastMessage){
        this.chatterName = name;
        this.lastMessage = lastMessage;
    }

    public String getChatterName() {
        return chatterName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setChatterName(String chatterName) {
        this.chatterName = chatterName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getLikes() {
        return likes;
    }
}
