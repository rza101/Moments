package com.pahat.moments.ui.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.pahat.moments.databinding.ActivitySplashBinding;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            finish();
        }, 1000);
    }
}