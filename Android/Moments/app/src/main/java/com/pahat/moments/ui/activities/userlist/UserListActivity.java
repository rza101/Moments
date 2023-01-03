package com.pahat.moments.ui.activities.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.firebase.model.UserList;
import com.pahat.moments.databinding.ActivityUserListBinding;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Utilities;

import java.util.List;

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
        binding.userListLoadingLottie.setVisibility(View.GONE);
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
        showLoading();

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
        hideLoading();

        itemUserAdapter = new ItemUserAdapter((v, data) ->
                startActivity(new Intent(UserListActivity.this, OtherProfileActivity.class)
                        .putExtra(OtherProfileActivity.USER_INTENT_KEY, data)
                )
        );

        userList = ((UserList) getIntent().getParcelableExtra(USER_LIST_INTENT_KEY)).getUserList();
        Utilities.initChildToolbar(this, binding.toolbar, title);
        itemUserAdapter.submitList(userList);

        RecyclerView recyclerView = binding.likeListRvLikes;
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        recyclerView.setAdapter(itemUserAdapter);
    }

    public void showLoading() {
        binding.userListLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.userListLoadingLottie.setVisibility(View.GONE);
    }
}