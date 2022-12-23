package com.pahat.moments.ui.activities.changepass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityChangePassBinding;

public class ChangePassActivity extends AppCompatActivity {

    private ActivityChangePassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}