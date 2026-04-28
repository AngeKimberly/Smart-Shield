package com.example.smartshield;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class AppLockActivity extends AppCompatActivity {
    
    private static final String CORRECT_PIN = "1234";
    private EditText pinEditText;
    private Button unlockButton;
    private Button fingerprintButton;
    private TextView statusText;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        
        initViews();
        setupBiometric();
        setupPinInput();
        setupButtons();
    }
    
    private void initViews() {
        pinEditText = findViewById(R.id.pin_edit_text);
        unlockButton = findViewById(R.id.unlock_button);
        fingerprintButton = findViewById(R.id.fingerprint_button);
        statusText = findViewById(R.id.status_text);
    }
    
    private void setupBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(AppLockActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                statusText.setText("Authentication successful!");
                Toast.makeText(AppLockActivity.this, "Access granted", Toast.LENGTH_SHORT).show();
                finish(); // Close the lock screen
            }
            
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                statusText.setText("Authentication failed. Try again.");
                Toast.makeText(AppLockActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                statusText.setText("Authentication error: " + errString);
                Toast.makeText(AppLockActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }
        });
        
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("SmartShield Lock")
                .setSubtitle("Authenticate to access this app")
                .setNegativeButtonText("Use PIN")
                .build();
    }
    
    private void setupPinInput() {
        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable unlock button only when PIN is 4 digits
                unlockButton.setEnabled(s.length() == 4);
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupButtons() {
        // Unlock with PIN button
        unlockButton.setOnClickListener(v -> {
            String enteredPin = pinEditText.getText().toString();
            if (enteredPin.equals(CORRECT_PIN)) {
                statusText.setText("Access granted!");
                Toast.makeText(this, "PIN correct", Toast.LENGTH_SHORT).show();
                finish(); // Close the lock screen
            } else {
                statusText.setText("Incorrect PIN. Try again.");
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                pinEditText.setText("");
            }
        });
        
        // Fingerprint button
        fingerprintButton.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    biometricPrompt.authenticate(promptInfo);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    statusText.setText("No biometric features available on this device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    statusText.setText("Biometric features are currently unavailable.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    statusText.setText("No biometric credentials enrolled. Please set up fingerprint in device settings.");
                    // Prompt user to enroll biometrics
                    Intent enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivity(enrollIntent);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    statusText.setText("Security update required. Please update your device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                    statusText.setText("Biometric authentication not supported on this device.");
                    break;
                case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                    statusText.setText("Biometric status unknown. Please try again.");
                    break;
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        // Send user back to home screen instead of allowing them to bypass the lock
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
