package com.pahat.moments.ui.activities.userlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.firebase.model.UserList;
import com.pahat.moments.databinding.ActivityUserListBinding;
import com.pahat.moments.util.Utilities;

import java.util.List;

public class UserListActivity extends AppCompatActivity {

    public static final String USER_LIST_INTENT_KEY = "USER_LIST_INTENT_KEY";
    public static final String TYPE_INTENT_KEY = "TYPE_INTENT_KEY";

    public static final int TYPE_LIKE = 1;
    public static final int TYPE_FOLLOWING = 2;
    public static final int TYPE_FOLLOWER = 3;

    private ActivityUserListBinding binding;

    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!getIntent().hasExtra(USER_LIST_INTENT_KEY) ||
                !getIntent().hasExtra(TYPE_INTENT_KEY)) {
            Utilities.makeToast(getApplicationContext(), "Invalid access");
            finish();
        }

        if (getIntent().getIntExtra(TYPE_INTENT_KEY, -1) < 1 ||
                getIntent().getIntExtra(TYPE_INTENT_KEY, -1) > 3) {
            Utilities.makeToast(getApplicationContext(), "Invalid access");
            finish();
        }

        String title = "";

        switch (getIntent().getIntExtra(TYPE_INTENT_KEY, -1)) {
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

        userList = ((UserList) getIntent().getParcelableExtra(USER_LIST_INTENT_KEY)).getUserList();

        Utilities.initChildToolbar(this, binding.toolbar, title);
    }
}