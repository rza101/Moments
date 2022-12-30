package com.pahat.moments.ui.activities.chatadd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityChatAddBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAddActivity extends AppCompatActivity {

    private ActivityChatAddBinding binding;
    private ItemUserAdapter itemUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemUserAdapter = new ItemUserAdapter((v, data) -> {
            Intent intent = new Intent(ChatAddActivity.this, ChatActivity.class);
            intent.putExtra(ChatActivity.USER_INTENT_KEY, data);
            startActivity(intent);
        });

        binding.chatAddRvResult.setLayoutManager(new LinearLayoutManager(this));
        binding.chatAddRvResult.setAdapter(itemUserAdapter);

        binding.chatAddSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utilities.makeToast(ChatAddActivity.this, query);
                APIUtil.getAPIService().getUserByUsernameOrFullName(query).enqueue(new Callback<APIResponse<List<APIUser>>>() {
                    @Override
                    public void onResponse(Call<APIResponse<List<APIUser>>> call, Response<APIResponse<List<APIUser>>> response) {
                        if (response.isSuccessful()) {
                            List<User> userList = new ArrayList<>();

                            for (APIUser apiUser : response.body().getData()) {
                                userList.add(new User(
                                        apiUser.getUserId(),
                                        apiUser.getUsername(),
                                        apiUser.getFullName(),
                                        apiUser.getImageUrl()
                                ));
                            }

                            itemUserAdapter.submitList(userList);
                        } else {
                            Utilities.makeToast(ChatAddActivity.this, "Failed to show search result");
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<List<APIUser>>> call, Throwable t) {
                        Utilities.makeToast(ChatAddActivity.this, "Failed to show search result");
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}