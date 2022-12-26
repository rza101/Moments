package com.pahat.moments.ui.activities.otherprofile;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}