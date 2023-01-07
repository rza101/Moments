package com.pahat.moments.ui.activities.otherprofile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.data.network.model.FCMResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.UserFollow;
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

    private ActivityOtherProfileBinding binding;
    private ItemPostAdapter itemPostAdapter;

    private User currentUser;
    private User otherUser;
    private UserFollowComposite userFollowComposite;
    private List<Post> postList;

    private boolean loadSuccess = true;
    private boolean isFollowing = false;

    private long followId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.otherProfileLoadingLottie.setVisibility(View.GONE);
        binding.otherProfileSvMain.setVisibility(View.GONE);

        otherUser = getIntent().getParcelableExtra(USER_INTENT_KEY);

        Utilities.initChildToolbar(this, binding.toolbar, otherUser.getFullName());

        itemPostAdapter = new ItemPostAdapter((v, data) -> {
            startActivity(new Intent(OtherProfileActivity.this, DetailPostActivity.class)
                    .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
            );
        });

        binding.otherProfileProfileRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.otherProfileProfileRvPosts.setAdapter(itemPostAdapter);

        new Thread(() -> {
            showLoading();
            CountDownLatch countDownLatch1 = new CountDownLatch(2);

            APIUtil.getAPIService()
                    .getUserByUsername(otherUser.getUsername())
                    .enqueue(new Callback<APIResponse<APIUser>>() {
                        @Override
                        public void onResponse(Call<APIResponse<APIUser>> call, Response<APIResponse<APIUser>> response) {
                            if (response.isSuccessful()) {
                                otherUser = Utilities.APIUserToUser(response.body().getData());
                            } else {
                                showErrorToast();
                            }
                            countDownLatch1.countDown();
                        }

                        @Override
                        public void onFailure(Call<APIResponse<APIUser>> call, Throwable t) {
                            showErrorToast();
                            countDownLatch1.countDown();
                        }
                    });

            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser = task.getResult().getValue(User.class);
                        } else {
                            showErrorToast();
                        }
                        countDownLatch1.countDown();
                    });

            try {
                countDownLatch1.await();
            } catch (InterruptedException e) {
                showErrorToast();
            }

            if (!loadSuccess) {
                runOnUiThread(this::hideLoading);
                return;
            }

            CountDownLatch countDownLatch2 = new CountDownLatch(2);

            APIUtil.getAPIService()
                    .getUserFollow(otherUser.getUsername())
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

            APIUtil.getAPIService()
                    .getAllPostByUsername(otherUser.getUsername())
                    .enqueue(new Callback<APIResponse<List<Post>>>() {
                        @Override
                        public void onResponse(Call<APIResponse<List<Post>>> call, Response<APIResponse<List<Post>>> response) {
                            if (response.isSuccessful()) {
                                postList = response.body().getData();
                            } else {
                                showErrorToast();
                            }

                            countDownLatch2.countDown();
                        }

                        @Override
                        public void onFailure(Call<APIResponse<List<Post>>> call, Throwable t) {
                            showErrorToast();
                            t.printStackTrace();
                            countDownLatch2.countDown();
                        }
                    });

            try {
                countDownLatch2.await();
            } catch (InterruptedException e) {
                showErrorToast();
            }

            if (!loadSuccess) {
                runOnUiThread(this::hideLoading);
                return;
            }

            OtherProfileActivity.this.runOnUiThread(() -> {
                hideLoading();
                if (!loadSuccess) {
                    return;
                }

                binding.otherProfileSvMain.setVisibility(View.VISIBLE);
                submitList();

                for (UserFollow userFollow : userFollowComposite.getFollower()) {
                    if (userFollow.getUsername().equals(currentUser.getUsername())) {
                        isFollowing = true;
                        followId = userFollow.getId();
                        break;
                    }
                }

                if (!TextUtils.isEmpty(otherUser.getProfilePicture())) {
                    Glide.with(OtherProfileActivity.this)
                            .load(otherUser.getProfilePicture())
                            .into(binding.otherProfileProfileCivDp);
                }

                binding.otherProfileProfileTvUsername.setText(otherUser.getUsername());
                binding.otherProfileProfileTvFullname.setText(otherUser.getFullName());

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

                setFollowButtonAndCount(isFollowing);

                binding.otherProfileBtnFollow.setOnClickListener(v -> new Thread(() -> {
                    runOnUiThread(() -> v.setEnabled(false));

                    CountDownLatch cdl1 = new CountDownLatch(1);

                    if (isFollowing) {
                        APIUtil.getAPIService()
                                .deleteUserFollow(followId)
                                .enqueue(new Callback<APIResponse<UserFollow>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<UserFollow>> call, Response<APIResponse<UserFollow>> response) {
                                        runOnUiThread(() -> {
                                            if (!response.isSuccessful()) {
                                                Utilities.makeToast(OtherProfileActivity.this, "Failed to unfollow");
                                            }
                                        });

                                        cdl1.countDown();
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<UserFollow>> call, Throwable t) {
                                        runOnUiThread(() -> Utilities.makeToast(OtherProfileActivity.this, "Failed to unfollow"));
                                        cdl1.countDown();
                                    }
                                });
                    } else {
                        APIUtil.getAPIService()
                                .createUserFollow(currentUser.getUsername(), otherUser.getUsername())
                                .enqueue(new Callback<APIResponse<FCMResponse>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<FCMResponse>> call, Response<APIResponse<FCMResponse>> response) {
                                        runOnUiThread(() -> {
                                            if (!response.isSuccessful()) {
                                                Utilities.makeToast(OtherProfileActivity.this, "Failed to follow");
                                            }
                                        });

                                        cdl1.countDown();
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<FCMResponse>> call, Throwable t) {
                                        runOnUiThread(() -> Utilities.makeToast(OtherProfileActivity.this, "Failed to follow"));
                                        cdl1.countDown();
                                    }
                                });
                    }

                    try {
                        cdl1.await();
                    } catch (InterruptedException e) {
                        runOnUiThread(() -> Utilities.makeToast(OtherProfileActivity.this, "Failed to follow"));
                        return;
                    }

                    APIUtil.getAPIService()
                            .getUserFollow(otherUser.getUsername())
                            .enqueue(new Callback<APIResponse<UserFollowComposite>>() {
                                @Override
                                public void onResponse(Call<APIResponse<UserFollowComposite>> call,
                                                       Response<APIResponse<UserFollowComposite>> responseFollow) {
                                    runOnUiThread(() -> {
                                        if (responseFollow.isSuccessful()) {
                                            userFollowComposite = responseFollow.body().getData();

                                            isFollowing = false;
                                            followId = -1;

                                            for (UserFollow userFollow : userFollowComposite.getFollower()) {
                                                if (userFollow.getUsername().equals(currentUser.getUsername())) {
                                                    isFollowing = true;
                                                    followId = userFollow.getId();
                                                    break;
                                                }
                                            }

                                            setFollowButtonAndCount(isFollowing);
                                            v.setEnabled(true);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<APIResponse<UserFollowComposite>> call, Throwable t) {
                                }
                            });
                }).start());

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

    private void setFollowButtonAndCount(boolean isFollowing) {
        if (isFollowing) {
            binding.otherProfileBtnFollow.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.grey));
            binding.otherProfileBtnFollow.setTextColor(Color.BLACK);
            binding.otherProfileBtnFollow.setText("Unfollow");
        } else {
            binding.otherProfileBtnFollow.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.blue_400));
            binding.otherProfileBtnFollow.setTextColor(Color.WHITE);
            binding.otherProfileBtnFollow.setText("Follow");
        }
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