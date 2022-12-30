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
import com.pahat.moments.data.firebase.model.Chat;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.FragmentMainChatBinding;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.adapters.ItemChatRoomAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        itemChatRoomAdapter = new ItemChatRoomAdapter((v, data) -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.USER_INTENT_KEY,
                    new User(null,
                            data.getUsername(),
                            data.getFullname(),
                            data.getProfilePicture())
            );
            startActivity(intent);
        });


        binding.fragmentMainChatRvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentMainChatRvChats.setAdapter(itemChatRoomAdapter);

        new Thread(() -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(1);

            FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USERS_DB_REF).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
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
                return;
            }

            if (!loadSuccess) {
                return;
            }

            FirebaseDatabase.getInstance()
                    .getReference(Constants.FIREBASE_CHATS_DB_REF)
                    .orderByChild("sender")
                    .equalTo(currentUser.getUsername())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Chat> chats = new ArrayList<>();

                            for (DataSnapshot snapshotItem : snapshot.getChildren()) {
                                chats.add(snapshotItem.getValue(Chat.class));
                            }

                            FirebaseDatabase.getInstance()
                                    .getReference(Constants.FIREBASE_CHATS_DB_REF)
                                    .orderByChild("receiver")
                                    .equalTo(currentUser.getUsername())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            for (DataSnapshot snapshotItem2 : task.getResult().getChildren()) {
                                                chats.add(snapshotItem2.getValue(Chat.class));
                                            }

                                            Collections.sort(chats, (Comparator<Chat>) (o1, o2) -> {
                                                if (o1.getTimestamp() == 0) {
                                                    return -1;
                                                } else if (o2.getTimestamp() == 0) {
                                                    return 0;
                                                } else {
                                                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                                                }
                                            });

                                            Set<ChatRoom> chatRoomSet = new HashSet<>();

                                            for (Chat chat : chats) {
                                                // karena sorted descending jadi operasi set tdk
                                                // menghilangkan message terbaru
                                                if (chat.getSender().equals(currentUser.getUsername())){
                                                    chatRoomSet.add(new ChatRoom(
                                                            chat.getReceiver(),
                                                            chat.getReceiverFullName(),
                                                            chat.getReceiverProfilePicture(),
                                                            chat.getMessage(),
                                                            chat.getTimestamp()
                                                    ));
                                                }else{
                                                    chatRoomSet.add(new ChatRoom(
                                                            chat.getSender(),
                                                            chat.getSenderFullName(),
                                                            chat.getSenderProfilePicture(),
                                                            chat.getMessage(),
                                                            chat.getTimestamp()
                                                    ));
                                                }
                                            }

                                            itemChatRoomAdapter.submitList(new ArrayList<>(chatRoomSet));
                                        } else {
                                            Utilities.makeToast(requireContext(), "Failed to get chat data");
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            requireActivity().runOnUiThread(() -> Utilities.makeToast(requireContext(), "Failed to get chat data"));
                        }
                    });
        }).start();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showError() {
        requireActivity().runOnUiThread(() -> {
            Utilities.makeToast(requireContext(), "Failed to get chats");
            loadSuccess = false;
        });
    }
}