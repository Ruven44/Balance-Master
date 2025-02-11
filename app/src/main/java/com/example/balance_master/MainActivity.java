package com.example.balance_master;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() started!");
        setContentView(R.layout.activity_login); // Load the login screen

        // Request notification permissions (Android 13+)
        requestNotificationPermission();

        // Start the notification scheduler
        Log.d(TAG, "Starting NotificationScheduler...");
        NotificationScheduler.scheduleDailyNotification(this);
        Log.d(TAG, "NotificationScheduler initialized!");

        // Test Button for Manual Notification
        Button testButton = findViewById(R.id.btnTestNotification);
        if (testButton != null) {
            testButton.setOnClickListener(v -> {
                Log.d(TAG, "Manual test notification triggered...");
                NotificationReceiver receiver = new NotificationReceiver();
                receiver.sendNotification(MainActivity.this);
            });
        }
    }

    // Requests notification permission for Android 13+
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting notification permissions...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                Log.d(TAG, "Notification permissions already granted.");
            }
        }
    }
}
