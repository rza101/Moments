package com.pahat.moments.ui.activities.likelist;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityLikeListBinding;

public class LikeListActivity extends AppCompatActivity {

    private ActivityLikeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLikeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.getRoot());
        binding.toolbar.toolbarTitle.setText("Likes");
        binding.toolbar.toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}