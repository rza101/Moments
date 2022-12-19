package com.pahat.moments.ui.activities.changepass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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