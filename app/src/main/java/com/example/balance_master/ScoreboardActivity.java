package com.example.balance_master;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.scoreboardbalancemaster.R;

public class ScoreboardActivity extends AppCompatActivity {

    private TextView highscoreText, lastScoreText;
    private Button retryButton, settingsButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        highscoreText = findViewById(R.id.highscore_text);
        lastScoreText = findViewById(R.id.last_score_text);
        retryButton = findViewById(R.id.retry_button);
        settingsButton = findViewById(R.id.settings_button);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // Navigation Buttons
        Button btnGame = findViewById(R.id.btnGame);
        Button btnScoreboard = findViewById(R.id.btnScoreboard);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Navigate to GameActivity
        btnGame.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreboardActivity.this, GameActivity.class);
            startActivity(intent);
        });

        // Stay on ScoreboardActivity when clicking "Scoreboard"
        btnScoreboard.setOnClickListener(v -> {
            // Do nothing since we are already here
        });

        // Logout and go back to LoginActivity
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(intent);
            finish();
        });


        // Score aus Intent abrufen
        int lastScore = getIntent().getIntExtra("LAST_SCORE", 0);
        TextView lastScoreTextView = findViewById(R.id.last_score_text);  // Falls du eine TextView für den Score hast
        lastScoreTextView.setText("Last Score: " + lastScore);
        int highscore = sharedPreferences.getInt("HIGHSCORE", 0);

        // Highscore aktualisieren, falls neuer Score höher ist
        if (lastScore > highscore) {
            highscore = lastScore;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("HIGHSCORE", highscore);
            editor.apply();
        }

        // Texte aktualisieren
        highscoreText.setText("Highscore: " + highscore);
        lastScoreText.setText("Last Score: " + lastScore);

        // Button für "Nochmal spielen"
       /* retryButton.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreboardActivity.this, ScoreboardActivity.class);
            startActivity(intent);
            finish();
        });

        */

        // Button für Einstellungen
       /* settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        */
    }
}