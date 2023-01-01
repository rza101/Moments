package com.pahat.moments.ui.activities.editprofile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pahat.moments.BuildConfig;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityEditProfileBinding;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private Uri tempCameraUri;
    private Uri imageUri;
    private User currentUser;

    private ActivityResultLauncher<Intent> cameraIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                imageUri = tempCameraUri;
                setImagePreview(imageUri);
            }
        }
    });

    private ActivityResultLauncher<Intent> galleryIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    imageUri = result.getData().getData();
                    setImagePreview(imageUri);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editProfileIbCamera.setOnClickListener(v -> {
            File tempFile = Utilities.createTempImageFile(EditProfileActivity.this);
            tempCameraUri = FileProvider.getUriForFile(EditProfileActivity.this,
                    BuildConfig.APPLICATION_ID,
                    tempFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.resolveActivity(getPackageManager());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri);

            cameraIntent.launch(intent);
        });

        binding.editProfileIbGallery.setOnClickListener(v -> galleryIntent.launch(
                Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                        "Select an image"
                )
        ));

        binding.editProfileIbClear.setOnClickListener(v -> {
            imageUri = null;
            binding.editProfileIvPreview.setImageDrawable(null);
            binding.editProfileIvCameraInfo.setVisibility(View.VISIBLE);
            binding.editProfileTvTextInfo.setVisibility(View.VISIBLE);
        });

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_USERS_DB_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = task.getResult().getValue(User.class);
                    } else {
                        showError();
                    }
                });

        binding.editProfileEtFullName.setText(currentUser.getFullName());
        binding.editProfileEtUsername.setText(currentUser.getUsername());
        Glide.with(EditProfileActivity.this)
                .load(currentUser.getProfilePicture())
                .into(binding.editProfileIvPreview);

        binding.editProfileBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = binding.editProfileEtFullName.getText().toString();
                String username = binding.editProfileEtUsername.getText().toString();

                if (TextUtils.isEmpty(fullName)) {
                    binding.editProfileEtFullName.setError("Enter some captions");
                }

                if (TextUtils.isEmpty(username)) {
                    binding.editProfileEtFullName.setError("Enter some captions");
                }

                if (imageUri == null) {
                    Utilities.makeToast(EditProfileActivity.this, "Please enter an image");
                }

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(Constants.FIREBASE_PROFILE_PICTURES_STORAGE_REF)
                        .child(firebaseUser.getUid())
                        .child(imageUri.getLastPathSegment());

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(Constants.FIREBASE_USERS_DB_REF)
                        .child(firebaseUser.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            task.getResult()
                                                    .getMetadata()
                                                    .getReference()
                                                    .getDownloadUrl()
                                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            if (task.isSuccessful()) {
                                                                APIUtil.getAPIService()
                                                                        .updateUser(currentUser.getUserId(), fullName, task.getResult().toString())
                                                                        .enqueue(new Callback<APIResponse<APIUser>>() {
                                                                            @Override
                                                                            public void onResponse(Call<APIResponse<APIUser>> call, Response<APIResponse<APIUser>> response) {
                                                                                if (response.isSuccessful()) {
                                                                                    Utilities.makeToast(EditProfileActivity.this, "Profile updated!");
                                                                                    finish();
                                                                                } else {
                                                                                    Utilities.makeToast(EditProfileActivity.this, "Failed to update profile");
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<APIResponse<APIUser>> call, Throwable t) {
                                                                                call.clone().enqueue(this);

                                                                            }
                                                                        });
                                                            } else {
                                                                Utilities.makeToast(EditProfileActivity.this, "Image upload failed. Please try again");
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Utilities.makeToast(EditProfileActivity.this, "Image upload failed. Please try again");
                                        }
                                    }
                                });
                            } else {
                                Utilities.makeToast(EditProfileActivity.this, "Failed to update profile");
                            }
                        });
            }
        });

    }

    private void setImagePreview(Uri uri) {
        Glide.with(EditProfileActivity.this)
                .load(uri)
                .placeholder(AppCompatResources.getDrawable(EditProfileActivity.this, R.drawable.ic_broken_image_24))
                .into(binding.editProfileIvPreview);
        binding.editProfileIvCameraInfo.setVisibility(View.GONE);
        binding.editProfileTvTextInfo.setVisibility(View.GONE);
    }

    private void showError() {
        Utilities.makeToast(this, "Failed to edit profile");
    }
}