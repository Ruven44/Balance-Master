package com.example.balance_master;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Request Notification Permission (Android 13+)
        requestNotificationPermission();

        // Reference UI elements
        EditText editTextUsername = findViewById(R.id.editText_username);
        Button btnContinue = findViewById(R.id.button_continue);
        Button btnTestNotification = findViewById(R.id.btnTestNotification); // Test button

        // Initially disable the button
        btnContinue.setEnabled(false);

        // Add text change listener to EditText
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable button only if text is not empty
                btnContinue.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click listener for the Continue button
        btnContinue.setOnClickListener(view -> {
            // Get username input
            String username = editTextUsername.getText().toString().trim();

            // Proceed only if username is valid
            if (!username.isEmpty()) {
                // Navigate to GameActivity
                Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);
                finish(); // Close LoginActivity so user can't go back to it
            }
        });

        // Click listener for Test Notification button
        btnTestNotification.setOnClickListener(view -> {
            Log.d(TAG, "Test notification button pressed.");
            NotificationReceiver receiver = new NotificationReceiver();
            receiver.sendNotification(LoginActivity.this);
        });
    }

    // Requests notification permission for Android 13+
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting notification permissions...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            } else {
                Log.d(TAG, "Notification permissions already granted.");
            }
        }
    }
}
