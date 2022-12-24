package com.pahat.moments.ui.activities.updatepost;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityUpdatePostBinding;
import com.pahat.moments.util.Utilities;

public class UpdatePostActivity extends AppCompatActivity {

    private ActivityUpdatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Update Post");
    }
}