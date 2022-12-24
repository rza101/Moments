package com.pahat.moments.ui.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityLoginBinding;
import com.pahat.moments.ui.activities.forgotpass.ForgotPassActivity;
import com.pahat.moments.ui.activities.main.MainActivity;
import com.pahat.moments.ui.activities.register.RegisterActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String register = "New to this app? <font color=#3FBDF1>Register Here</font>";
        binding.loginTvRegister.setText(Html.fromHtml(register));

        mAuth = FirebaseAuth.getInstance();

        binding.loginBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.loginEtEmail.getText().toString();
                String password = binding.loginEtPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    binding.loginEtEmail.setError("Enter your email address!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.loginEtPassword.setError("Enter your password!");
                    return;
                }

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> fcmTask) {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> authTask) {
                                        if (authTask.isSuccessful()) {
                                            APIUtil.getAPIService()
                                                    .updateUser(authTask.getResult().getUser().getUid(), fcmTask.getResult())
                                                    .enqueue(new Callback<APIResponse<List<APIUser>>>() {
                                                        @Override
                                                        public void onResponse(Call<APIResponse<List<APIUser>>> call, Response<APIResponse<List<APIUser>>> response) {
                                                            if (response.isSuccessful()) {
                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                FirebaseAuth.getInstance().signOut();
                                                                Toast.makeText(LoginActivity.this, "Login Failed. Please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<APIResponse<List<APIUser>>> call, Throwable t) {
                                                            call.clone().enqueue(this);
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        });

        binding.loginTvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.loginBtnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });
    }
}