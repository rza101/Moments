package com.pahat.moments.ui.fragments.mainchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.FragmentMainChatBinding;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.activities.chatadd.ChatAddActivity;
import com.pahat.moments.ui.adapters.ItemChatRoomAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainChatFragment extends Fragment {

    private FragmentMainChatBinding binding;
    private ItemChatRoomAdapter itemChatRoomAdapter;

    private User currentUser;
    private List<ChatRoom> chatRoomList;

    private boolean loadSuccess = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {
            itemChatRoomAdapter = new ItemChatRoomAdapter((v, data) -> {
                if (isAdded()) {
                    Intent intent = new Intent(requireContext(), ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ROOM_INTENT_KEY, data);
                    startActivity(intent);
                }
            });

            binding.fragmentMainChatRvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.fragmentMainChatRvChats.setAdapter(itemChatRoomAdapter);
            binding.fragmentMainChatLoadingLottie.setVisibility(View.GONE);
            binding.fragmentMainChatFabAdd.setOnClickListener(v -> {
                if (isAdded()) {
                    startActivity(new Intent(requireContext(), ChatAddActivity.class));
                }
            });

            new Thread(() -> {
                showLoading();

                CountDownLatch countDownLatch1 = new CountDownLatch(1);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(Constants.FIREBASE_USERS_DB_REF)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                currentUser = task.getResult().getValue(User.class);
                            } else {
                                showError();
                            }
                            countDownLatch1.countDown();
                        });

                try {
                    countDownLatch1.await();
                } catch (InterruptedException e) {
                    showError();
                }

                hideLoading();
                if (!loadSuccess) {
                    return;
                }

                FirebaseDatabase.getInstance()
                        .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                        .child(currentUser.getUserId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                chatRoomList = new ArrayList<>();

                                for (DataSnapshot child : snapshot.getChildren()) {
                                    ChatRoom chatRoom = child.getValue(ChatRoom.class);

                                    if (chatRoom.getLastMessage() != null) {
                                        chatRoom.setChatRoomId(child.getKey());
                                        chatRoomList.add(chatRoom);
                                    }
                                }

                                Collections.sort(chatRoomList, (o1, o2) ->
                                        (int) (o2.getLastMessageTimestamp() - o1.getLastMessageTimestamp()));

                                itemChatRoomAdapter.submitList(chatRoomList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                showError();
                            }
                        });
            }).start();
        }
    }

    private void showError() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                Utilities.makeToast(requireContext(), "Failed to show chats");
                loadSuccess = false;
            });
        }
    }

    public void showLoading() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> binding.fragmentMainChatLoadingLottie.setVisibility(View.VISIBLE));
        }
    }

    public void hideLoading() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> binding.fragmentMainChatLoadingLottie.setVisibility(View.GONE));
        }
    }
}