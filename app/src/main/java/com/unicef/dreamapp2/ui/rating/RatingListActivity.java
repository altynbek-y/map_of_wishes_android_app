package com.unicef.dreamapp2.ui.rating;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicef.dreamapp2.BaseInterface;
import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.adapter.ChannelAdapter;
import com.unicef.dreamapp2.adapter.RatingAdapter;
import com.unicef.dreamapp2.application.MyPreferenceManager;
import com.unicef.dreamapp2.application.Utility;
import com.unicef.dreamapp2.model.ChannelModel;
import com.unicef.dreamapp2.model.RatingModel;
import com.unicef.dreamapp2.ui.chat.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Iman Augustine
 *
 * ChatListActivity.
 *
 * */

public class RatingListActivity extends AppCompatActivity {

    private final static String TAG = "ChatListActivity";

    // Global variables
    private DatabaseReference volunteerDatabase; // Volunteer database
    // List elements
    private RecyclerView ratingList; // Channels list
    private RatingAdapter ratingAdapter; // Channels adapter
    // String
    private String mUserType = null; // User role
    private String userId = null; // User id
    // Shared preferences
    private SharedPreferences shared;

    // On creation of this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        // Initializes global variables
        initValues();

        // Initialize views
        initView();

        // Sets up listener
        setupListener();
    }

    private void initValues() {
        // Shared preferences
        shared = MyPreferenceManager.getMySharedPreferences(this);

        // Volunteer users database
        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child(Utility.USERS)
                .child(MyPreferenceManager.VOLUNTEER);
    }

    // Initializes views
    private void initView() {
        // List initializations
        ratingList = findViewById(R.id.ratingsList); // Get ratings list
        ratingAdapter = new RatingAdapter(new ArrayList<RatingModel>(), this, null); // Rating adapter
        ratingList.setLayoutManager(new LinearLayoutManager(this)); // Rendering as linear list
        ratingList.setAdapter(ratingAdapter); // Set adapter
    }
    
    // Set up listener
    private void setupListener() {
        // Adds value event listener
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            // On data change
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                extractRating(snapshot); // Extracts
            }
            // On cancelled
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: error: "+error.getMessage());
            }
        });
    }

    // Extract channels
    private void extractRating(DataSnapshot snapshot) {
        ArrayList<RatingModel> ratingList = new ArrayList<>(); // Channel list
        HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue(); // Map of messages

        if(map!=null) // If map is not null
        {
            HashMap<String, Object> ratingMap;
            Object[] keySet = map.keySet().toArray(); // KeySet
            ChannelModel channelModel; // Channel model
            String key; // Key of the channel map

            // Loop each item in channel with a certain key
            for (Object o : keySet) {
                key = o.toString(); // Key
                ratingMap = (HashMap<String, Object>) map.get(key); // Channel map
                assert ratingMap != null; // Make sure channel is not null
                ratingList.add(createRating(ratingMap)); // Create and add channel into the list
            }
            // Inflate the list with
            ratingAdapter.setValues(ratingList); // Setting values in the list
            ratingAdapter.notifyDataSetChanged(); // Notify data change
        }
    }

    // Create and inflates Channel with data
    private RatingModel createRating(HashMap<String, Object> ratingMap) {
        RatingModel ratingModel = new RatingModel(); // Create channel model
        ratingModel.setVolunteerName(Objects.requireNonNull(ratingMap.get(Utility.NAME)).toString());
        ratingModel.setLikes(Integer.parseInt(Objects.requireNonNull(ratingMap.get(Utility.LIKES)).toString()));
        return ratingModel; // return
    }

//    int likes = Integer.parseInt(Objects.requireNonNull(channel.get(Utility.LIKES)).toString());
//    Toast.makeText(this, "likes: "+likes, Toast.LENGTH_SHORT).show();

}