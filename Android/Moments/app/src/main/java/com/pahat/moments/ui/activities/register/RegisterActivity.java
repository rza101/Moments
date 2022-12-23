package com.pahat.moments.ui.activities.register;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.data.network.model.User;
import com.pahat.moments.databinding.ActivityRegisterBinding;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRoot, mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String login = "<font color=#FF000000>Have an account? </font>" +
                "<font color=#3FBDF1>Login</font>";
        binding.registerTvLogin.setText(Html.fromHtml(login));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRoot = mDatabase.getReference();

        binding.registerBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = binding.registerEtEmail.getText().toString();
                final String fullName = binding.registerEtFullName.getText().toString();
                String username = binding.registerEtUsername.getText().toString();
                String password = binding.registerEtPassword.getText().toString();
                String confirmPassword = binding.registerEtConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    binding.registerEtEmail.setError("Enter your email address!");
                    return;
                }
                if (TextUtils.isEmpty(fullName)) {
                    binding.registerEtFullName.setError("Enter your full name!");
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    binding.registerEtUsername.setError("Enter your username!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.registerEtPassword.setError("Enter your password!");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    binding.registerEtConfirmPassword.setError("Enter your confirm password!");
                    return;
                }
//                if (password.length() < 6) {
//                    binding.etPassword.setError("Password too short, enter minimum 6 characters!");
//                    return;
//                }
                if (!confirmPassword.equals(password)) {
                    binding.registerEtConfirmPassword.setError("Password doesn't match!");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                                    User user = new User(email, username, fullName);
                                    String userId = task.getResult().getUser().getUid();
                                    mRef = mRoot.child("users").child(userId);
                                    mRef.setValue(user);
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.registerTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}