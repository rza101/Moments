package com.pahat.moments.ui.activities.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.firebase.model.UserList;
import com.pahat.moments.data.network.APIService;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.UserFollowComposite;
import com.pahat.moments.databinding.ActivityUserListBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    public static final String USER_LIST_INTENT_KEY = "USER_LIST_INTENT_KEY";
    public static final String TYPE_INTENT_KEY = "TYPE_INTENT_KEY";

    public static final int TYPE_LIKE = 1;
    public static final int TYPE_FOLLOWING = 2;
    public static final int TYPE_FOLLOWER = 3;

    private ItemUserAdapter itemUserAdapter;

    private ActivityUserListBinding binding;

    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!getIntent().hasExtra(USER_LIST_INTENT_KEY) ||
                !getIntent().hasExtra(TYPE_INTENT_KEY)) {
            Utilities.makeToast(this, "Invalid access");
            finish();
            return;
        }

        if (getIntent().getIntExtra(TYPE_INTENT_KEY, -1) < 1 ||
                getIntent().getIntExtra(TYPE_INTENT_KEY, -1) > 3) {
            Utilities.makeToast(this, "Invalid access");
            finish();
            return;
        }

        String title = "";
        int type = getIntent().getIntExtra(TYPE_INTENT_KEY, -1);
        switch (type) {
            case TYPE_LIKE:
                title = "Like";
                break;
            case TYPE_FOLLOWER:
                title = "Follower";
                break;
            case TYPE_FOLLOWING:
                title = "Following";
                break;
        }

        itemUserAdapter = new ItemUserAdapter(new OnItemClick<User>() {
            @Override
            public void onClick(View v, User data) {
                startActivity(new Intent(UserListActivity.this, OtherProfileActivity.class)
                        .putExtra(OtherProfileActivity.USER_INTENT_KEY, data)
                );
            }
        });

        userList = ((UserList) getIntent().getParcelableExtra(USER_LIST_INTENT_KEY)).getUserList();
        String username = userList.get(2).getUsername(); //butuh id user
        Utilities.initChildToolbar(this, binding.toolbar, title);

        if (type == TYPE_FOLLOWER || type == TYPE_FOLLOWING){
            Utilities.makeToast(UserListActivity.this, "Loading");
            APIUtil.getAPIService().getUserFollow(username).enqueue(new Callback<APIResponse<UserFollowComposite>>() {
                @Override
                public void onResponse(Call<APIResponse<UserFollowComposite>> call, Response<APIResponse<UserFollowComposite>> response) {
                    if (response.isSuccessful()){
                        switch (type) {
                            case TYPE_FOLLOWER:
                                itemUserAdapter.submitList(Utilities.followerListToUserList(response.body().getData().getFollower()).getUserList());
                                break;
                            case TYPE_FOLLOWING:
                                itemUserAdapter.submitList(Utilities.followingListToUserList(response.body().getData().getFollowing()).getUserList());
                                break;
                        }
                    }else {
                        Utilities.makeToast(UserListActivity.this, "Failed to connect");
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<UserFollowComposite>> call, Throwable t) {
                    Utilities.makeToast(UserListActivity.this, "Failed to connect");
                }
            });
        }
        if (type == TYPE_LIKE) {
            Utilities.makeToast(UserListActivity.this, "Loading");
            APIUtil.getAPIService().getAllPostLikes(1).enqueue(new Callback<APIResponse<List<PostLike>>>() {
                @Override
                public void onResponse(Call<APIResponse<List<PostLike>>> call, Response<APIResponse<List<PostLike>>> response) {
                    if (response.isSuccessful()){
                        itemUserAdapter.submitList(Utilities.likeListToUserList(response.body().getData()).getUserList());
                    } else {
                        Utilities.makeToast(UserListActivity.this, "Failed to connect");
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<List<PostLike>>> call, Throwable t) {
                    Utilities.makeToast(UserListActivity.this, "Failed to connect");
                }
            });
        }
        RecyclerView recyclerView = binding.likeListRvLikes;
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        recyclerView.setAdapter(itemUserAdapter);
    }
}