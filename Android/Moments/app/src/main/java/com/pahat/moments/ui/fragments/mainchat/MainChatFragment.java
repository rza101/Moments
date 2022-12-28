package com.pahat.moments.ui.fragments.mainchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.FragmentMainChatBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainChatFragment extends Fragment {

    private FragmentMainChatBinding binding;
    private ItemUserAdapter itemUserAdapter;

    private User currentUser;
    private List<Chat> chatList;

    private boolean loadSuccess = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemUserAdapter = new ItemUserAdapter(new OnItemClick<User>() {
            @Override
            public void onClick(View v, User data) {
                startActivity(new Intent(requireContext(), ChatActivity.class)
                        .putExtra(ChatActivity.USER_INTENT_KEY, data)
                );
            }
        });

        binding.fragmentMainChatRvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentMainChatRvChats.setAdapter(itemUserAdapter);

        new Thread(() -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(1);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_REF)
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
                return;
            }

            if (!loadSuccess) {
                return;
            }

            FirebaseDatabase.getInstance()
                    .getReference(Constants.FIREBASE_CHATS_REF)
                    .orderByChild("sender")
//                    .equalTo(currentUser.getUsername())
                    .equalTo("5lgV4E1oDVWSHtG3gPjflkHdXU32")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("chat", "onDataChange: " + snapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void goToChat(View v) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
    }

    private void showError() {
        requireActivity().runOnUiThread(() -> {
            Utilities.makeToast(requireActivity().getApplicationContext(), "Failed to get chats");
            loadSuccess = false;
        });
    }
}