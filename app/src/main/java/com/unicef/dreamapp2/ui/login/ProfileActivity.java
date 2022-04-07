package com.unicef.dreamapp2.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicef.dreamapp2.application.MyPreferenceManager;
import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.application.Utility;
import com.unicef.dreamapp2.ui.main.main.CustomerMainActivity;
import com.unicef.dreamapp2.ui.main.main.VolunteerMainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Tan Ton
 *
 * */

public class ProfileActivity extends AppCompatActivity {

    // Request code
    private static final int GALLERY_RQUEST_CODE = 0;

    // Global variables
    // EditText
    private EditText mNameField;
    private EditText mPhoneField;
    private EditText mProblemField;

    // Buttons
    private Button mBackBtn;
    private Button mSaveBtn;

    // ImageView
   // private ImageView mProfileImage;
    private CircleImageView mProfileImage;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    // String variables
    private String userID = null; // UID
    private String mName = null; // User name
    private String mPhone = null; // User phone
    private String imageBase64 = null; // USer image base64
    private String mProblem = null; // User's problem
    private String mUserType = null; // User type
    private String phone; // Phone
    private int likes = 0; // Number of likes

    // Image URI
    private Uri resultUri;

    // Shared preferences and editor
    private SharedPreferences shared = null;
    private SharedPreferences.Editor editor = null;

    // Boolean
    private boolean isSetup = false;

    // On creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            // Initialized shared preferences
            shared = MyPreferenceManager.getMySharedPreferences(this);
            mUserType = shared.getString(MyPreferenceManager.USER_TYPE, null);

            // This is the first time launch, therefore it should behave a bit differently
            isSetup = getIntent().getBooleanExtra("setup", false);

            // Getting phone number from the previous step
            phone = getIntent().getStringExtra("mobile");

            // Initializes views
            initView();

            // Firebase realtime database
            mAuth = FirebaseAuth.getInstance();

            // User ID
            userID = mAuth.getCurrentUser().getUid();

            // Firebase database
            mCustomerDatabase = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Utility.USERS)
                    .child(Objects.requireNonNull(shared.getString(MyPreferenceManager.USER_TYPE, null)))
                    .child(userID);

            // Loads user info from Firebase database
            loadUserInfo();

            // In case NullPointerException is thrown
        } catch(NullPointerException error) {
            Log.d("AccountSetupActivity", "onDataChange: error: "+error.getLocalizedMessage());
        }
    }

    // Initializes widgets
    private void initView() {
        mNameField = findViewById(R.id.nameEditText); // User name
        mPhoneField = findViewById(R.id.phoneEditText); // User phone number
        mProblemField = findViewById(R.id.problemEditText); // User problem
        mProfileImage = findViewById(R.id.profilePicture); // User profile image
        mBackBtn = findViewById(R.id.backButton); // Back button

        // The volunteer does not need to tell his problem
        // He is supposed to help solve others' problems!
        if(mUserType.equals(MyPreferenceManager.VOLUNTEER)) {
            mProblemField.setEnabled(false);
        }
    }

    // Opens gallery for selection
    public void selectPictureFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_RQUEST_CODE);
    }

    // Image returned from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_RQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            resultUri = data.getData(); // ImageU URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                imageBase64 = Utility.getBase64FromBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(getApplication()).load(resultUri).into(mProfileImage); // Showing the profile image
        }
    }

    // Loads user profile information from Firebase database
    // 1 - User name
    // 2 - User phone number
    // 3 - User problem ( or a regular user, not volunteer)
    private void loadUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        // Map data structure
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        // User name
                        if (map.get("name") != null) { // Of name is not empty
                            mName = map.get("name").toString();
                            mNameField.setText(mName);
                        }
                        // User phone
                        if (map.get("phone") != null) { // If phone is not empty
                            mPhone = map.get("phone").toString();
                            mPhoneField.setText(mPhone);
                        } else {
                            mPhoneField.setText(phone);
                        }
                        // User problem
                        if (map.get("problem") != null) { // If problem is not empty
                            mProblem = map.get("problem").toString();
                            mProblemField.setText(mProblem);
                        }
                        // User profile image URI
                        if (map.get("profileImageUrl") != null) { // if is not empty
                            imageBase64 = map.get("profileImageUrl").toString();
                            Glide.with(getApplication()).load(Utility.getBitmapFromBase64(imageBase64))
                                    .into(mProfileImage);
                        }
                        // User's number of likes (for volunteer)
                        if (map.get(Utility.LIKES) != null) { // if is not empty
                            likes = Integer.parseInt(Objects.requireNonNull(map.get(Utility.LIKES)).toString());
                        }
                    }
                } catch(NullPointerException error) {
                    Log.d("AccountSetupActivity", "onDataChange: error: "+error.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Cancelled
                Toast.makeText(ProfileActivity.this, "Произошла ошибка.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Saves user information to the Firebase database
    public void saveUserInformation(View view) {

        boolean isValid = true;
        mName = mNameField.getText().toString(); // Get the entered name
        mPhone = mPhoneField.getText().toString(); // Get the entered phone
        mProblem = mProblemField.getText().toString(); // Get the entered problem (for non-volunteers)

        // Name is empty
        if(mName.isEmpty()) {
            mNameField.setError("Введите имя");
            isValid = false;
        }

        // Phone is empty
        if(mPhone.isEmpty()) {
            mPhoneField.setError("Введите номер");
            isValid = false;
        }

        // Map data structure to be uploaded
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("problem", mProblem);
        userInfo.put("likes", likes);

        // Saving path to the profile image
        if(imageBase64!=null) {
            userInfo.put("profileImageUrl", imageBase64);
        }

        // If entered data is valid
        if (isValid) {
            // Updates children of the user with some id in Users
            mCustomerDatabase.updateChildren(userInfo);
            if (isSetup) {  // If it is the first time setup
                startMainActivity();
            }
            // Finishes the current activity
            finish();
        }
    }

    // Starts the proper activity according to the user's type
    private void startMainActivity() {
        if (mUserType.equals(MyPreferenceManager.REGULAR_USER)) {
            startActivity(new Intent(this, CustomerMainActivity.class));
        } else {
            startActivity(new Intent(this, VolunteerMainActivity.class));
        }
    }

    // On back button pressed
    public void backButton(View view) {
        finish();
    }
}
