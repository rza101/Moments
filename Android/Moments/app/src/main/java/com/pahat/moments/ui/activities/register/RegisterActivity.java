package com.pahat.moments.ui.activities.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityRegisterBinding;
import com.pahat.moments.ui.activities.main.MainActivity;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        binding.registerLoadingLottie.setVisibility(View.GONE);

        binding.registerBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                final String email = binding.registerEtEmail.getText().toString();
                final String fullName = binding.registerEtFullName.getText().toString();
                String username = binding.registerEtUsername.getText().toString().toLowerCase(Locale.ROOT);
                String password = binding.registerEtPassword.getText().toString();
                String confirmPassword = binding.registerEtConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    binding.registerEtEmail.setError("Enter your email address!");
                    valid = false;
                }
                if (TextUtils.isEmpty(fullName)) {
                    binding.registerEtFullName.setError("Enter your full name!");
                    valid = false;
                }
                if (TextUtils.isEmpty(username)) {
                    binding.registerEtUsername.setError("Enter your username!");
                    valid = false;
                }
                if (TextUtils.isEmpty(password)) {
                    binding.registerEtPassword.setError("Enter your password!");
                    valid = false;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    binding.registerEtConfirmPassword.setError("Enter your confirm password!");
                    valid = false;
                }
                if (password.length() < 6) {
                    binding.registerEtPassword.setError("Password too short, enter minimum 6 characters!");
                    valid = false;
                }

                if (!confirmPassword.equals(password)) {
                    binding.registerEtConfirmPassword.setError("Password doesn't match!");
                    valid = false;
                }

                for (char c : username.toCharArray()) {
                    if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '.' && c != '_') {
                        Utilities.makeToast(RegisterActivity.this, "Username contains invalid character");
                        valid = false;
                        break;
                    }
                }

                if (!valid) {
                    return;
                }
                showLoading();
                mRef = mRoot.child(Constants.FIREBASE_USERS_DB_REF);
                mRef.orderByChild("username").equalTo(username)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().getValue() != null) {
                                    // USERNAME NOT DUPLICATE
                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> fcmTask) {
                                            // GET FCM TOKEN
                                            mAuth.createUserWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> authTask) {
                                                                    if (authTask.isSuccessful()) {
                                                                        // SUCCESSFULLY REGISTERED
                                                                        String userId = authTask.getResult().getUser().getUid();

                                                                        APIUtil.getAPIService().createUser(userId, username, fullName, fcmTask.getResult()).enqueue(new Callback<APIResponse<APIUser>>() {
                                                                            @Override
                                                                            public void onResponse(Call<APIResponse<APIUser>> call, Response<APIResponse<APIUser>> response) {
                                                                                hideLoading();
                                                                                if (response.isSuccessful()) {
                                                                                    // STORE TO API SUCCESS
                                                                                    User user = new User(userId, username, fullName, null);
                                                                                    mRef = mRoot.child(Constants.FIREBASE_USERS_DB_REF).child(userId);
                                                                                    mRef.setValue(user);

                                                                                    Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();

                                                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                } else {
                                                                                    // STORE TO API FAILED
                                                                                    FirebaseAuth.getInstance().signOut();
                                                                                    Toast.makeText(RegisterActivity.this, "Register failed, please try with other credentials", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<APIResponse<APIUser>> call, Throwable t) {
                                                                                call.clone().enqueue(this);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        // REGISTER FAILED
                                                                        Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                    );
                                        }
                                    });
                                } else {
                                    binding.registerEtUsername.setError("Username already exists!");
                                }
                            } else {
                                Utilities.makeToast(getApplicationContext(), "Failed to register");
                            }
                        });
            }
        });

        binding.registerTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.registerIvPasswordEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.registerEtPassword.setInputType(
                        binding.registerEtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                binding.registerIvPasswordEye.setImageDrawable(AppCompatResources.getDrawable(RegisterActivity.this,
                        binding.registerEtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                                R.drawable.ic_visibility_off_24 :
                                R.drawable.ic_visibility_24
                ));
            }
        });

        binding.registerIvConfirmPasswordEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.registerEtConfirmPassword.setInputType(
                        binding.registerEtConfirmPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                binding.registerIvConfirmPasswordEye.setImageDrawable(AppCompatResources.getDrawable(RegisterActivity.this,
                        binding.registerEtConfirmPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                                R.drawable.ic_visibility_off_24 :
                                R.drawable.ic_visibility_24
                ));
            }
        });
    }

    public void showLoading() {
        binding.registerLoadingLottie.setVisibility(View.VISIBLE);
    }

    ;

    public void hideLoading() {
        binding.registerLoadingLottie.setVisibility(View.GONE);
    }

    ;
}