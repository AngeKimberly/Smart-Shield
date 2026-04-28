package com.example.smartshield;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText pinInput;
    private Button loginButton;
    private static final String CORRECT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        pinInput = findViewById(R.id.pin_input);
        loginButton = findViewById(R.id.login_button);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String enteredPin = pinInput.getText().toString().trim();

        if (TextUtils.isEmpty(enteredPin)) {
            Toast.makeText(this, "Please enter PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredPin.equals(CORRECT_PIN)) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ParentDashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            pinInput.setText("");
        }
    }
}
