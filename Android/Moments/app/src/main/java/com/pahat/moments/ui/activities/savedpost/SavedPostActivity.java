package com.pahat.moments.ui.activities.savedpost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pahat.moments.databinding.ActivityLikeListBinding;
import com.pahat.moments.databinding.ActivitySavedPostBinding;

public class SavedPostActivity extends AppCompatActivity {

    private ActivitySavedPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}