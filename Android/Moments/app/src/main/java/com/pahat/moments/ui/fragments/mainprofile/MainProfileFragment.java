package com.pahat.moments.ui.fragments.mainprofile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.pahat.moments.databinding.FragmentMainProfileBinding;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.activities.updatepost.UpdatePostActivity;
import com.pahat.moments.ui.activities.userlist.UserListActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainProfileFragment extends Fragment {

    private FragmentMainProfileBinding binding;
    private ItemPostAdapter itemPostAdapter;

    private User user;
    private UserFollowComposite userFollowComposite;
    private List<Post> postList;

    private boolean loadSuccess = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fragmentMainProfileTvFollowers.setVisibility(View.GONE);
        binding.fragmentMainProfileCl.setVisibility(View.GONE);

        itemPostAdapter = new ItemPostAdapter((v, data) -> {
            startActivity(new Intent(requireContext(), DetailPostActivity.class)
                    .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
            );
        }, (v, data) -> {

            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_popup_post_profile_edit) {
                    startActivity(new Intent(requireContext(), UpdatePostActivity.class)
                            .putExtra(UpdatePostActivity.POST_INTENT_KEY, data)
                    );
                    return true;
                } else if (id == R.id.menu_popup_post_profile_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Delete Post");
                    builder.setMessage("Are you sure to delete post");
                    builder.setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                        CountDownLatch cdl1 = new CountDownLatch(1);

                        showLoading();
                        APIUtil.getAPIService()
                                .deletePost(data.getId())
                                .enqueue(new Callback<APIResponse<Post>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<Post>> call, Response<APIResponse<Post>> response) {
                                        if (response.isSuccessful()) {
                                            requireActivity().runOnUiThread(() -> {
                                                Utilities.makeToast(requireContext(), "Post deleted");
                                            });
                                        } else {
                                            showErrorDeleteToast();
                                        }
                                        cdl1.countDown();
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<Post>> call, Throwable t) {
                                        showErrorDeleteToast();
                                        cdl1.countDown();
                                    }
                                });

                        try {
                            cdl1.await();
                        } catch (InterruptedException e) {
                            showErrorDeleteToast();
                            return;
                        }

                        APIUtil.getAPIService()
                                .getAllPostByUsername(user.getUsername())
                                .enqueue(new Callback<APIResponse<List<Post>>>() {
                                    @Override
                                    public void onResponse(Call<APIResponse<List<Post>>> call, Response<APIResponse<List<Post>>> response) {
                                        hideLoading();
                                        if (response.isSuccessful()) {
                                            postList = response.body().getData();
                                            submitList();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<APIResponse<List<Post>>> call, Throwable t) {
                                        hideLoading();
                                    }
                                });
                    }).start());
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                    if (!requireActivity().isFinishing()) {
                        builder.create().show();
                    }

                    return true;
                }

                return false;
            });

            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_popup_post_profile, popupMenu.getMenu());
            popupMenu.show();
        });

        binding.fragmentMainProfileRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.fragmentMainProfileRvPosts.setAdapter(itemPostAdapter);
        showLoading();
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


            requireActivity().runOnUiThread(() -> {
                if (!loadSuccess) {
                    return;
                }

                binding.fragmentMainProfileCl.setVisibility(View.VISIBLE);
                hideLoading();
                submitList();

                if (!TextUtils.isEmpty(user.getProfilePicture())) {
                    Glide.with(requireContext())
                            .load(user.getProfilePicture())
                            .into(binding.fragmentMainProfileCivDp);
                }

                binding.fragmentMainProfileTvUsername.setText(user.getUsername());
                binding.fragmentMainProfileTvFullname.setText(user.getFullName());

                binding.fragmentMainProfileTvFollowers.setText(
                        String.format(getString(R.string.followers_count),
                                Utilities.numberToText(userFollowComposite.getFollower().size())
                        )
                );

                binding.fragmentMainProfileTvFollowing.setText(
                        String.format(getString(R.string.following_count),
                                Utilities.numberToText(userFollowComposite.getFollowing().size())
                        )
                );

                binding.fragmentMainProfileTvFollowers.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), UserListActivity.class);
                    intent.putExtra(UserListActivity.TYPE_INTENT_KEY, UserListActivity.TYPE_FOLLOWER);
                    intent.putExtra(UserListActivity.USER_LIST_INTENT_KEY, Utilities.followerListToUserList(userFollowComposite.getFollower()));
                    startActivity(intent);
                });

                binding.fragmentMainProfileTvFollowing.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), UserListActivity.class);
                    intent.putExtra(UserListActivity.TYPE_INTENT_KEY, UserListActivity.TYPE_FOLLOWING);
                    intent.putExtra(UserListActivity.USER_LIST_INTENT_KEY, Utilities.followingListToUserList(userFollowComposite.getFollowing()));
                    startActivity(intent);
                });
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showErrorToast() {
        requireActivity().runOnUiThread(() -> {
            Utilities.makeToast(requireContext(), "Failed to show profile");
            loadSuccess = false;
        });
    }

    private void showErrorDeleteToast() {
        requireActivity().runOnUiThread(() -> {
            Utilities.makeToast(requireContext(), "Failed to delete post");
            loadSuccess = false;
        });
    }

    private void submitList() {
        binding.fragmentMainProfileTvNoPost.setVisibility(postList.size() == 0 ? View.VISIBLE : View.GONE);
        itemPostAdapter.submitList(postList);
    }

    public void showLoading() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.fragmentMainProfileLoadingLottie.setVisibility(View.VISIBLE);
            }
        });

    }

    public void hideLoading() {

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.fragmentMainProfileLoadingLottie.setVisibility(View.GONE);            }
        });
    }
}