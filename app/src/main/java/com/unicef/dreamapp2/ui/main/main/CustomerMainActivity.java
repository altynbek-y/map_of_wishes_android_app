package com.unicef.dreamapp2.ui.main.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.unicef.dreamapp2.ui.language.LanguageActivity;
import com.unicef.dreamapp2.ui.login.ProfileActivity;
import com.unicef.dreamapp2.ui.questions.QuesionActivity;
import com.unicef.dreamapp2.ui.rating.RatingListActivity;
import com.unicef.dreamapp2.ui.welcome.WelcomeActivity;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class CustomerMainActivity extends FragmentActivity implements OnMapReadyCallback {

    // Map related variables
    private GoogleMap mMap;
    private Location mLastLocation; // Location
    private LocationRequest mLocationRequest; // Location request

    private FusedLocationProviderClient mFusedLocationClient; // Fused location provider client

    // LatLng object
    private LatLng destinationLatLng;
    private LatLng pickupLocation;

    // Button
    private Button mLogout, mRequest, mSettings, mHistory;

    // Logging out boolean variable
    private Boolean isLoggingOut = false;
    private Boolean isCancellable = false;

    // What Marker is that?
    private Marker pickupMarker;

    // Map fragment
    private SupportMapFragment mapFragment;
    private DatabaseReference customerDatabase;

    // String
    private String requestService;
    private String mService;
    private String mUserType = null;
    private String customerId = "";

    // Profile image
    private CircleImageView mProfileImage;

    // TextView
    private TextView mDriverName;
    private TextView mDriverPhone;
    private TextView mDriverCommunity;
    private TextView userName;
    private TextView userType;

    // Rating bar
    private RadioGroup mRadioGroup;
    private RatingBar mRatingBar;

    // DrawerLayout and Navigation view
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Shared preferences
    private SharedPreferences shared = null;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfoDatabase;
    private DatabaseReference mHelpRequestDatabase;
    private String userID;

    // Marker options to show on the Google Map
    private MarkerOptions markerOptions;
    // Array lists
    private String[] list;
    private int[] icons = new int[]{ R.mipmap.medicine, R.mipmap.burger, R.mipmap.oxygen, R.mipmap.car, R.mipmap.sos };
    //------------------------------DRAWER-LAYOUT-NAVIGATION-LISTENER------------------------------------------------------

    // On navigation item selected listener in the drawer layout
    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new OnSingleClickNavigationViewListener() {
                @Override
                public boolean onSingleClick(MenuItem item) {
                    switch (item.getItemId()) {
                        // Chat
                        case R.id.chat:
                            startActivity(new Intent(CustomerMainActivity.this,
                                    ChannelsListActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                        case R.id.rating:
                            // Launch rating list activity
                            startActivity(new Intent(CustomerMainActivity.this,
                                    RatingListActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                            // Change profile
                        case R.id.change_profile:
                            startActivity(new Intent(CustomerMainActivity.this,
                                    ProfileActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                        // Questions and suggestions
                        case R.id.question:
                            startActivity(new Intent(CustomerMainActivity.this,
                                    QuesionActivity.class));
                            drawerLayout.closeDrawers();
                            break;
                        // Language activity
                        case R.id.language:
                            startActivity(new Intent(CustomerMainActivity.this, LanguageActivity.class));
                            drawerLayout.closeDrawers();
                            finish();
                            break;
                            // Psychological Help activity
                        case R.id.psychologicalhelp:
                            startActivity(new Intent(CustomerMainActivity.this, PsychologyHelp.class));
                            drawerLayout.closeDrawers();
                            break;
                            // Logout
                        case R.id.logout:
                            drawerLayout.closeDrawers();
                            logout();
                            break;
                    }
                    return true;
                }
            };
    //--------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();

        // Accessing String array xml
        list = getResources().getStringArray(R.array.help_array);
    }

    //--------------------------------------ON-CREATE-ACTIVITY------------------------------------------------------------------
    // On creation of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initializes views
        initView();

        // Initialized shared preferences
        shared = MyPreferenceManager.getMySharedPreferences(this);
        // User type
        mUserType = shared.getString(MyPreferenceManager.USER_TYPE, null);

        // Firebase realtime database
        mAuth = FirebaseAuth.getInstance();

        // User ID
        userID = mAuth.getCurrentUser().getUid();

        // Firebase databases
        mUserInfoDatabase = FirebaseDatabase.getInstance().getReference()
                .child(Utility.USERS)
                .child(mUserType)
                .child(userID);

        // Help requests database
        mHelpRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("HelpRequests");

        // Loads user info
        loadUserInfo();

        // Load user's help request location
        loadHelpLocation();

        // Set up listeners
        setupListener();
    }
    //--------------------------------------------------------------------------------------------------------------------------
    // Initializes views
    private void initView() {
        try {
            // Map fragment
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            // Sets onMapReadCallback that the activity implements
            mapFragment.getMapAsync(this);
            // Request help button
            mRequest = findViewById(R.id.request);
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
            userType.setText(getString(R.string.user_regular)); // Regular user
            // User image
            mProfileImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);

        } catch(NullPointerException error) {
            Log.d("CustomerMainActivity", "initView: "+error.getLocalizedMessage());
        }

    }
    // Sets up listener
    private void setupListener() {
        // On help request listener
        mRequest.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Help request is cancellable
                if (isCancellable) {
                    cancelRequest(); // Cancel request
                } else {
                    requestHelp(); // Request help
                }
            }
        });
    }
    //--------------------------------------------------------------------------------------------------------------------------
    private void setMarkerOption(String problemTitle, int id) {
        mMap.clear(); // Clears from marker options
        markerOptions.title(problemTitle).icon(BitmapDescriptorFactory.fromResource(icons[id])); // Sets title and marker icon
        mMap.addMarker(markerOptions); // Adds the created marker option to the map
    }
    //--------------------------------------------------------------------------------------------------------------------------
    // On help request button click
    private void requestHelp() {
        isCancellable = true;
        // Location latitude and longitude
        GeoFire geoFire = new GeoFire(mHelpRequestDatabase);
        geoFire.setLocation(userID, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        // LatLng object creation
        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // Creating a dialog list of help forms
        markerOptions = new MarkerOptions(); // Marker options
        markerOptions.position(pickupLocation); // Sets the location
        // Alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMainActivity.this);
        builder.setTitle(getString(R.string.need_help_alert_title));
        // Sets list items in the AlerDialog
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setMarkerOption(list[id], id); // Sets marker option
                mHelpRequestDatabase.child(userID).child("iconId").setValue(id); // Id of option marker
                mHelpRequestDatabase.child(userID).child("problem").setValue(list[id]); // Updates problem in Firebase database
                // Informs the user that the search for volunteers has begun
                mRequest.setText(getString(R.string.cancel_help_request));
            }
        });
        // Clear traces on cancellation
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancelRequest();
            }
        });
        builder.create().show();  // Create and show the alert dialog
    }

    // Cancels the help request
    private void cancelRequest() {
        mHelpRequestDatabase.child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mMap.clear(); //  Clear the map
                mRequest.setText(getString(R.string.need_help_request)); // Resetting text
                isCancellable = false; // Select a help type to cancel it again
            }
        });
    }

    // Loads user profile information from Firebase database
    // 1 - User name
    // 2 - User phone number
    // 3 - User problem ( or a regular user, not volunteer)
    private void loadUserInfo(){
        mUserInfoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        // Map data structure
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        // User name
                        if (map.get("name") != null) {
                            userName.setText(map.get("name").toString());
                        }
                        // User profile image URI
                        if (map.get("profileImageUrl") != null) {
                            // imageBase64 = map.get("profileImageUrl").toString();
                            Glide.with(getApplication()).load(Utility.getBitmapFromBase64(
                                    map.get("profileImageUrl").toString()))
                                    .into(mProfileImage);                        }
                    }
                } catch(NullPointerException error) {
                    Log.d("CustomerMainActivity", "onDataChange: error: "+error.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CustomerMainActivity", "onCancelled: error: "+databaseError.getMessage());
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------------------
    // Loads user's help location (lat, long)
    private void loadHelpLocation() {
        mHelpRequestDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            // On data change
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // If snapshot exists
                    if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                        isCancellable = true;
                        // Map data structure
                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        // Longitude and latitude
                        ArrayList<Double> list = (ArrayList<Double> )map.get("l"); // Gets list of latitude and longitude
                        pickupLocation = new LatLng(list.get(0), list.get(1)); // Latitude and longitude
                        MarkerOptions markerOptions = new MarkerOptions(); // Marker options
                        markerOptions.position(pickupLocation); // Sets the location
                        int id = Integer.parseInt(map.get("iconId").toString());
                        // Adds marker to the map
                        mMap.clear();
                        markerOptions.title(map.get("problem").toString()).icon(BitmapDescriptorFactory
                                .fromResource(icons[id]));
                        mMap.addMarker(markerOptions); // Adds the marker to the map
                        // There was help request so it can be cancelled now
                        mRequest.setText(getString(R.string.cancel_help_request));
                    }
                } catch(NullPointerException error) {
                    Log.d("CustomerMainActivity", "onDataChange (loadHelpLocation): error: "+error.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Cancelled
            }
        });
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
    private void logout() {
        isLoggingOut = true; // Logging out
        FirebaseAuth.getInstance().signOut(); // Signs out from Firebase
        startActivity(new Intent(this, WelcomeActivity.class)); // Returns to WelcomeActivity
        finish(); // Finishes the current activity
    }

    //-------------------------------------------GOOGLE-MAP---------------------------------------------------------------------------------
        /*-------------------------------------------- Map specific functions -----
        |  Function(s) getDriverLocation
        |
        |  Purpose:  Get's most updated driver location and it's always checking for movements.
        |
        |  Note:
        |	   Even tho we used geofire to push the location of the driver we can use a normal
        |      Listener to get it's location with no problem.
        |
        |      0 -> Latitude
        |      1 -> Longitudde
        |
        *-------------------------------------------------------------------*/


    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // On map ready sets the global Map variable
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Version checking and granting permission to use location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }
    // Check location permission
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(CustomerMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    // Location callback
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;
                    // Creating LarLng
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    // Points to the user location on the planet
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
            }
        }
    };
    //-----------------------------------------------------------------------------------------------------------------------------------
    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    }
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------