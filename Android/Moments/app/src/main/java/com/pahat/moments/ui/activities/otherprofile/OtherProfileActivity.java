package com.pahat.moments.ui.activities.otherprofile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.UserFollowComposite;
import com.pahat.moments.databinding.ActivityOtherProfileBinding;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.activities.userlist.UserListActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherProfileActivity extends AppCompatActivity {

    public static final String USER_INTENT_KEY = "USER_INTENT_KEY";
    private User user;
    private ItemPostAdapter itemPostAdapter;
    private UserFollowComposite userFollowComposite;
    private List<Post> postList;
    private boolean loadSuccess = true;

    private ActivityOtherProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        binding.otherProfileLoadingLottie.setVisibility(View.GONE);
        setContentView(binding.getRoot());
        user = getIntent().getParcelableExtra(USER_INTENT_KEY);
        Utilities.initChildToolbar(this, binding.toolbar, user.getFullName());


        itemPostAdapter = new ItemPostAdapter((v, data) -> {
            startActivity(new Intent(OtherProfileActivity.this, DetailPostActivity.class)
                    .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
            );
        });

        binding.otherProfileProfileRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.otherProfileProfileRvPosts.setAdapter(itemPostAdapter);

        new Thread(() -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(1);

            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = task.getResult().getValue(User.class);
                        } else {
                            showErrorToast();
                        }
                        countDownLatch1.countDown();
                    });

            try {
                countDownLatch1.await();
            } catch (InterruptedException e) {
                showErrorToast();
                return;
            }

            CountDownLatch countDownLatch2 = new CountDownLatch(1);

            APIUtil.getAPIService()
                    .getUserFollow(user.getUsername())
                    .enqueue(new Callback<APIResponse<UserFollowComposite>>() {
                        @Override
                        public void onResponse(Call<APIResponse<UserFollowComposite>> call,
                                               Response<APIResponse<UserFollowComposite>> responseFollow) {
                            if (responseFollow.isSuccessful()) {
                                userFollowComposite = responseFollow.body().getData();
                            } else {
                                showErrorToast();
                            }
                            countDownLatch2.countDown();
                        }

                        @Override
                        public void onFailure(Call<APIResponse<UserFollowComposite>> call, Throwable t) {
                            showErrorToast();
                            countDownLatch2.countDown();
                        }
                    });

            try {
                countDownLatch2.await();
            } catch (InterruptedException e) {
                showErrorToast();
                return;
            }

            CountDownLatch countDownLatch3 = new CountDownLatch(1);

            APIUtil.getAPIService()
                    .getAllPostByUsername(user.getUsername())
                    .enqueue(new Callback<APIResponse<List<Post>>>() {
                        @Override
                        public void onResponse(Call<APIResponse<List<Post>>> call, Response<APIResponse<List<Post>>> response) {
                            if (response.isSuccessful()) {
                                postList = response.body().getData();
                            } else {
                                showErrorToast();
                            }

                            countDownLatch3.countDown();
                        }

                        @Override
                        public void onFailure(Call<APIResponse<List<Post>>> call, Throwable t) {
                            showErrorToast();
                            t.printStackTrace();
                            countDownLatch3.countDown();
                        }
                    });

            try {
                countDownLatch3.await();
            } catch (InterruptedException e) {
                showErrorToast();
                return;
            }

            OtherProfileActivity.this.runOnUiThread(() -> {
                if (!loadSuccess) {
                    return;
                }

                submitList();

                user = getIntent().getParcelableExtra(USER_INTENT_KEY);

                if (!TextUtils.isEmpty(user.getProfilePicture())) {
                    Glide.with(OtherProfileActivity.this)
                            .load(user.getProfilePicture())
                            .into(binding.otherProfileProfileCivDp);
                }

                binding.otherProfileProfileTvUsername.setText(user.getUsername());
                binding.otherProfileProfileTvFullname.setText(user.getFullName());

                binding.otherProfileProfileTvFollowers.setText(
                        String.format(getString(R.string.followers_count),
                                Utilities.numberToText(userFollowComposite.getFollower().size())
                        )
                );

                binding.otherProfileProfileTvFollowing.setText(
                        String.format(getString(R.string.following_count),
                                Utilities.numberToText(userFollowComposite.getFollowing().size())
                        )
                );

                binding.otherProfileProfileTvFollowers.setOnClickListener(v -> {
                    Intent intent = new Intent(OtherProfileActivity.this, UserListActivity.class);
                    intent.putExtra(UserListActivity.TYPE_INTENT_KEY, UserListActivity.TYPE_LIKE);
                    intent.putExtra(UserListActivity.USER_LIST_INTENT_KEY, Utilities.followerListToUserList(userFollowComposite.getFollower()));
                    startActivity(intent);
                });

                binding.otherProfileProfileTvFollowing.setOnClickListener(v -> {
                    Intent intent = new Intent(OtherProfileActivity.this, UserListActivity.class);
                    intent.putExtra(UserListActivity.TYPE_INTENT_KEY, UserListActivity.TYPE_LIKE);
                    intent.putExtra(UserListActivity.USER_LIST_INTENT_KEY, Utilities.followingListToUserList(userFollowComposite.getFollowing()));
                    startActivity(intent);
                });
            });
        }).start();
    }

    private void showErrorToast() {
        OtherProfileActivity.this.runOnUiThread(() -> {
            Utilities.makeToast(OtherProfileActivity.this, "Failed to show profile");
            loadSuccess = false;
        });
    }

    private void submitList() {
        binding.otherProfileProfileTvNoPost.setVisibility(postList.size() == 0 ? View.VISIBLE : View.GONE);
        itemPostAdapter.submitList(postList);
    }

    public void showLoading() {
        binding.otherProfileLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.otherProfileLoadingLottie.setVisibility(View.GONE);
    }
}