package com.pahat.moments.ui.activities.likelist;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityLikeListBinding;
import com.pahat.moments.util.Utilities;

public class LikeListActivity extends AppCompatActivity {

    private ActivityLikeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLikeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Likes");
    }
}