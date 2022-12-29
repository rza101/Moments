package com.pahat.moments.ui.activities.updatepost;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ActivityUpdatePostBinding;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;
import com.pahat.moments.ui.activities.updatepost.UpdatePostActivity;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostActivity extends AppCompatActivity {

    public static final String POST_INTENT_KEY = "POST_INTENT_KEY";
    private Post post;

    private ActivityUpdatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = getIntent().getParcelableExtra(POST_INTENT_KEY);

        Utilities.initChildToolbar(this, binding.toolbar, "Update Post");

        binding.updatePostEtCaption.setText(post.getCaption());
        Glide.with(UpdatePostActivity.this)
                .load(post.getImageUrl())
                .placeholder(AppCompatResources.getDrawable(UpdatePostActivity.this, R.drawable.ic_broken_image_24))
                .into(binding.updatePostIvPreview);

        binding.updatePostBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = binding.updatePostEtCaption.getText().toString();
                if (TextUtils.isEmpty(caption)) {
                    binding.updatePostEtCaption.setError("Enter a caption");
                    return;
                }
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference(firebaseUser.getUid());

                APIUtil.getAPIService().updatePost(post.getId(), caption).enqueue(new Callback<APIResponse<Post>>() {
                    @Override
                    public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                        if (response.isSuccessful()) {
                            Utilities.makeToast(UpdatePostActivity.this, "Success to update posts");
                        } else {
                            Utilities.makeToast(UpdatePostActivity.this, "Failed to update posts");
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                        Utilities.makeToast(UpdatePostActivity.this, "API ERROR");
                    }
                });
//                if (caption ==) {
//                    binding.updatePostEtCaption.setError("Caption cannot be same as before");
//                    return;
//                }
            }
        });
    }
}