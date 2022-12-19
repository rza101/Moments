package com.pahat.moments.ui.activities.detailpost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pahat.moments.databinding.ActivityDetailPostBinding;

public class DetailPostActivity extends AppCompatActivity {

    private ActivityDetailPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}