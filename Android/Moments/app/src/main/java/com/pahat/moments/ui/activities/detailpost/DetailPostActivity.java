package com.pahat.moments.ui.activities.detailpost;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.SavedPost;
import com.pahat.moments.data.network.model.UserFollow;
import com.pahat.moments.data.network.model.UserFollowComposite;
import com.pahat.moments.databinding.ActivityDetailPostBinding;
import com.pahat.moments.ui.activities.likelist.LikeListActivity;
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

    private User currentUser;

    private Post post;
    private User postUser;
    private PostComposite postComposite;
    private List<UserFollow> postUserFollowerList;

    private long likeId = -1;
    private boolean isLike = false;

    private long followId = -1;
    private boolean isFollowing = false;

    private long saveId = -1;
    private boolean isSaved = false;

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

        binding.detailPostIvBack.setOnClickListener(v -> finish());

        new Thread() {
            @Override
            public void run() {
                super.run();

                CountDownLatch countDownLatch1 = new CountDownLatch(2);

                FirebaseDatabase.getInstance().getReference()
                        .child(Constants.FIREBASE_USERS_REF)
                        .orderByChild("username")
                        .equalTo(post.getUsername())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                postUser = snapshot.getValue(User.class);
                                countDownLatch1.countDown();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                                countDownLatch1.countDown();
                            }
                        });

                FirebaseDatabase.getInstance().getReference()
                        .child(Constants.FIREBASE_USERS_REF)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                currentUser = snapshot.getValue(User.class);
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
                                    postUserFollowerList = responseFollow.body().getData().getFollower();
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

                CountDownLatch countDownLatch4 = new CountDownLatch(1);

                APIUtil.getAPIService()
                        .getAllSavedPosts(currentUser.getUsername())
                        .enqueue(new Callback<APIResponse<List<SavedPost>>>() {
                            @Override
                            public void onResponse(Call<APIResponse<List<SavedPost>>> call, Response<APIResponse<List<SavedPost>>> response) {
                                if (response.isSuccessful()) {
                                    for (SavedPost savedPost : response.body().getData()) {
                                        if (savedPost.getPostId() == post.getId()) {
                                            isSaved = true;
                                            saveId = savedPost.getId();
                                            break;
                                        }
                                    }
                                }
                                countDownLatch4.countDown();
                            }

                            @Override
                            public void onFailure(Call<APIResponse<List<SavedPost>>> call, Throwable t) {
                                countDownLatch4.countDown();
                            }
                        });

                try {
                    countDownLatch4.await();
                } catch (InterruptedException e) {
                    Utilities.makeToast(DetailPostActivity.this, "Failed to show post detail");
                    return;
                }

                runOnUiThread(() -> {
                    binding.detailPostClMain.setVisibility(View.VISIBLE);
                    for (UserFollow userFollow : postUserFollowerList) {
                        if (userFollow.getUsername().equals(currentUser.getUsername())) {
                            isFollowing = true;
                            followId = userFollow.getId();
                            break;
                        }
                    }

                    for (PostLike postLike : postComposite.getPostLike()) {
                        if (postLike.getUsername().equals(currentUser.getUsername())) {
                            isLike = true;
                            likeId = postLike.getId();
                            break;
                        }
                    }

                    itemCommentAdapter = new ItemCommentAdapter((v, data) -> {
                        Intent intent = new Intent(DetailPostActivity.this, OtherProfileActivity.class);
                        intent.putExtra(OtherProfileActivity.USER_INTENT_KEY, data);
                        startActivity(intent);
                    }, (v, data) -> {
                        if (data.getUsername().equals(currentUser.getUsername()) ||
                                currentUser.getUsername().equals(post.getUsername())) {
                            // pemilik comment ATAU yg punya post
                            PopupMenu popupMenu = new PopupMenu(DetailPostActivity.this, v);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();

                                    if (id == R.id.menu_popup_comment_delete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPostActivity.this);
                                        builder.setTitle("Delete Comment");
                                        builder.setMessage("Are you sure to delete this comment?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                APIUtil.getAPIService()
                                                        .deletePostComment(data.getId())
                                                        .enqueue(new Callback<APIResponse<PostComment>>() {
                                                            @Override
                                                            public void onResponse(Call<APIResponse<PostComment>> call, Response<APIResponse<PostComment>> response) {
                                                                if (response.isSuccessful()) {
                                                                    Utilities.makeToast(DetailPostActivity.this, "Comment deleted");

                                                                    APIUtil.getAPIService()
                                                                            .getPostById(String.valueOf(post.getId()))
                                                                            .enqueue(new Callback<APIResponse<PostComposite>>() {
                                                                                @Override
                                                                                public void onResponse(Call<APIResponse<PostComposite>> call,
                                                                                                       Response<APIResponse<PostComposite>> response) {
                                                                                    if (response.isSuccessful()) {
                                                                                        postComposite = response.body().getData();
                                                                                        itemCommentAdapter.submitList(postComposite.getPostComment());
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
                                                                                }
                                                                            });
                                                                } else {
                                                                    Utilities.makeToast(DetailPostActivity.this, "Failed to delete comment");
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<APIResponse<PostComment>> call, Throwable t) {
                                                                Utilities.makeToast(DetailPostActivity.this, "Failed to delete comment");
                                                            }
                                                        });
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        if (!DetailPostActivity.this.isFinishing()) {
                                            builder.create().show();
                                        }
                                        return true;
                                    }

                                    return false;
                                }
                            });

                            MenuInflater menuInflater = popupMenu.getMenuInflater();
                            menuInflater.inflate(R.menu.menu_popup_comment, popupMenu.getMenu());
                            popupMenu.show();
                        }

                        return false;
                    });

                    Glide.with(DetailPostActivity.this)
                            .load(post.getImageUrl())
                            .placeholder(AppCompatResources.getDrawable(DetailPostActivity.this,
                                    R.drawable.ic_broken_image_24))
                            .into(binding.detailPostIvPicture);

                    if (!TextUtils.isEmpty(postUser.getProfilePicture())) {
                        Glide.with(DetailPostActivity.this)
                                .load(postUser.getProfilePicture())
                                .into(binding.detailPostCivDp);
                    }

                    binding.detailPostTvCaptions.setText(post.getCaption());
                    binding.detailPostTvUsername.setText(post.getUsername());

                    binding.detailPostTvUsername.setOnClickListener(v -> {
                        Intent intent = new Intent(DetailPostActivity.this, OtherProfileActivity.class);
                        intent.putExtra(OtherProfileActivity.USER_INTENT_KEY, postUser);
                        startActivity(intent);
                    });

                    binding.detailPostTvLikeCount.setOnClickListener(v -> {
                        Intent intent = new Intent(DetailPostActivity.this, LikeListActivity.class);
                        intent.putExtra(DetailPostActivity.POST_INTENT_KEY, postComposite);
                        startActivity(intent);
                    });

                    if (post.getUsername().equals(currentUser.getUsername())) {
                        binding.detailPostBtnFollow.setVisibility(View.GONE);
                    }

                    setFollowButtonAndCount(isFollowing);
                    setLikeButtonAndCount(isLike);
                    setSavedButton(isSaved);

                    binding.detailPostRvComments.setLayoutManager(new LinearLayoutManager(DetailPostActivity.this));
                    binding.detailPostRvComments.setAdapter(itemCommentAdapter);
                    itemCommentAdapter.submitList(postComposite.getPostComment());

                    binding.detailPostBtnFollow.setOnClickListener(v -> {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

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
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to unfollow");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<UserFollow>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to unfollow"));
                                                    cdl1.countDown();
                                                }
                                            });
                                } else {
                                    APIUtil.getAPIService()
                                            .createUserFollow(currentUser.getUsername(), post.getUsername())
                                            .enqueue(new Callback<APIResponse<UserFollow>>() {
                                                @Override
                                                public void onResponse(Call<APIResponse<UserFollow>> call, Response<APIResponse<UserFollow>> response) {
                                                    runOnUiThread(() -> {
                                                        if (!response.isSuccessful()) {
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to follow");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<UserFollow>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to follow"));
                                                    cdl1.countDown();
                                                }
                                            });
                                }

                                try {
                                    cdl1.await();
                                } catch (InterruptedException e) {
                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to follow"));
                                    return;
                                }

                                APIUtil.getAPIService()
                                        .getUserFollow(post.getUsername())
                                        .enqueue(new Callback<APIResponse<UserFollowComposite>>() {
                                            @Override
                                            public void onResponse(Call<APIResponse<UserFollowComposite>> call,
                                                                   Response<APIResponse<UserFollowComposite>> responseFollow) {
                                                runOnUiThread(() -> {
                                                    if (responseFollow.isSuccessful()) {
                                                        postUserFollowerList = responseFollow.body().getData().getFollower();

                                                        isFollowing = false;
                                                        followId = -1;

                                                        for (UserFollow userFollow : postUserFollowerList) {
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
                            }
                        }.start();
                    });

                    binding.detailPostIvLike.setOnClickListener(v -> {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                runOnUiThread(() -> v.setEnabled(false));

                                CountDownLatch cdl1 = new CountDownLatch(1);

                                if (isLike) {
                                    APIUtil.getAPIService()
                                            .deletePostLike(likeId)
                                            .enqueue(new Callback<APIResponse<PostLike>>() {
                                                @Override
                                                public void onResponse(Call<APIResponse<PostLike>> call, Response<APIResponse<PostLike>> response) {
                                                    runOnUiThread(() -> {
                                                        if (!response.isSuccessful()) {
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to remove like");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<PostLike>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to remove like"));
                                                    cdl1.countDown();
                                                }
                                            });
                                } else {
                                    APIUtil.getAPIService()
                                            .createPostLike(post.getId(), currentUser.getUsername())
                                            .enqueue(new Callback<APIResponse<PostLike>>() {
                                                @Override
                                                public void onResponse(Call<APIResponse<PostLike>> call, Response<APIResponse<PostLike>> response) {
                                                    runOnUiThread(() -> {
                                                        if (!response.isSuccessful()) {
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to like");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<PostLike>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to like"));
                                                    cdl1.countDown();
                                                }
                                            });
                                }

                                try {
                                    cdl1.await();
                                } catch (InterruptedException e) {
                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to like"));
                                    return;
                                }

                                APIUtil.getAPIService()
                                        .getPostById(String.valueOf(post.getId()))
                                        .enqueue(new Callback<APIResponse<PostComposite>>() {
                                            @Override
                                            public void onResponse(Call<APIResponse<PostComposite>> call,
                                                                   Response<APIResponse<PostComposite>> response) {
                                                runOnUiThread(() -> {
                                                    if (response.isSuccessful()) {
                                                        postComposite = response.body().getData();

                                                        isLike = false;
                                                        likeId = -1;

                                                        for (PostLike postLike : postComposite.getPostLike()) {
                                                            if (postLike.getUsername().equals(currentUser.getUsername())) {
                                                                isLike = true;
                                                                likeId = postLike.getId();
                                                                break;
                                                            }
                                                        }

                                                        setLikeButtonAndCount(isLike);
                                                        v.setEnabled(true);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
                                            }
                                        });
                            }
                        }.start();
                    });

                    binding.detailPostIvSendComment.setOnClickListener(v -> {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                runOnUiThread(() -> {
                                    String comment = binding.detailPostEtComment.getText().toString();

                                    if (TextUtils.isEmpty(comment)) {
                                        Utilities.makeToast(DetailPostActivity.this, "Comment cannot be empty");
                                        return;
                                    }

                                    binding.detailPostEtComment.setEnabled(false);
                                    v.setEnabled(false);
                                });

                                CountDownLatch cdl1 = new CountDownLatch(1);

                                APIUtil.getAPIService()
                                        .createPostComment(post.getId(),
                                                currentUser.getUsername(),
                                                binding.detailPostEtComment.getText().toString()
                                        )
                                        .enqueue(new Callback<APIResponse<PostComment>>() {
                                            @Override
                                            public void onResponse(Call<APIResponse<PostComment>> call, Response<APIResponse<PostComment>> response) {
                                                runOnUiThread(() -> {
                                                    if (response.isSuccessful()) {
                                                        Utilities.makeToast(DetailPostActivity.this, "Comment sent!");
                                                    } else {
                                                        Utilities.makeToast(DetailPostActivity.this, "Failed to send comment");
                                                    }
                                                });

                                                cdl1.countDown();
                                            }

                                            @Override
                                            public void onFailure(Call<APIResponse<PostComment>> call, Throwable t) {
                                                runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to send comment"));
                                                cdl1.countDown();
                                            }
                                        });

                                try {
                                    cdl1.await();
                                } catch (InterruptedException e) {
                                    Utilities.makeToast(DetailPostActivity.this, "Failed to send comment");
                                    return;
                                }

                                runOnUiThread(() -> {
                                    binding.detailPostEtComment.setText("");
                                    binding.detailPostEtComment.setEnabled(true);
                                    v.setEnabled(true);
                                });

                                APIUtil.getAPIService()
                                        .getPostById(String.valueOf(post.getId()))
                                        .enqueue(new Callback<APIResponse<PostComposite>>() {
                                            @Override
                                            public void onResponse(Call<APIResponse<PostComposite>> call,
                                                                   Response<APIResponse<PostComposite>> response) {
                                                if (response.isSuccessful()) {
                                                    postComposite = response.body().getData();
                                                    itemCommentAdapter.submitList(postComposite.getPostComment());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<APIResponse<PostComposite>> call, Throwable t) {
                                            }
                                        });
                            }
                        }.start();
                    });

                    binding.detailPostIvSave.setOnClickListener(v -> {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                runOnUiThread(() -> v.setEnabled(false));

                                CountDownLatch cdl1 = new CountDownLatch(1);

                                if (isSaved) {
                                    APIUtil.getAPIService()
                                            .deleteSavedPost(saveId)
                                            .enqueue(new Callback<APIResponse<Post>>() {
                                                @Override
                                                public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                                                    runOnUiThread(() -> {
                                                        if (!response.isSuccessful()) {
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to unsave");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to unsave"));
                                                    cdl1.countDown();
                                                }
                                            });
                                } else {
                                    APIUtil.getAPIService()
                                            .createSavedPost(currentUser.getUsername(), post.getId())
                                            .enqueue(new Callback<APIResponse<Post>>() {
                                                @Override
                                                public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                                                    runOnUiThread(() -> {
                                                        if (!response.isSuccessful()) {
                                                            Utilities.makeToast(DetailPostActivity.this, "Failed to save");
                                                        }
                                                    });

                                                    cdl1.countDown();
                                                }

                                                @Override
                                                public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to save"));
                                                    cdl1.countDown();
                                                }
                                            });
                                }

                                try {
                                    cdl1.await();
                                } catch (InterruptedException e) {
                                    runOnUiThread(() -> Utilities.makeToast(DetailPostActivity.this, "Failed to save"));
                                    return;
                                }

                                APIUtil.getAPIService()
                                        .getAllSavedPosts(currentUser.getUsername())
                                        .enqueue(new Callback<APIResponse<List<SavedPost>>>() {
                                            @Override
                                            public void onResponse(Call<APIResponse<List<SavedPost>>> call, Response<APIResponse<List<SavedPost>>> response) {
                                                if (response.isSuccessful()) {
                                                    isSaved = false;
                                                    saveId = -1;

                                                    for (SavedPost savedPost : response.body().getData()) {
                                                        if (savedPost.getPostId() == post.getId()) {
                                                            isSaved = true;
                                                            saveId = savedPost.getId();
                                                            break;
                                                        }
                                                    }

                                                    setSavedButton(isSaved);
                                                    v.setEnabled(true);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<APIResponse<List<SavedPost>>> call, Throwable t) {

                                            }
                                        });
                            }
                        }.start();
                    });
                });
            }
        }.start();
    }

    private void setFollowButtonAndCount(boolean isFollowing) {
        if (isFollowing) {
            binding.detailPostBtnFollow.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.grey));
            binding.detailPostBtnFollow.setTextColor(Color.BLACK);
            binding.detailPostBtnFollow.setText("Unfollow");
        } else {
            binding.detailPostBtnFollow.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.blue));
            binding.detailPostBtnFollow.setTextColor(Color.WHITE);
            binding.detailPostBtnFollow.setText("Follow");
        }
        binding.detailPostTvFollowers.setText(String.format(getString(R.string.followers_count), Utilities.numberToText(postUserFollowerList.size())));
    }

    private void setLikeButtonAndCount(boolean isLike) {
        if (isLike) {
            binding.detailPostIvLike.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_favorite_24));
        } else {
            binding.detailPostIvLike.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_favorite_border_24));
        }
        binding.detailPostTvLikeCount.setText(String.format(getString(R.string.likes_count), postComposite.getPostLike().size()));
    }

    private void setSavedButton(boolean isSaved) {
        if (isSaved) {
            binding.detailPostIvSave.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_bookmark_24));
        } else {
            binding.detailPostIvSave.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_bookmark_border_24));
        }
    }
}