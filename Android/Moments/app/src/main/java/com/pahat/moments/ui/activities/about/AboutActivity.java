package com.pahat.moments.ui.activities.about;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityAboutBinding;
import com.pahat.moments.util.Utilities;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "About");
    }
}