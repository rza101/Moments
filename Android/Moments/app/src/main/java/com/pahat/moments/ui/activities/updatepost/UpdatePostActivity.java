package com.pahat.moments.ui.activities.updatepost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pahat.moments.databinding.ActivityUpdatePostBinding;

public class UpdatePostActivity extends AppCompatActivity {

    private ActivityUpdatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}