package com.example.balance_master;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log; // Import für Logging
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // Log-Tag für bessere Übersicht

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DEBUG", "onCreate() wurde gestartet!"); // Wichtiger Test-Log
        setContentView(R.layout.activity_login);
        Log.e("DEBUG", "activity_login.xml wurde als Layout gesetzt!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Jetzt wird das Login-Layout geladen!
        Log.d(TAG, "activity_login.xml wurde als Layout gesetzt!");
        Log.d(TAG, "onCreate: MainActivity gestartet!");

        // Berechtigungen für Benachrichtigungen (Android 13+)
        requestNotificationPermission();

        // Starte den Notification Scheduler
        Log.d(TAG, "Starte den NotificationScheduler...");
        NotificationScheduler.scheduleDailyNotification(this);
        Log.d(TAG, "NotificationScheduler wurde aufgerufen!");

        // TESTBUTTON für manuelle Benachrichtigung (aus activity_login.xml)
        Button testButton = findViewById(R.id.btnTestNotification);
        if (testButton == null) {
            Log.e(TAG, "Fehler: btnTestNotification wurde nicht gefunden!");
        } else {
            Log.d(TAG, "btnTestNotification wurde erfolgreich gefunden!");
            testButton.setOnClickListener(v -> {
                Log.d(TAG, "Manuelle Testbenachrichtigung wird gesendet...");
                NotificationReceiver receiver = new NotificationReceiver();
                receiver.sendNotification(MainActivity.this);
            });
        }
    }

    // Prüft und fordert die Benachrichtigungsberechtigung für Android 13+ an
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Berechtigung für Benachrichtigungen wird angefordert...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                Log.d(TAG, "Berechtigung für Benachrichtigungen bereits erteilt!");
            }
        }
    }
}
