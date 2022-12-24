package com.pahat.moments.ui.activities.savedpost;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivitySavedPostBinding;
import com.pahat.moments.util.Utilities;

public class SavedPostActivity extends AppCompatActivity {

    private ActivitySavedPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Saved Posts");
    }
}