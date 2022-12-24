package com.pahat.moments.util;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ToolbarChildrenBinding;

public class Utilities {
    public static void initChildToolbar(AppCompatActivity activity, ToolbarChildrenBinding binding, String title) {
        activity.setSupportActionBar(binding.getRoot());
        binding.toolbarTitle.setText(title);
        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
