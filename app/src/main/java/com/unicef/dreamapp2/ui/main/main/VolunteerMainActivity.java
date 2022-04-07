package com.unicef.dreamapp2.ui.main.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicef.dreamapp2.ui.psychology.PsychologyHelp;
import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.application.MyPreferenceManager;
import com.unicef.dreamapp2.application.Utility;
import com.unicef.dreamapp2.singleclicklistener.OnSingleClickListener;
import com.unicef.dreamapp2.singleclicklistener.OnSingleClickNavigationViewListener;
import com.unicef.dreamapp2.ui.chat.ChannelsListActivity;
import com.unicef.dreamapp2.ui.chat.ChatActivity;
import com.unicef.dreamapp2.ui.language.LanguageActivity;
import com.unicef.dreamapp2.ui.login.ProfileActivity;
import com.unicef.dreamapp2.ui.questions.QuesionActivity;
import com.unicef.dreamapp2.ui.rating.RatingListActivity;
import com.unicef.dreamapp2.ui.welcome.WelcomeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * @author Iman Augustine
 *
 * VolunteerMainActivity extends FragmentActivity.
 *
 * */

public class VolunteerMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "VolunteerMainActivity";
    // Global variables
    private GoogleMap mMap; // Google map
    private SupportMapFragment mapFragment; // Map fragment
    private FusedLocationProviderClient mFusedLocationClient; // Fused location provider client
    private LatLng pickupLatLng; // Pick LatLng
    private Location mLastLocation; // Geo location
    private LocationRequest mLocationRequest; // Location reqquest
    // Switch
    private SwitchCompat switchHeroMode;
    // Status
    private int status = 0;
    // String variables
    private String customerName = null;
    private String volunteerName = null;
    private String userProblemStr = null;
    private String userPhoneStr = null;
    private String mUserType = null;
    private String userId = null;
    private String customerId = null;
    private String imageBase64 = null;
    // Profile image
    private CircleImageView mProfileImage;
    // Logging out
    private Boolean isShowingBottom = false;
    // Layout
    private LinearLayout mCustomerInfo;
    // TextView
    private ImageView mCustomerProfileImage;
    private TextView mCustomerName;
    private TextView mCustomerPhone;
    private TextView mCustomerProblem;
    private TextView userName;
    private TextView userType;
    // DrawerLayout and Navigation view
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    // Shared preferences
    private SharedPreferences shared = null;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfoDatabase;
    private DatabaseReference mCustomerInfoDatabase;
    private DatabaseReference mHelpRequestDatabase;
    // Array lists
    private String[] list;
    // Bottom sheet dialog
    private BottomSheetDialog dialog;

    private int[] icons = new int[]{ R.mipmap.medicine, R.mipmap.burger, R.mipmap.oxygen, R.mipmap.car, R.mipmap.sos };
    // Marker map
    private Map<Marker, Object> markersMap = new HashMap<>();
    // Value event listener
    private ValueEventListener volunteerEventListener;
    private ValueEventListener customerEventListener;

    // On navigation item selected listener in the drawer layout
    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new OnSingleClickNavigationViewListener() {
                @Override
                public boolean onSingleClick(MenuItem item) {
                    switch (item.getItemId()) {
                        // Chat
                        case R.id.chat:
                            // Launch chat list activity
                            startActivity(new Intent(VolunteerMainActivity.this,
                                    ChannelsListActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                        case R.id.rating:
                            // Launch rating list activity
                            startActivity(new Intent(VolunteerMainActivity.this,
                                    RatingListActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                            // Edit profile
                        case R.id.change_profile:
                            // Profile activity
                            startActivity(new Intent(VolunteerMainActivity.this,
                                    ProfileActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                        case R.id.question:
                            // Questions and suggestions
                            startActivity(new Intent(VolunteerMainActivity.this, QuesionActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                            // Language activity
                        case R.id.language:
                            // Questions and suggestions
                            startActivity(new Intent(VolunteerMainActivity.this, LanguageActivity.class));
                            drawerLayout.closeDrawers();
                            finish();
                            break;
                        // Psychological Help activity
                        case R.id.psychologicalhelp:
                            startActivity(new Intent(VolunteerMainActivity.this, PsychologyHelp.class));
                            drawerLayout.closeDrawers();
                            break;
                            // Logs out
                        case R.id.logout:
                            drawerLayout.closeDrawers();
                            logout();
                            break;
                    }
                    return true;
                }
            };
    //------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();

        // Accessing String array xml
        list = getResources().getStringArray(R.array.help_array);

        // Initialize customer event value listener
        customerEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    // Map data structure
                    try {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        customerName = map.get("name").toString(); // Getting user name
                        userProblemStr = map.get("problem").toString(); // Getting user's problem
                        userPhoneStr = map.get("phone").toString(); // Getting user's phone
                        imageBase64 = map.get("profileImageUrl").toString(); // Image base 64 format
                        if(isShowingBottom) { showBottomSheetDialog(); }// Shows the above accessed information as a bottom sheet dialog
                    } catch(Exception error) {
                        Log.d(TAG, "loadCustomerInformation, error: "+error.getLocalizedMessage());
                    }
                }
                // Null pointer exception thrown
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Cancelled
            }
        };
    }

    @Override
    protected void onPause() {
//        if(volunteerEventListener!=null && mUserInfoDatabase!=null) {
//            mUserInfoDatabase.removeEventListener(volunteerEventListener);
//        }
        if(customerEventListener!=null && mCustomerInfoDatabase!=null) {
            mCustomerInfoDatabase.removeEventListener(customerEventListener);
        }
        super.onPause();
    }
    //--------------------------------------------------ON-CREATE-ACTIVITY----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_main);

        try {
            // Initializes views
            initView();
            // Shared preferences
            shared = MyPreferenceManager.getMySharedPreferences(this);
            // User type
            mUserType = shared.getString(MyPreferenceManager.USER_TYPE, null);
            // Firebase realtime database
            mAuth = FirebaseAuth.getInstance();
            // User ID
            userId = mAuth.getCurrentUser().getUid();
            // Firebase database, user info database
            mUserInfoDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(mUserType)
                    .child(userId);
            // Firebase database, help requests database
            mHelpRequestDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child("HelpRequests");
            // Firebase database, regular users database
            mCustomerInfoDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(MyPreferenceManager.REGULAR_USER);
            // Loads volunteer's information
            loadVolunteerInfo();
            // Fused location client initialized
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // Caught some error
        } catch(Exception error) {
            Log.d(TAG, "onCreate, error: "+error.getLocalizedMessage()); // Print the error message
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------
    private void initValues() {

    }
    //-----------------------------------------------------------------------------------------------------------------------
    private void initView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Setting on map ready callback interface
        mapFragment.getMapAsync(this);
        // Switch widget
        switchHeroMode = findViewById(R.id.switchHeroMode);
        // Setting set on checked listener
        switchHeroMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // Already checked
                    connectDriver(); // Connect
                } else {
                    disconnectDriver(); // Disconnect
                }
            }
        });
        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer);
        // Navigation view
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        // TextView
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        // User type (Regular or volunteer)
        userType = navigationView.getHeaderView(0).findViewById(R.id.userType);
        // Sets text that shows who the user is: regular or volunteer
        userType.setText(getString(R.string.user_volunteer)); // Volunteer user
        // User image
        mProfileImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    /**
     *  Showing help requester information as bottom sheet dialog
     * */
    private void showBottomSheetDialog() {
        // Root view
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null); // Inflates layout_bottom
        // TextView
        ((TextView)view.findViewById(R.id.nameTextView)).setText(customerName); // User name
        ((TextView)view.findViewById(R.id.problemTextView)).setText(userProblemStr); // User's problem
        ((TextView)view.findViewById(R.id.phoneTextView)).setText(userPhoneStr); // User's phone
        Glide.with(getApplication())
                .load(Utility.getBitmapFromBase64(imageBase64))
                .into(((CircleImageView)view.findViewById(R.id.profilePicture))); // Setting user profile image
        // Help button
        Button help = view.findViewById(R.id.helpButton);
        dialog = new BottomSheetDialog(this); // Creates bottom sheet dialog
        dialog.setContentView(view); // Sets content
        // On dialog dismiss listener
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface dialogInterface) { }});
        dialog.show(); // Shows user information dialog
        // Sets on click listener
        help.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                launchChatActivity(help); // Starts chat activity
            }
        });
        // Call on click
        view.findViewById(R.id.call).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                makeCall(view.findViewById(R.id.call)); // Call
            }
        });
        // Send a message in WhatsApp
        view.findViewById(R.id.whatsapp).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                sendWhatsApp(view.findViewById(R.id.whatsapp)); // Launch WhatsApp
            }
        });
        // Send a message in Telegram
        view.findViewById(R.id.telegram).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                sendTelegram(); // Launch Telegram
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------------------------
    private void makeCall(View view) {
        // Request a permission to make a call
        new MaterialTapTargetPrompt.Builder(VolunteerMainActivity.this).setTarget(view)
                .setPrimaryText(getString(R.string.call_by_phone_text)).setAutoFinish(true)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        // User has pressed the prompt target
                        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(VolunteerMainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                        } else {
                            // Make a call
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userPhoneStr)));
                        }
                    }
                }).show();
        dialog.dismiss();
    }
    //-----------------------------------------------------------------------------------------------------------------------
    private void sendWhatsApp(View view) {
        new MaterialTapTargetPrompt.Builder(VolunteerMainActivity.this)
                .setTarget(view).setAutoFinish(true)
                .setPrimaryText(getString(R.string.send_whatsapp_text))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        // User has pressed the prompt target
                        String url = "https://api.whatsapp.com/send?phone="+userPhoneStr;
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                }).show();
        dialog.dismiss();
    }
    // Starts chat activity
    private void launchChatActivity(View view) {
        // Tutorial
        new MaterialTapTargetPrompt.Builder(VolunteerMainActivity.this)
                .setTarget(view).setAutoFinish(true)
                .setPrimaryText(getString(R.string.goto_chat_help))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        // User has pressed the prompt target
                        if(!customerId.equals(userId)) { // If it is not the same user
                            startChatActivity(customerId); // Launches activity
                        } else {
                            // Prompt to the user that this is himself
                            Toast.makeText(VolunteerMainActivity.this,
                                    getString(R.string.this_is_you_warning),
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        isShowingBottom = false;
                    }
                }).show();
        dialog.dismiss();
    }
    //-----------------------------------------------------------------------------------------------------------------------
    private void sendTelegram() {
        Intent LaunchIntent=getPackageManager().getLaunchIntentForPackage("org.telegram.messenger");
        startActivity(LaunchIntent);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Log out
    private void logout() {
        disconnectDriver(); // Disconnects drivers
        FirebaseAuth.getInstance().signOut(); // Signs out from Firebase
        startActivity(new Intent(VolunteerMainActivity.this, WelcomeActivity.class)); // Starts WelcomeActivity
        finish(); // Finishes the current activity
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Loads user profile information from Firebase database
    // 1 - User name
    // 2 - User phone number
    // 3 - User problem ( or a regular user, not volunteer)
    private void loadVolunteerInfo(){
        // Sets value event listener on the database
        mUserInfoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        // Map data structure
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        // User name
                        if (map.get("name") != null) {
                            volunteerName = map.get("name").toString();
                            userName.setText(volunteerName);
                        }
                        // User profile image URI
                        if (map.get("profileImageUrl") != null) {
                            Glide.with(getApplication()).load(Utility.getBitmapFromBase64(
                                    map.get("profileImageUrl").toString()))
                                    .into(mProfileImage);
                        }
                    }
                    // Null pointer exception thrown
                } catch(NullPointerException error) {
                    Log.d("AccountSetupActivity", "onDataChange: error: "+error.getLocalizedMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Cancelled
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Get the assigned customer
    private void loadCustomerInformation(String uid) {
        try {
            // Loads regular information from database
            mCustomerInfoDatabase.child(uid).addValueEventListener(customerEventListener);
        } catch(Exception error) {
            Log.d(TAG, "loadCustomerInformation, error: "+error.getLocalizedMessage());
        }
    }
    // Start chat with the chosen customer
    private void startChatActivity(String uid) {
        Intent intent = new Intent(VolunteerMainActivity.this, ChatActivity.class);
        intent.putExtra("chatterName", customerName); // Customer name
        intent.putExtra("customerName", customerName); // Customer name
        intent.putExtra("volunteerName", volunteerName); // Volunteer name
        intent.putExtra("customerId", customerId); // Customer id
        intent.putExtra("volunteerId", userId); // Volunteer id
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Sets on marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Loads the selected user's information
                customerId = Objects.requireNonNull(markersMap.get(marker)).toString();
                loadCustomerInformation(customerId); // Passes customer ID to load profile information
                isShowingBottom = true;
                return true;
            }
        });
        // Request user's location with high accuracy
        mLocationRequest = new LocationRequest(); // Location request
        mLocationRequest.setInterval(1000); // Interval
        mLocationRequest.setFastestInterval(1000); // Fastest interval
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // High accuracy
        // Load people requesting help
        mHelpRequestDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            // On data change
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterates through all child snapshots
                for(DataSnapshot child : snapshot.getChildren()) {
                    // Adds all of help markers to the map
                    addHelpRequestMarkers(child.getKey(), (Map<String, Object>) child.getValue());
                }
            }
            // On cancellation
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Cancelled
            }
        });

        // Requests permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            }
        }
    }

    // Marks all places where people have requested help
    private void addHelpRequestMarkers(String uid, Map<String, Object> map) {
        // Gets icon id from map and converts to integer
        try {
            int id = Integer.parseInt(map.get("iconId").toString());
            // Gets list of geo location [lat, long]
            ArrayList<Double> geo = (ArrayList<Double>) map.get("l");
            // Creating marker options object
            MarkerOptions markerOptions = new MarkerOptions();
            // Sets problem title
            markerOptions.title(map.get("problem").toString());
            // Latitude and longitude
            markerOptions.position(new LatLng(geo.get(0), geo.get(1)));
            // Sets marker icon by the problem type
            Marker marker = mMap.addMarker(markerOptions);
            // Setting marker icon
            marker.setIcon(BitmapDescriptorFactory.fromResource(icons[id]));
            // Puts marker in the hash map for later use of user id
            markersMap.put(marker, uid);
        } catch(Exception ignored)
        {

        }
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Location call backs interface listener
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;
                }

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        }
    };
    //-----------------------------------------------------------------------------------------------------------------------
    // Check geo location permission
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(VolunteerMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    // On request permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case 1: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        } catch(Exception error) {
            Log.d(TAG, "onRequestPermissionsResult, error: "+error.getLocalizedMessage());
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------
    // Connects driver
    private void connectDriver() {
        try {
            if(mMap!=null) {
                checkLocationPermission();
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        } catch(Exception error) {
            Log.d(TAG, "connectDriver: "+error);
        }
    }
    // Disconnects driver
    private void disconnectDriver() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

}
//-----------------------------------------------------------------------------------------------------------------------
