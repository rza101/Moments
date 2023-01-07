package com.pahat.moments.ui.activities.updatepost;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.pahat.moments.R;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ActivityUpdatePostBinding;
import com.pahat.moments.util.Utilities;

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

        binding.updatePostBtnPost.setOnClickListener(view -> {
            String caption = binding.updatePostEtCaption.getText().toString();

            if (TextUtils.isEmpty(caption)) {
                binding.updatePostEtCaption.setError("Enter a caption");
                return;
            }

            showLoading();

            APIUtil.getAPIService().updatePost(post.getId(), caption).enqueue(new Callback<APIResponse<Post>>() {
                @Override
                public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                    hideLoading();
                    if (response.isSuccessful()) {
                        Utilities.makeToast(UpdatePostActivity.this, "Success to update posts");
                        finish();
                    } else {
                        Utilities.makeToast(UpdatePostActivity.this, "Failed to update posts");
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                    hideLoading();
                    Utilities.makeToast(UpdatePostActivity.this, "API ERROR");
                }
            });
        });
    }

    public void showLoading() {
        binding.updatePostLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.updatePostLoadingLottie.setVisibility(View.GONE);
    }
}