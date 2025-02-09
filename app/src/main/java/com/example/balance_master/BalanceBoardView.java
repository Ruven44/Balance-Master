package com.example.balance_master;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class BalanceBoardView extends AppCompatImageView {

    private final Paint paint = new Paint();
    private final Paint redBoxPaint = new Paint();
    private double currentAngle = 0.0; // Angle in degrees

    private final RectF redBox = new RectF();

    private boolean gameOver = false;
    private boolean gameRunning = false;
    private float finalScore = 0;
    private long startTime;

    private final Handler handler = new Handler();
    private GameListener gameListener;

    public interface GameListener {
        void onGameEnd(float finalScore);
    }

    public BalanceBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);

        redBoxPaint.setColor(Color.RED);
        redBoxPaint.setStyle(Paint.Style.FILL);
    }

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void startGame() {
        finalScore = 0;
        gameOver = false;
        gameRunning = false;
        currentAngle = 0.0; // Reset tilt
        handler.removeCallbacksAndMessages(null); // Clear previous tasks
        invalidate();

        handler.postDelayed(() -> {
            startTime = System.currentTimeMillis();
            gameRunning = true;
            invalidate();
        }, 5000);
    }

    public void updateAngle(double angle) {
        if (!gameRunning || gameOver) return;

        // Smooth transition instead of abrupt changes
        currentAngle = (0.98 * currentAngle) + (0.006 * angle);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float lineLength = getWidth() / 3f;

        float endX = (float) (centerX + lineLength * Math.cos(Math.toRadians(currentAngle)));
        float endY = (float) (centerY + lineLength * Math.sin(Math.toRadians(currentAngle)));
        float startX = (float) (centerX - lineLength * Math.cos(Math.toRadians(currentAngle)));
        float startY = (float) (centerY - lineLength * Math.sin(Math.toRadians(currentAngle)));

        // Collision detection
        if (lineIntersectsRect(startX, startY, endX, endY, redBox)) {
            endGame();
            return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= 10000) { // 10 seconds passed, game won
            finalScore = 100;
            endGame();
        }

        invalidate();
    }

    private boolean lineIntersectsRect(float x1, float y1, float x2, float y2, RectF rect) {
        return lineIntersectsLine(x1, y1, x2, y2, rect.left, rect.top, rect.right, rect.top) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.left, rect.bottom, rect.right, rect.bottom) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.left, rect.top, rect.left, rect.bottom) ||
                lineIntersectsLine(x1, y1, x2, y2, rect.right, rect.top, rect.right, rect.bottom);
    }

    private boolean lineIntersectsLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denominator == 0) return false; // Parallel lines

        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

        return ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1;
    }

    private void endGame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        finalScore = Math.min(100, (elapsedTime / 10000f) * 100);
        gameOver = true;
        gameRunning = false;
        handler.removeCallbacksAndMessages(null); // Stop everything

        if (gameListener != null) {
            gameListener.onGameEnd(finalScore);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(50f);
            canvas.drawText("Game Over!", getWidth() / 2f - 100, getHeight() / 2f, paint);
            return;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float lineLength = getWidth() / 3f;

        float endX = (float) (centerX + lineLength * Math.cos(Math.toRadians(currentAngle)));
        float endY = (float) (centerY + lineLength * Math.sin(Math.toRadians(currentAngle)));
        float startX = (float) (centerX - lineLength * Math.cos(Math.toRadians(currentAngle)));
        float startY = (float) (centerY - lineLength * Math.sin(Math.toRadians(currentAngle)));

        paint.setColor(Color.BLUE);
        canvas.drawLine(startX, startY, endX, endY, paint);
        canvas.drawCircle(centerX, centerY, 10f, paint);

        float boxHeight = getHeight() / 8f;
        float boxWidth = getWidth() / 2f;
        float boxTop = getHeight() * 0.75f;

        redBox.set(centerX - boxWidth / 2f, boxTop, centerX + boxWidth / 2f, boxTop + boxHeight);
        canvas.drawRect(redBox, redBoxPaint);
    }
}
