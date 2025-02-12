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
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String TAG = "LoginActivity";
    private TextView textViewReminder;
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

        // Reference UI elements
        textViewReminder = findViewById(R.id.textView_ReminderUsername);
        EditText editTextUsername = findViewById(R.id.editText_username);
        Button btnContinue = findViewById(R.id.button_continue);
        Button btnTestNotification = findViewById(R.id.btnTestNotification); // Test button

        // Debugging: Ensure textViewReminder is found
        if (textViewReminder == null) {
            Log.e(TAG, "textViewReminder is NULL! Check XML ID.");
        } else {
            Log.d(TAG, "textViewReminder found successfully.");
        }

        // Request Notification Permission (Android 13+)
        requestNotificationPermission();

        // Ensure button is always enabled but validate input on click
        btnContinue.setEnabled(true);
        btnContinue.setClickable(true);
        textViewReminder.setText("");
        textViewReminder.setVisibility(TextView.GONE);

        // Add text change listener to EditText
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewReminder.setVisibility(TextView.GONE); // Hide reminder when typing
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setOnClickListener(view -> {
            Log.d(TAG, "Continue button clicked!"); // Log when button is pressed

            String username = editTextUsername.getText().toString().trim();

            if (!username.isEmpty()) {
                Log.d(TAG, "Username entered: " + username);
                Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Username is EMPTY. Reminder should be visible.");

                textViewReminder.setText("Please enter a username!");
                textViewReminder.setVisibility(TextView.VISIBLE);

                // Check visibility status
                Log.d(TAG, "textViewReminder visibility after setting: " + textViewReminder.getVisibility());

                // Force UI refresh
                textViewReminder.post(() -> {
                    textViewReminder.invalidate();
                    textViewReminder.requestLayout();
                });

                // Debugging Log
                Log.d(TAG, "textViewReminder set to VISIBLE.");
            }
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
