package com.example.balance_master;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements SensorEventListener, BalanceBoardView.GameListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final Handler handler = new Handler();

    private Button startButton;
    private TextView timerTextView;
    private TextView resultTextView;
    private BalanceBoardView balanceBoardView;

    private boolean isGameRunning = false;
    private final long gameDuration = 10000L; // 10 seconds
    private final long countdownTime = 5000L; // 5-second countdown

    private double tiltAngle = 0.0;
    private CountDownTimer gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game); // Updated XML reference



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Navigation Buttons
        Button btnGame = findViewById(R.id.btnGame);
        Button btnScoreboard = findViewById(R.id.btnScoreboard);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Stay on GameActivity when clicking "Game"
        btnGame.setOnClickListener(v -> {
            // Do nothing since we are already here
        });

        // Navigate to ScoreboardActivity
        btnScoreboard.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, ScoreboardActivity.class);
            startActivity(intent);
        });

        // Logout and go back to LoginActivity
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(intent);
            finish();
        });

        startButton = findViewById(R.id.startButton);
        timerTextView = findViewById(R.id.timerTextView);
        resultTextView = findViewById(R.id.resultTextView);
        balanceBoardView = findViewById(R.id.balanceBoardView);
        balanceBoardView.setGameListener(this);

        startButton.setOnClickListener(v -> startGame());


    }

    private void startGame() {
        resetUI();
        balanceBoardView.startGame();
        startButton.setEnabled(false);

        new CountDownTimer(countdownTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Starting in: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                startMeasurement();
            }
        }.start();
    }

    private void startMeasurement() {
        isGameRunning = true;
        timerTextView.setText("Game Running");

        gameTimer = new CountDownTimer(gameDuration, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                balanceBoardView.updateAngle(tiltAngle);
            }

            @Override
            public void onFinish() {
                if (!balanceBoardView.isGameOver()) {
                    onGameEnd(100);
                }
            }
        };
        gameTimer.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isGameRunning) {
            float x = event.values[0]; // Left/Right tilt
            float y = event.values[1]; // Forward/Backward tilt

            tiltAngle = Math.toDegrees(Math.atan2(y, x));

            balanceBoardView.updateAngle(tiltAngle);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void resetUI() {
        resultTextView.setText("");
        timerTextView.setText("Get Ready...");
        startButton.setEnabled(false);
    }

    public void onGameEnd(float finalScore) {
        isGameRunning = false;
        if (gameTimer != null) gameTimer.cancel();
        resultTextView.setText("Final Score: " + Math.round(finalScore));
        startButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
