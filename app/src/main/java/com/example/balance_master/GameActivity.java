package com.example.balance_master;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
private int playerScore;

public class GameActivity extends AppCompatActivity {

    private TextView highScoreText, lastScoreText;
    private ProgressBar highScoreBar, lastScoreBar;
    private int lastScore = 0;
    private int highScore = 0;
    private final int MAX_SCORE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        highScoreText = findViewById(R.id.highScoreText);
        lastScoreText = findViewById(R.id.lastScoreText);
        highScoreBar = findViewById(R.id.highScoreBar);
        lastScoreBar = findViewById(R.id.lastScoreBar);

        // Load saved scores
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        lastScore = prefs.getInt("lastScore", 0);
        highScore = prefs.getInt("highScore", 0);

        // Update UI
        updateScores();

        import android.content.Intent;

        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.putExtra("LAST_SCORE", playerScore);
        startActivity(intent);
        finish();
    }

    private void updateScores() {
        highScoreText.setText("High Score: " + highScore);
        lastScoreText.setText("Last Score: " + lastScore);

        // Convert scores to percentage of max
        int highScoreProgress = (int) ((highScore / (float) MAX_SCORE) * 100);
        int lastScoreProgress = (int) ((lastScore / (float) MAX_SCORE) * 100);

        highScoreBar.setProgress(highScoreProgress);
        lastScoreBar.setProgress(lastScoreProgress);
    }

    // Call this method after a game ends
    public void saveNewScore(int newScore) {
        lastScore = newScore;
        if (newScore > highScore) {
            highScore = newScore;
        }

        // Save scores
        SharedPreferences.Editor editor = getSharedPreferences("GamePrefs", MODE_PRIVATE).edit();
        editor.putInt("lastScore", lastScore);
        editor.putInt("highScore", highScore);
        editor.apply();

        // Update UI
        updateScores();
    }
}
