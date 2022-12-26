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

import com.pahat.moments.databinding.FragmentMainChatBinding;
import com.pahat.moments.ui.activities.chat.ChatActivity;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.main.MainActivity;

public class MainChatFragment extends Fragment {

    private FragmentMainChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fragmentMainChatRvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentMainChatBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChat(view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void goToChat(View v)
    {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
    }
}