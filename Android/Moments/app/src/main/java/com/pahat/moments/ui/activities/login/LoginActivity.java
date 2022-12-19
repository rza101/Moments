package com.pahat.moments.ui.activities.login;

import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String register = "<font color=#FF000000>New to this app ? </font>" +
                "<font color=#3FBDF1>Register Here</font>";
        binding.loginTvRegister.setText(Html.fromHtml(register));
    }
}