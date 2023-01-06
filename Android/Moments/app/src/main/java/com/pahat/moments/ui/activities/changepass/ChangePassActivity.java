package com.pahat.moments.ui.activities.changepass;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pahat.moments.R;
import com.pahat.moments.databinding.ActivityChangePassBinding;
import com.pahat.moments.util.Utilities;

public class ChangePassActivity extends AppCompatActivity {

    private ActivityChangePassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Change Password");
        binding.changePassLoadingLottie.setVisibility(View.GONE);

        binding.changePassBtnChangePass.setOnClickListener(v -> {
            String password = binding.changePassEtPassword.getText().toString();
            String confirmPassword = binding.changePassEtPasswordConfirm.getText().toString();

            if (TextUtils.isEmpty(password)) {
                binding.changePassEtPassword.setError("Enter your password!");
                return;
            }

            if (!confirmPassword.equals(password)) {
                binding.changePassEtPasswordConfirm.setError("Password doesn't match!");
                return;
            }

            showLoading();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        hideLoading();
                        if (task.isSuccessful()) {
                            user.updatePassword(password).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ChangePassActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ChangePassActivity.this, "Error password not updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ChangePassActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
        binding.changePassIvPasswordEye.setOnClickListener(v -> {
            binding.changePassEtPassword.setInputType(
                    binding.changePassEtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            binding.changePassIvPasswordEye.setImageDrawable(AppCompatResources.getDrawable(ChangePassActivity.this,
                    binding.changePassEtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                            R.drawable.ic_visibility_off_24 :
                            R.drawable.ic_visibility_24
            ));
        });
        binding.changePassIvConfirmPasswordEye.setOnClickListener(v -> {
            binding.changePassEtPasswordConfirm.setInputType(
                    binding.changePassEtPasswordConfirm.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            binding.changePassIvConfirmPasswordEye.setImageDrawable(AppCompatResources.getDrawable(ChangePassActivity.this,
                    binding.changePassEtPasswordConfirm.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ?
                            R.drawable.ic_visibility_off_24 :
                            R.drawable.ic_visibility_24
            ));
        });
    }

    public void showLoading() {
        binding.changePassLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.changePassLoadingLottie.setVisibility(View.GONE);
    }
}