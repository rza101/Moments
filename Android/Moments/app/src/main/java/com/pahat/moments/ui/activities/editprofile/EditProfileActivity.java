package com.pahat.moments.ui.activities.editprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.pahat.moments.BuildConfig;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityEditProfileBinding;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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

        new Thread(() -> {
            boolean[] isSuccess = {true};

            CountDownLatch countDownLatch = new CountDownLatch(1);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser = task.getResult().getValue(User.class);
                        } else {
                            showError();
                            isSuccess[0] = false;
                        }
                        countDownLatch.countDown();
                    });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isSuccess[0]) {
                return;
            }

            runOnUiThread(() -> {
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

                        new Thread(() -> {
                            boolean[] isUpdateSuccess = {true};
                            String[] imageUrl = new String[1];

                            CountDownLatch countDownLatch1 = new CountDownLatch(1);

                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child(Constants.FIREBASE_PROFILE_PICTURES_STORAGE_REF)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(imageUri.getLastPathSegment())
                                    .putFile(imageUri)
                                    .addOnCompleteListener(taskUpload -> {
                                        if (taskUpload.isSuccessful()) {
                                            taskUpload.getResult()
                                                    .getMetadata()
                                                    .getReference()
                                                    .getDownloadUrl()
                                                    .addOnCompleteListener(taskMetadata -> {
                                                        if (taskMetadata.isSuccessful()) {
                                                            imageUrl[0] = taskMetadata.getResult().toString();
                                                        } else {
                                                            Utilities.makeToast(EditProfileActivity.this, "Image upload failed. Please try again");
                                                            isUpdateSuccess[0] = false;
                                                        }
                                                        countDownLatch1.countDown();
                                                    });
                                        } else {
                                            Utilities.makeToast(EditProfileActivity.this, "Image upload failed. Please try again");
                                            isUpdateSuccess[0] = false;
                                            countDownLatch1.countDown();
                                        }
                                    });

                            try {
                                countDownLatch1.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (!isUpdateSuccess[0]) {
                                return;
                            }

                            CountDownLatch countDownLatch2 = new CountDownLatch(3);

                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("fullName", fullName);
                            updateMap.put("profilePicture", imageUrl[0]);

                            // update API
                            APIUtil.getAPIService()
                                    .updateUser(currentUser.getUserId(), fullName, imageUrl[0])
                                    .enqueue(new Callback<APIResponse<APIUser>>() {
                                        @Override
                                        public void onResponse(Call<APIResponse<APIUser>> call, Response<APIResponse<APIUser>> response) {
                                            if (!response.isSuccessful()) {
                                                isUpdateSuccess[0] = false;
                                            }
                                            countDownLatch2.countDown();
                                        }

                                        @Override
                                        public void onFailure(Call<APIResponse<APIUser>> call, Throwable t) {
                                            isUpdateSuccess[0] = false;
                                            countDownLatch2.countDown();
                                        }
                                    });

                            // update Firebase
                            FirebaseDatabase.getInstance()
                                    .getReference(Constants.FIREBASE_USERS_DB_REF)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .updateChildren(updateMap, (error, ref) -> {
                                        if (error != null) {
                                            isUpdateSuccess[0] = false;
                                        }
                                        countDownLatch2.countDown();
                                    });

                            // update chat group participants data
                            FirebaseDatabase.getInstance()
                                    .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        Map<String, Object> otherUpdateMap = new HashMap<>();

                                        for (DataSnapshot chatUserSnapshot : task.getResult().getChildren()) {
                                            for (DataSnapshot chatRoomSnapshot : chatUserSnapshot.getChildren()) {
                                                for (String userId : chatRoomSnapshot.getValue(ChatRoom.class).getParticipants().keySet()) {
                                                    if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        otherUpdateMap.put(chatUserSnapshot.getKey() + "/" +
                                                                chatRoomSnapshot.getKey() + "/participants/" +
                                                                userId + "/fullName", fullName);
                                                        otherUpdateMap.put(chatUserSnapshot.getKey() + "/" +
                                                                chatRoomSnapshot.getKey() + "/participants/" +
                                                                userId + "/profilePicture", imageUrl[0]);
                                                    }
                                                }
                                            }
                                        }

                                        FirebaseDatabase.getInstance()
                                                .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                                                .updateChildren(otherUpdateMap, (error, ref) -> {
                                                    if (error != null) {
                                                        isUpdateSuccess[0] = false;
                                                    }
                                                    countDownLatch2.countDown();
                                                });
                                    });

                            try {
                                countDownLatch2.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (!isUpdateSuccess[0]) {
                                Utilities.makeToast(EditProfileActivity.this, "Profile updated!");
                                finish();
                            } else {
                                Utilities.makeToast(EditProfileActivity.this, "Failed to update profile");
                            }
                        }).start();
                    }
                });
            });
        }).start();
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
        runOnUiThread(() -> Utilities.makeToast(EditProfileActivity.this, "Failed to edit profile"));
    }
}