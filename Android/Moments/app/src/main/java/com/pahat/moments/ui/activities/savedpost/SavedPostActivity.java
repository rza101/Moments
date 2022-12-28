package com.pahat.moments.ui.activities.savedpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.SavedPost;
import com.pahat.moments.databinding.ActivitySavedPostBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedPostActivity extends AppCompatActivity {

    private ActivitySavedPostBinding binding;
    private ItemPostAdapter itemPostAdapter;
    private User currentUser;
    private List<SavedPost> savedPostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Saved Posts");

        itemPostAdapter = new ItemPostAdapter(new OnItemClick<Post>() {
            @Override
            public void onClick(View v, Post data) {
                // ON ITEM CLICK
                startActivity(new Intent(SavedPostActivity.this, DetailPostActivity.class)
                        .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
                );
            }
        });

        binding.fragmentSavedPostRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.fragmentSavedPostRvPosts.setAdapter(itemPostAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_USERS_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = task.getResult().getValue(User.class);

                        APIUtil.getAPIService().getAllSavedPosts(currentUser.getUsername())
                                .enqueue(new Callback<APIResponse<List<SavedPost>>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<List<SavedPost>>> call,
                                                           Response<APIResponse<List<SavedPost>>> response) {
                                        if (response.isSuccessful()) {
                                            List<Post> postList = new ArrayList<>();

                                            for (SavedPost savedPost : response.body().getData()) {
                                                postList.add(new Post(
                                                        savedPost.getPostId(),
                                                        savedPost.getPostUsername(),
                                                        savedPost.getImageUrl(),
                                                        savedPost.getCaption(),
                                                        savedPost.getCreatedAt(),
                                                        savedPost.getCreatedAt()
                                                ));
                                            }

                                            itemPostAdapter.submitList(postList);
                                        } else {
                                            Utilities.makeToast(SavedPostActivity.this, "Failed to load saved posts");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<List<SavedPost>>> call, Throwable t) {
                                        Utilities.makeToast(SavedPostActivity.this, "Failed to load saved posts");
                                    }
                                });
                    } else {
                        Utilities.makeToast(SavedPostActivity.this, "Failed to load saved posts");
                    }
                });
    }
}