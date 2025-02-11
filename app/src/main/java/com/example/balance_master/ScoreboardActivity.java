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


        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // Navigation Buttons
        Button btnGame = findViewById(R.id.btnGame);
        Button btnScoreboard = findViewById(R.id.btnScoreboard);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnGame.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreboardActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        btnScoreboard.setOnClickListener(v -> {
            // Stay on this screen
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Retrieve the last score from Intent
        int lastScore = getIntent().getIntExtra("LAST_SCORE", 0);

        // Retrieve the high score from SharedPreferences
        int highscore = sharedPreferences.getInt("HIGHSCORE", 0);

        // Update highscore if the new score is higher
        if (lastScore > highscore) {
            highscore = lastScore;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("HIGHSCORE", highscore);
            editor.apply();
        }

        // Update the TextViews with the correct scores
        highscoreText.setText("Highscore: " + highscore);
        lastScoreText.setText("Last Score: " + lastScore);
    }

}