package com.pahat.moments.ui.activities.detailpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.pahat.moments.data.network.model.PostComment;
import com.pahat.moments.data.network.model.PostComposite;
import com.pahat.moments.data.network.model.UserFollow;
import com.pahat.moments.data.network.model.UserFollowComposite;
import com.pahat.moments.databinding.ActivityDetailPostBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.adapters.ItemCommentAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostActivity extends AppCompatActivity {

    public static String POST_INTENT_KEY = "POST_INTENT_KEY";

    private ActivityDetailPostBinding binding;
    private ItemCommentAdapter itemCommentAdapter;

    private Post post;
    private User user;
    private List<PostComment> postCommentList;

    private PostComposite postComposite;
    private List<UserFollow> userFollowList;

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

        itemCommentAdapter = new ItemCommentAdapter(new OnItemClick<User>() {
            @Override
            public void onClick(View v, User data) {
                startActivity(new Intent(DetailPostActivity.this, OtherProfileActivity.class)
                        .putExtra(OtherProfileActivity.USER_INTENT_KEY, data));
            }
        });

        binding.detailPostRvComments.setLayoutManager(new LinearLayoutManager(this));
        binding.detailPostRvComments.setAdapter(itemCommentAdapter);

        new Thread() {
            @Override
            public void run() {
                super.run();

                CountDownLatch countDownLatch1 = new CountDownLatch(1);

                FirebaseDatabase.getInstance().getReference()
                        .child(Constants.FIREBASE_USERS_REF)
                        .orderByChild("username")
                        .equalTo(post.getUsername())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User.class);
                                countDownLatch1.countDown();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                countDownLatch1.countDown();
                            }
                        });

                try {
                    countDownLatch1.await();
                } catch (InterruptedException e) {
                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                    return;
                }

                CountDownLatch countDownLatch2 = new CountDownLatch(1);

                APIUtil.getAPIService()
                        .getPostById(String.valueOf(post.getId()))
                        .enqueue(new Callback<APIResponse<PostComposite>>() {
                            @Override
                            public void onResponse(Call<APIResponse<PostComposite>> call,
                                                   Response<APIResponse<PostComposite>> response) {
                                if (response.isSuccessful()) {
                                    postComposite = response.body().getData();
                                } else {
                                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                }

                                countDownLatch2.countDown();
                            }

                            @Override
                            public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
                                Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                countDownLatch2.countDown();
                            }
                        });

                try {
                    countDownLatch2.await();
                } catch (InterruptedException e) {
                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                    return;
                }

                CountDownLatch countDownLatch3 = new CountDownLatch(1);

                APIUtil.getAPIService()
                        .getUserFollow(post.getUsername())
                        .enqueue(new Callback<APIResponse<UserFollowComposite>>() {
                            @Override
                            public void onResponse(Call<APIResponse<UserFollowComposite>> call,
                                                   Response<APIResponse<UserFollowComposite>> responseFollow) {
                                if (responseFollow.isSuccessful()) {
                                    userFollowList = responseFollow.body().getData().getFollower();
                                } else {
                                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                }

                                countDownLatch3.countDown();
                            }

                            @Override
                            public void onFailure(Call<APIResponse<UserFollowComposite>> call, Throwable t) {
                                Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                countDownLatch3.countDown();
                            }
                        });

                try {
                    countDownLatch3.await();
                } catch (InterruptedException e) {
                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(DetailPostActivity.this)
                                .load(post.getImageUrl())
                                .placeholder(AppCompatResources.getDrawable(DetailPostActivity.this,
                                        R.drawable.ic_broken_image_24))
                                .into(binding.detailPostIvPicture);

                        if (user.getProfilePicture() != null) {
                            Glide.with(DetailPostActivity.this)
                                    .load(user.getProfilePicture())
                                    .into(binding.detailPostCivDp);
                        }

                        binding.detailPostTvCaptions.setText(post.getCaption());
                        binding.detailPostTvUsername.setText(post.getUsername());
                        binding.detailPostTvFollowers.setText(String.format(getString(R.string.followers_count),
                                Utilities.numberToText(userFollowList.size())));
                        binding.detailPostTvLikeCount.setText(String.format(getString(R.string.likes_count),
                                postComposite.getPostLike().size()));

                        postCommentList = postComposite.getPostComment();
                        itemCommentAdapter.submitList(postCommentList);

                        binding.detailPostClMain.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();

//        FirebaseDatabase.getInstance().getReference()
//                .child(Constants.FIREBASE_USERS_REF)
//                .orderByChild("username")
//                .equalTo(post.getUsername())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        user = snapshot.getValue(User.class);
//
//                        APIUtil.getAPIService()
//                                .getPostById(String.valueOf(post.getId()))
//                                .enqueue(new Callback<APIResponse<PostComposite>>() {
//                                    @Override
//                                    public void onResponse(Call<APIResponse<PostComposite>> call,
//                                                           Response<APIResponse<PostComposite>> responsePost) {
//                                        if (responsePost.isSuccessful()) {
//                                            APIUtil.getAPIService()
//                                                    .getUserFollow(post.getUsername())
//                                                    .enqueue(new Callback<APIResponse<UserFollowComposite>>() {
//                                                        @Override
//                                                        public void onResponse(Call<APIResponse<UserFollowComposite>> call,
//                                                                               Response<APIResponse<UserFollowComposite>> responseFollow) {
//                                                            if (responseFollow.isSuccessful()) {
//                                                                Glide.with(DetailPostActivity.this)
//                                                                        .load(post.getImageUrl())
//                                                                        .placeholder(AppCompatResources.getDrawable(DetailPostActivity.this,
//                                                                                R.drawable.ic_broken_image_24))
//                                                                        .into(binding.detailPostIvPicture);
//
//                                                                if (user.getProfilePicture() != null) {
//                                                                    Glide.with(DetailPostActivity.this)
//                                                                            .load(user.getProfilePicture())
//                                                                            .into(binding.detailPostCivDp);
//                                                                }
//
//                                                                binding.detailPostTvCaptions.setText(post.getCaption());
//                                                                binding.detailPostTvUsername.setText(post.getUsername());
//                                                                binding.detailPostTvFollowers.setText(String.format(getString(R.string.followers_count),
//                                                                        Utilities.numberToText(responseFollow.body().getData().getFollower().size())));
//                                                                binding.detailPostTvLikeCount.setText(String.format(getString(R.string.likes_count),
//                                                                        responsePost.body().getData().getPostLike().size()));
//
//                                                                postCommentList = responsePost.body().getData().getPostComment();
//                                                                itemCommentAdapter.submitList(postCommentList);
//
//                                                                binding.detailPostClMain.setVisibility(View.VISIBLE);
//                                                            } else {
//                                                                Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(Call<APIResponse<UserFollowComposite>> call, Throwable t) {
//                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
//                                                        }
//                                                    });
//                                        } else {
//                                            Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
//                                        Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
//                    }
//                });
    }
}