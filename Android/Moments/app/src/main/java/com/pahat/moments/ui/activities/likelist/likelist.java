package com.pahat.moments.ui.activities.likelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pahat.moments.databinding.ActivityForgotPassBinding;

public class likelist extends AppCompatActivity {

    private ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}