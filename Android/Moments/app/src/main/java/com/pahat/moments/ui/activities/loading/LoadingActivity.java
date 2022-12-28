package com.pahat.moments.ui.activities.loading;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.pahat.moments.databinding.ActivityLikeListBinding;
import com.pahat.moments.databinding.ActivityLoadingBinding;

public class LoadingActivity extends AppCompatActivity {

    private ActivityLoadingBinding binding;

    boolean is


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    LottieAnimationView lottie

}