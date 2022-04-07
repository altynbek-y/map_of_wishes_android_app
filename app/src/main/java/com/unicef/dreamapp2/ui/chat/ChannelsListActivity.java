package com.unicef.dreamapp2.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicef.dreamapp2.BaseInterface;
import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.adapter.ChannelAdapter;
import com.unicef.dreamapp2.application.MyPreferenceManager;
import com.unicef.dreamapp2.application.Utility;
import com.unicef.dreamapp2.model.ChannelModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Iman Augustine
 *
 * ChatListActivity.
 *
 * */

public class ChannelsListActivity extends AppCompatActivity {

    private final static String TAG = "ChatListActivity";

    // Global variables
    private DatabaseReference channelsDatabase; // Channels database
    private DatabaseReference customerDatabase; // Customer database
    private DatabaseReference volunteerDatabase; // Volunteer database
    // List elements
    private RecyclerView channelsList; // Channels list
    private ChannelAdapter channelAdapter; // Channels adapter
    // String
    private String customerId = null; // Customer ID
    private String volunteerId = null; // Volunteer ID
    private String chatterName = null; // Chatter name
    private String mUserType = null; // User role
    private String chatId = null; // Chat ID
    private String key = null; // Key
    private String userId = null; // User id
    // Shared preferences
    private SharedPreferences shared;

    // On creation of this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        try {
            // Initializes global variables
            initValues();

            // Initialize views
            initView();

            // Load list of channels
            loadChannels();

        } catch (Exception error) {
            Log.d(TAG, "onCreate: error: "+error.getLocalizedMessage());
        }
    }

    private void initValues() {
        // Shared preferences
        shared = MyPreferenceManager.getMySharedPreferences(this);

        // User type [Volunteer or regular]
        mUserType = shared.getString(MyPreferenceManager.USER_TYPE, null);

        // Current user's id
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Chat list database
        channelsDatabase = FirebaseDatabase.getInstance().getReference().child(Utility.USERS)
                .child(mUserType).child(userId).child(Utility.MESSAGES);

        // Regular users database
        customerDatabase = FirebaseDatabase.getInstance().getReference().child(Utility.USERS)
                .child(MyPreferenceManager.REGULAR_USER);

        // Volunteer users database
        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child(Utility.USERS)
                .child(MyPreferenceManager.VOLUNTEER);
    }

    // Initializes views
    private void initView() {
        try {
            // List initializations
            channelsList = findViewById(R.id.channelsList);
            channelAdapter = new ChannelAdapter(new ArrayList<ChannelModel>(), this, new BaseInterface.OnItemClickListener() {
                @Override
                public void onItemClick(Object object) {
                    startChatActivity((ChannelModel) object); // Launch chat
                }
            });
            channelsList.setLayoutManager(new LinearLayoutManager(this));
            channelsList.setAdapter(channelAdapter);
        } catch (Exception error) {
            Log.d(TAG, "initView: error: "+error.getLocalizedMessage());
        }
    }

    // Sets up listeners on list's items click
    private void setupListeners() {

    }

    // Load channels list
    private void loadChannels() {
        try {
            // Listen to value events
            channelsDatabase.addValueEventListener(new ValueEventListener() {
                // On data change
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        // Toast.makeText(ChatListActivity.this, "map:" + map.toString(), Toast.LENGTH_LONG).show();
                        extractChannels(snapshot); // Extract channel from Firebase database
                    }
                }

                // On cancel
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        } catch(Exception error) {
            Log.d(TAG, "loadChannels: error: "+error.getLocalizedMessage());
        }
    }

    // Extract channels
    private void extractChannels(DataSnapshot snapshot) {
        try {
            ArrayList<ChannelModel> list = new ArrayList<>(); // Channel list
            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue(); // Map of messages

            if (map != null) // If map is not null
            {
                HashMap<String, Object> channel;
                Object[] keySet = map.keySet().toArray(); // KeySet
                ChannelModel channelModel; // Channel model
                String key; // Key of the channel map

                // Loop each item in channel with a certain key
                for (Object o : keySet) {
                    key = o.toString(); // Key
                    channel = (HashMap<String, Object>) map.get(key); // Channel map
                    assert channel != null; // Make sure channel is not null
                    list.add(createChannel(channel)); // Create and add channel into the list
                }
                // Inflate the list with
                channelAdapter.setValues(list); // Setting values in the list
                channelAdapter.notifyDataSetChanged(); // Notify data change
            }
        } catch (Exception error) {
            Log.d(TAG, "extractChannels: error: "+error.getLocalizedMessage());
        }
    }

    // Create and inflates Channel with data
    private ChannelModel createChannel(HashMap<String, Object> channel) {
        try {
            ChannelModel channelModel = new ChannelModel(); // Create channel model
            // Chatter name
            channelModel.setChatterName(Objects.requireNonNull(channel.get(Utility.CHATTER_NAME)).toString());
            channelModel.setCustomerName(Objects.requireNonNull(channel.get(Utility.CUSTOMER_NAME)).toString());
            channelModel.setVolunteerName(Objects.requireNonNull(channel.get(Utility.VOLUNTEER_NAME)).toString());

            if (mUserType.equals(MyPreferenceManager.REGULAR_USER)) { // This is the user's chat, not a volunteer
                channelModel.setCustomerId(userId); // Customer id
                channelModel.setVolunteerId(Objects.requireNonNull(channel.get(Utility.CHATTER_ID)).toString()); // Chatter's id
            } else {
                channelModel.setCustomerId(Objects.requireNonNull(channel.get(Utility.CHATTER_ID)).toString()); // Chatter's id
                channelModel.setVolunteerId(userId); // Volunteer id
            }
            return channelModel; // return
        } catch(Exception error) {
            Log.d(TAG, "createChannel: error: "+error.getLocalizedMessage());
        }
        return null;
    }

    // Start chat with the chosen customer
    private void startChatActivity(ChannelModel channel) {
        try {
            Intent intent = new Intent(ChannelsListActivity.this, ChatActivity.class);
            intent.putExtra("chatterName", channel.getChatterName()); // Chatter name
            intent.putExtra("customerName", channel.getCustomerName()); // Customer name
            intent.putExtra("volunteerName", channel.getVolunteerName()); // Volunteer name
            intent.putExtra("customerId", channel.getCustomerId()); // Set customer id
            intent.putExtra("volunteerId", channel.getVolunteerId()); // Set volunteer id
            startActivity(intent);
            finish();
        } catch(Exception error) {
            Log.d(TAG, "startChatActivity: error: "+error.getLocalizedMessage());
        }
    }
}