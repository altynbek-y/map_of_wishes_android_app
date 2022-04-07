package com.unicef.dreamapp2.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.unicef.dreamapp2.application.MyPreferenceManager;
import com.unicef.dreamapp2.R;

import java.util.concurrent.TimeUnit;

/**
* @author Iman Augustine
 *
* */

public class CodeActivity extends AppCompatActivity {

    // TAG
    private String TAG = "CodeActivity";

    // Global variables
    private EditText codeEditText;
    private Button confirmBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private String mVerificationId; // Verification id
    private String phone; // Phone number
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private SharedPreferences shared = null;
    private SharedPreferences.Editor editor = null;

    // PhoneAuthProvider.OnVerificationChangedCallbacks interface initialized
    // 1 - onVerificationCompleted - proceed to the main activity
    // 2 - onCodeSent - code is sent the phone number, should be entered manually
    // 3 - onVerificationFailed - on phone verification failed
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            // Toast.makeText(CodeActivity.this, "Verification completed!", Toast.LENGTH_SHORT).show();

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            String code = credential.getSmsCode();
            if(code!=null) {
                // Setting code in the code edit text
                codeEditText.setText(code);
                // verifying the code
                verifyVerificationCode(code);
            }

            // signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            // Toast.makeText(CodeActivity.this, "Code sent!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // Verification failed due to wrong configurations with Firebase
            Log.d(TAG, "onVerificationFailed:" + e.getLocalizedMessage());
            Toast.makeText(CodeActivity.this, getString(R.string.otp_error_occurred), Toast.LENGTH_LONG).show();
            // finish();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);

            Toast.makeText(CodeActivity.this, "Code retrieval timeout! Try again!", Toast.LENGTH_LONG).show();
        }
    };

    // On creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        // Initialize views
        initView();

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();

        // Phone number from the previous step (Enter number)
        phone = getIntent().getStringExtra("mobile");

        // Send verification code to the given number
        sendVerificationCode(phone);

        // Shared preferences
        shared = MyPreferenceManager.getMySharedPreferences(this);
        editor = shared.edit();
    }

    // Initializes views
    private void initView() {
        codeEditText = findViewById(R.id.codeEditText); // Edit text code
        confirmBtn = findViewById(R.id.confirmButton); // Confirm button
    }

    // On confirm click
    public void onConfirmClick(View view) {

        // Get the received OTP
        String code = codeEditText.getText().toString().trim();

        // If code is empty or code's length is less than 6
        if (code.isEmpty() || code.length()<6) {
            // Show error
            codeEditText.setError(getString(R.string.enter_valid_code));
            codeEditText.requestFocus();
            return;
        }

        // Verify the code entered manually
        verifyVerificationCode(code);
    }

    // Verify verification code
    private void verifyVerificationCode(String otp) {
        // Creating the credential
        PhoneAuthCredential credential;

        if(mVerificationId!=null) {
            credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            // Signing the user
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(CodeActivity.this, getString(R.string.otp_error_occurred), Toast.LENGTH_LONG).show();
        }
    }

    // Sending verification code
    private void sendVerificationCode(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, 60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    // Resending OTP code
    public void resendVerificationCode(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,                 // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,      // Unit of timeout
                this,          // Activity (for callback binding)
                mCallbacks,            // OnVerificationStateChangedCallbacks
                mResendToken);         // ForceResendingToken from callbacks
    }

    // Sign in with the credential sent in onVerificationCompleted
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CodeActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("mobile", phone);
                            intent.putExtra("setup", true);
                            startActivity(intent);
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                codeEditText.setError(getString(R.string.enter_valid_code));
                            }
                        }
                    }
                });
    }
}



