package com.pahat.moments.ui.activities.createpost;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pahat.moments.BuildConfig;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ActivityCreatePostBinding;
import com.pahat.moments.util.Utilities;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;

    private Uri tempCameraUri;
    private Uri imageUri;

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

        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Create Post");

        binding.createPostIbCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File tempFile = Utilities.createTempImageFile(CreatePostActivity.this);
                tempCameraUri = FileProvider.getUriForFile(CreatePostActivity.this,
                        BuildConfig.APPLICATION_ID,
                        tempFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.resolveActivity(getPackageManager());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri);

                cameraIntent.launch(intent);
            }
        });

        binding.createPostIbGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent.launch(
                        Intent.createChooser(
                                new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                                "Select an image"
                        )
                );
            }
        });

        binding.createPostIbClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                binding.createPostIvPreview.setImageDrawable(null);
                binding.createPostIvCameraInfo.setVisibility(View.VISIBLE);
                binding.createPostTvTextInfo.setVisibility(View.VISIBLE);
            }
        });

        binding.createPostBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = binding.createPostEtCaption.getText().toString();

                if (TextUtils.isEmpty(caption)) {
                    binding.createPostEtCaption.setError("Enter some captions");
                }

                if (imageUri == null) {
                    Utilities.makeToast(CreatePostActivity.this, "Please enter an image");
                }

                if (TextUtils.isEmpty(caption) || imageUri == null) {
                    return;
                }

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference(firebaseUser.getUid());

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("/users")
                        .child(firebaseUser.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue(User.class).getUsername();

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
                                                                        .createPost(username, task.getResult().toString(), caption)
                                                                        .enqueue(new Callback<APIResponse<Post>>() {
                                                                            @Override
                                                                            public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                                                                                if (response.isSuccessful()) {
                                                                                    Utilities.makeToast(CreatePostActivity.this, "Post created!");
                                                                                    finish();
                                                                                } else {
                                                                                    Utilities.makeToast(CreatePostActivity.this, "Failed to create post");
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                                                                                call.clone().enqueue(this);
                                                                            }
                                                                        });
                                                            } else {
                                                                Utilities.makeToast(CreatePostActivity.this, "Image upload failed. Please try again");
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Utilities.makeToast(CreatePostActivity.this, "Image upload failed. Please try again");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void setImagePreview(Uri uri) {
        Glide.with(CreatePostActivity.this)
                .load(uri)
                .placeholder(AppCompatResources.getDrawable(CreatePostActivity.this, R.drawable.ic_broken_image_24))
                .into(binding.createPostIvPreview);
        binding.createPostIvCameraInfo.setVisibility(View.GONE);
        binding.createPostTvTextInfo.setVisibility(View.GONE);
    }
}