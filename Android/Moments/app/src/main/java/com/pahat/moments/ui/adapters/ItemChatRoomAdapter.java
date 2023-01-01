package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ItemChatRoomBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.util.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemChatRoomAdapter extends ListAdapter<ChatRoom, ItemChatRoomAdapter.ViewHolder> {
    private final OnItemClick<ChatRoom> onItemClick;

    public ItemChatRoomAdapter(OnItemClick<ChatRoom> onItemClick) {
        super(new DiffUtil.ItemCallback<ChatRoom>() {
            @Override
            public boolean areItemsTheSame(@NonNull ChatRoom oldItem, @NonNull ChatRoom newItem) {
                return oldItem.getChatRoomId().equals(newItem.getChatRoomId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ChatRoom oldItem, @NonNull ChatRoom newItem) {
                return false;
            }
        });

        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ItemChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemChatRoomAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ChatRoom data = getItem(position);

        User user = data.getParticipants().entrySet().iterator().next().getValue();

        if (!TextUtils.isEmpty(user.getProfilePicture())) {
            Glide.with(context)
                    .load(user.getProfilePicture())
                    .into(holder.binding.itemChatRoomCivDp);
        }

        holder.binding.itemChatRoomTvUsername.setText(user.getUsername());
        holder.binding.itemChatRoomTvMessage.setText(data.getLastMessage());
        holder.binding.itemChatRoomTvTime.setText(Utilities.timestampToPrettyDate(data.getLastMessageTimestamp()));

        holder.itemView.setOnClickListener(v -> onItemClick.onClick(v, data));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemChatRoomBinding binding;

        public ViewHolder(ItemChatRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
