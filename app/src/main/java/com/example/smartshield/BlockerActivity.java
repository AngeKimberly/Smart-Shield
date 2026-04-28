package com.example.smartshield;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BlockerActivity extends AppCompatActivity {
    private EditText pinInput;
    private Button unlockButton;
    private TextView blockedAppText;
    private static final String CORRECT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocker);

        initViews();
        setupListeners();
    }

    private void initViews() {
        pinInput = findViewById(R.id.pin_input);
        unlockButton = findViewById(R.id.unlock_button);
        blockedAppText = findViewById(R.id.blocked_app_text);
        
        // Show blocked app message
        blockedAppText.setText("This app is blocked by SmartShield Parental Control");
    }

    private void setupListeners() {
        unlockButton.setOnClickListener(v -> attemptUnlock());
    }

    private void attemptUnlock() {
        String enteredPin = pinInput.getText().toString().trim();

        if (TextUtils.isEmpty(enteredPin)) {
            Toast.makeText(this, "Please enter PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredPin.equals(CORRECT_PIN)) {
            Toast.makeText(this, "Child Mode Deactivated", Toast.LENGTH_SHORT).show();
            
            // Stop WatchdogService
            Intent serviceIntent = new Intent(this, WatchdogService.class);
            stopService(serviceIntent);
            
            // Go to ParentDashboardActivity
            Intent intent = new Intent(this, ParentDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            pinInput.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        // Send user back to home screen instead of allowing them into the blocked app
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
