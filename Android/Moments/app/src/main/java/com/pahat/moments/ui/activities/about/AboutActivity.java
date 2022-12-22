package com.pahat.moments.ui.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pahat.moments.databinding.ActivityAboutBinding;
import com.pahat.moments.databinding.ActivityLoginBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}