package com.pahat.moments.ui.activities.chatadd;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.ActivityChatAddBinding;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAddActivity extends AppCompatActivity {

    private ActivityChatAddBinding binding;
    private ItemUserAdapter itemUserAdapter;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.initChildToolbar(this, binding.toolbar, "Add Chat");
        new Thread(() -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser = task.getResult().getValue(User.class);
                        }
                        countDownLatch.countDown();
                    });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                itemUserAdapter = new ItemUserAdapter((v, data) -> new Thread(() -> {
                    CountDownLatch countDownLatch1 = new CountDownLatch(1);

                    ChatRoom[] extraChatRoom = new ChatRoom[1];

                    boolean[] isRoomExists = new boolean[1];
                    boolean[] isOtherRoomExists = new boolean[1];
                    boolean[] isSuccess = {true};

                    // ASSUMED NO ROOM IS DELETED!
                    // CHECK NODE
                    FirebaseDatabase.getInstance()
                            .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                            .child(currentUser.getUserId())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot child : task.getResult().getChildren()) {
                                        ChatRoom chatRoom = child.getValue(ChatRoom.class);
                                        chatRoom.setChatRoomId(child.getKey());
                                        User user = chatRoom.getParticipants().entrySet().iterator().next().getValue();

                                        if (user.getUsername().equals(data.getUsername())) {
                                            isRoomExists[0] = true;
                                            extraChatRoom[0] = chatRoom;
                                            break;
                                        }
                                    }
                                } else {
                                    isSuccess[0] = false;
                                }

                                countDownLatch1.countDown();
                            });

                    try {
                        countDownLatch1.await();
                    } catch (InterruptedException e) {
                        isSuccess[0] = false;
                        e.printStackTrace();
                    }

                    if (!isSuccess[0]) {
                        runOnUiThread(() -> Utilities.makeToast(ChatAddActivity.this, "Failed to setup chat"));
                        return;
                    }

                    // ADD NODE IF NOT EXISTS
                    if (!isRoomExists[0]) {
                        CountDownLatch countDownLatch2 = new CountDownLatch(1);

                        Map<String, User> currentUserMap = new HashMap<>();
                        currentUserMap.put(data.getUserId(), data);

                        FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                                .child(currentUser.getUserId())
                                .push()
                                .setValue(new ChatRoom(currentUserMap))
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // REFRESH ROOM NODE
                                        FirebaseDatabase.getInstance()
                                                .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                                                .child(currentUser.getUserId())
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        for (DataSnapshot child : task1.getResult().getChildren()) {
                                                            ChatRoom chatRoom = child.getValue(ChatRoom.class);
                                                            chatRoom.setChatRoomId(child.getKey());
                                                            User user = chatRoom.getParticipants().entrySet().iterator().next().getValue();

                                                            if (user.getUsername().equals(data.getUsername())) {
                                                                extraChatRoom[0] = chatRoom;
                                                                break;
                                                            }
                                                        }
                                                    } else {
                                                        isSuccess[0] = false;
                                                    }
                                                    countDownLatch2.countDown();
                                                });
                                    } else {
                                        isSuccess[0] = false;
                                        countDownLatch2.countDown();
                                    }
                                });

                        try {
                            countDownLatch2.await();
                        } catch (InterruptedException e) {
                            isSuccess[0] = false;
                            e.printStackTrace();
                        }
                    }

                    if (!isSuccess[0]) {
                        runOnUiThread(() -> Utilities.makeToast(ChatAddActivity.this, "Failed to setup chat"));
                        return;
                    }

                    CountDownLatch countDownLatch3 = new CountDownLatch(1);

                    // CHECK OTHER NODE
                    FirebaseDatabase.getInstance()
                            .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                            .child(data.getUserId())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DataSnapshot child : task.getResult().getChildren()) {
                                        ChatRoom chatRoom = child.getValue(ChatRoom.class);
                                        User user = chatRoom.getParticipants().entrySet().iterator().next().getValue();

                                        if (user.getUsername().equals(data.getUsername())) {
                                            isOtherRoomExists[0] = true;
                                            break;
                                        }
                                    }
                                } else {
                                    isSuccess[0] = false;
                                }

                                countDownLatch3.countDown();
                            });

                    try {
                        countDownLatch3.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!isSuccess[0]) {
                        runOnUiThread(() -> Utilities.makeToast(ChatAddActivity.this, "Failed to setup chat"));
                        return;
                    }

                    // ADD OTHER NODE IF NOT EXISTS
                    if (!isOtherRoomExists[0]) {
                        CountDownLatch countDownLatch4 = new CountDownLatch(1);

                        Map<String, User> otherUserMap = new HashMap<>();
                        otherUserMap.put(currentUser.getUserId(), currentUser);

                        FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                                .child(data.getUserId())
                                .child(extraChatRoom[0].getChatRoomId())
                                .setValue(new ChatRoom(otherUserMap))
                                .addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        isSuccess[0] = false;
                                    }
                                    countDownLatch4.countDown();
                                });

                        try {
                            countDownLatch3.await();
                        } catch (InterruptedException e) {
                            isSuccess[0] = false;
                            e.printStackTrace();
                        }
                    }

                    if (!isSuccess[0]) {
                        runOnUiThread(() -> Utilities.makeToast(ChatAddActivity.this, "Failed to setup chat"));
                        return;
                    }

                    Intent intent = new Intent(ChatAddActivity.this, ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ROOM_INTENT_KEY, extraChatRoom[0]);
                    startActivity(intent);
                    finish();
                }).start());

                binding.chatAddRvResult.setLayoutManager(new LinearLayoutManager(this));
                binding.chatAddRvResult.setAdapter(itemUserAdapter);

                binding.chatAddSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        APIUtil.getAPIService().getUserByUsernameOrFullName(query).enqueue(new Callback<APIResponse<List<APIUser>>>() {
                            @Override
                            public void onResponse(Call<APIResponse<List<APIUser>>> call, Response<APIResponse<List<APIUser>>> response) {
                                if (response.isSuccessful()) {
                                    List<User> userList = new ArrayList<>();

                                    for (APIUser apiUser : response.body().getData()) {
                                        if (!apiUser.getUsername().equals(currentUser.getUsername())) {
                                            userList.add(new User(
                                                    apiUser.getUserId(),
                                                    apiUser.getUsername(),
                                                    apiUser.getFullName(),
                                                    apiUser.getImageUrl()
                                            ));
                                        }
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

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            });
        }).start();
    }
}