package com.pahat.moments.ui.activities.otherprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.pahat.moments.databinding.ActivityEditProfileBinding;
import com.pahat.moments.databinding.ActivityOtherProfileBinding;

public class OtherProfileActivity extends AppCompatActivity {

    public static final String USER_INTENT_KEY = "USER_INTENT_KEY";

    private ActivityOtherProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }

    }
}