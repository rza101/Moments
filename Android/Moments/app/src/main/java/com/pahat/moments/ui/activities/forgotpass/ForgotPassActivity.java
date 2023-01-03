package com.pahat.moments.ui.activities.forgotpass;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.pahat.moments.databinding.ActivityForgotPassBinding;

public class ForgotPassActivity extends AppCompatActivity {

    private ActivityForgotPassBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        binding.forgotPassLoadingLottie.setVisibility(View.GONE);

        binding.forgotPassBtnSendEmail.setOnClickListener(v -> {
            String email = binding.forgotPassEtEmail.getText().toString();

            if (TextUtils.isEmpty(email)) {
                binding.forgotPassEtEmail.setError("Enter your email address!");
                return;
            }
            showLoading();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        hideLoading();
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public void showLoading() {
        binding.forgotPassLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.forgotPassLoadingLottie.setVisibility(View.GONE);
    }
}