package com.pahat.moments.ui.activities.detailpost;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityDetailPostBinding;

public class DetailPostActivity extends AppCompatActivity {

    private ActivityDetailPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.detailPostIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}