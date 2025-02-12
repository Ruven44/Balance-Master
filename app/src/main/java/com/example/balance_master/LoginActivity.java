package com.example.balance_master;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String TAG = "LoginActivity";

    private Switch switchDailyNotification;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Get Switch UI Element
        switchDailyNotification = findViewById(R.id.switchDailyNotification);

        // Load saved switch state
        boolean isNotificationEnabled = sharedPreferences.getBoolean("DailyNotificationEnabled", false);
        switchDailyNotification.setChecked(isNotificationEnabled);

        // Handle Switch Toggle
        switchDailyNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("DailyNotificationEnabled", isChecked);
            editor.apply();

            if (isChecked) {
                Log.d(TAG, "Daily notifications enabled!");
                requestExactAlarmPermission();
                NotificationScheduler.scheduleDailyNotification(this);
            } else {
                Log.d(TAG, "Daily notifications disabled!");
                NotificationScheduler.cancelDailyNotification(this);
            }
        });

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

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e(TAG, "Exact alarm permission missing! Asking user...");
                Toast.makeText(this, "Please allow exact alarms in settings!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
}
