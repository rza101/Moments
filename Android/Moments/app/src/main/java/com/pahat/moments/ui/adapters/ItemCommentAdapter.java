package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.model.PostComment;
import com.pahat.moments.databinding.ItemCommentBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.OnLongClick;

public class ItemCommentAdapter extends ListAdapter<PostComment, ItemCommentAdapter.ViewHolder> {
    private final OnItemClick<User> onUsernameClick;
    private final OnLongClick<PostComment> onLongClick;

    public ItemCommentAdapter(OnItemClick<User> onUsernameClick, OnLongClick<PostComment> onLongClick) {
        super(new DiffUtil.ItemCallback<PostComment>() {
            @Override
            public boolean areItemsTheSame(@NonNull PostComment oldItem, @NonNull PostComment newItem) {
                return oldItem.getId() == oldItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull PostComment oldItem, @NonNull PostComment newItem) {
                return false;
            }
        });

        this.onUsernameClick = onUsernameClick;
        this.onLongClick = onLongClick;
    }

    @NonNull
    @Override
    public ItemCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentAdapter.ViewHolder holder, int position) {
        PostComment postComment = getItem(position);
        Context context = holder.itemView.getContext();

        if (!TextUtils.isEmpty(postComment.getImageUrl())) {
            Glide.with(context)
                    .load(postComment.getImageUrl())
                    .into(holder.binding.itemCommentCivDp);
        }

        holder.binding.itemCommentTvUsername.setText(postComment.getUsername());
        holder.binding.itemCommentTvComment.setText(postComment.getComment());

        holder.binding.itemCommentTvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUsernameClick.onClick(v, new User(postComment.getUsername()));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onLongClick.onLongClick(v, postComment);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        public ViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
