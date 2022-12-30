package com.pahat.moments.ui.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ActivitySplashBinding;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.main.MainActivity;
import com.pahat.moments.util.Constants;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = task.getResult().getValue(User.class);
                            startActivity(
                                    new Intent(SplashActivity.this, MainActivity.class)
                                            .putExtra(MainActivity.USER_INTENT_KEY, user)
                            );
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        finish();
                    });
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, 1000);
        }
    }
}