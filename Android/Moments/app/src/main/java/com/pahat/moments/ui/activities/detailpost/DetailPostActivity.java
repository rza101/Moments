package com.pahat.moments.ui.activities.detailpost;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.PostComposite;
import com.pahat.moments.databinding.ActivityDetailPostBinding;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostActivity extends AppCompatActivity {

    public static String POST_INTENT_KEY = "POST_INTENT_KEY";

    private ActivityDetailPostBinding binding;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        binding.detailPostClMain.setVisibility(View.GONE);

        setContentView(binding.getRoot());

        if (getIntent().getParcelableExtra(POST_INTENT_KEY) == null) {
            Utilities.makeToast(this, "Invalid access");
            finish();
        }

        post = getIntent().getParcelableExtra(POST_INTENT_KEY);

        binding.detailPostIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.FIREBASE_USERS_REF)
                .orderByChild("username")
                .equalTo(post.getUsername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        APIUtil.getAPIService()
                                .getPostById(String.valueOf(post.getId()))
                                .enqueue(new Callback<APIResponse<PostComposite>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<PostComposite>> call, Response<APIResponse<PostComposite>> response) {
                                        Log.d("postt", "onResponse: " + post);
                                        Log.d("postt", "onResponse: " + response.body().getData());
                                        if(response.isSuccessful()){
                                            Glide.with(DetailPostActivity.this)
                                                    .load(post.getImageUrl())
                                                    .placeholder(AppCompatResources.getDrawable(DetailPostActivity.this, R.drawable.ic_broken_image_24))
                                                    .into(binding.detailPostIvPicture);

                                            Log.d("postt", "onResponse: " + post);

                                            binding.detailPostTvCaptions.setText(post.getCaption());
                                            binding.detailPostTvUsername.setText(post.getUsername());
                                            binding.detailPostTvLikeCount.setText(String.format(getString(R.string.likes_count), response.body().getData().getPostLike().size()));

                                            binding.detailPostClMain.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
                                        Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                    }
                });
    }
}