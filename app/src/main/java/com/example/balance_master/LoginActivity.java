package com.example.balance_master;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Reference UI elements
        EditText editTextUsername = findViewById(R.id.editText_username);
        Button btnContinue = findViewById(R.id.button_continue);

        // Initially disable the button
        btnContinue.setEnabled(false);

        // Add text change listener to EditText
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable button only if text is not empty
                btnContinue.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Click listener for the Continue button
        btnContinue.setOnClickListener(view -> {
            // Get username input
            String username = editTextUsername.getText().toString().trim();

            // Proceed only if username is valid
            if (!username.isEmpty()) {
                // Navigate to DashboardActivity
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);
            }
        });
    }
}
