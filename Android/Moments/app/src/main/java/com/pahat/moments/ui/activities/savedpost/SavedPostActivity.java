package com.pahat.moments.ui.activities.savedpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedPostActivity extends AppCompatActivity {

    private ActivitySavedPostBinding binding;
    private ItemPostAdapter itemPostAdapter;
    private User currentUser;
    private SavedPost savedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Saved Posts");

        FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_USERS_REF)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUser = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Utilities.makeToast(SavedPostActivity.this, "Failed to show post detail");
                    }
                });

        itemPostAdapter = new ItemPostAdapter(new OnItemClick<Post>() {
            @Override
            public void onClick(View v, Post data) {
                // ON ITEM CLICK
                startActivity(new Intent(SavedPostActivity.this, DetailPostActivity.class)
                        .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
                );
            }
        }, new OnItemClick<Post>() {
            @Override
            public void onClick(View v, Post data) {
                // ON MORE CLICK
            }
        });

//        itemPostAdapter.submitList(new ArrayList<>());
        APIUtil.getAPIService().getAllSavedPosts(currentUser.getUsername())
                .enqueue(new Callback<APIResponse<List<SavedPost>>>() {
                    @Override
                    public void onResponse(Call<APIResponse<List<SavedPost>>> call, Response<APIResponse<List<SavedPost>>> response) {
                        if (response.isSuccessful()){

                        } else {
                            Utilities.makeToast(SavedPostActivity.this, "Failed to load data");
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<List<SavedPost>>> call, Throwable t) {

                    }
                });

        binding.fragmentSavedPostRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        binding.fragmentSavedPostRvPosts.setAdapter(itemPostAdapter);

    }
}