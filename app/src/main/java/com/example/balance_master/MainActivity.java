package com.example.balance_master;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() started!");
        setContentView(R.layout.activity_login); // Load the login screen

        // Enable BootReceiver on app launch
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // Request notification permissions (Android 13+)
        requestNotificationPermission();

        // Manually trigger notification scheduling and log result
        Log.d(TAG, "Triggering NotificationScheduler...");
        NotificationScheduler.scheduleDailyNotification(this);
        Log.d(TAG, "NotificationScheduler should now be active!");

        // Test Button for Manual Notification
        Button testButton = findViewById(R.id.btnTestNotification);
        if (testButton != null) {
            testButton.setOnClickListener(v -> {
                Log.d(TAG, "Manual test notification triggered...");
                NotificationReceiver receiver1 = new NotificationReceiver();
                receiver1.sendNotification(MainActivity.this);
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
