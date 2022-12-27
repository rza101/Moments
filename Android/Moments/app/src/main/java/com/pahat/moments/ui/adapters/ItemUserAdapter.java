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
import com.pahat.moments.databinding.ItemUserBinding;
import com.pahat.moments.ui.OnItemClick;

import java.util.Objects;

public class ItemUserAdapter extends ListAdapter<User, ItemUserAdapter.ViewHolder> {
    private final OnItemClick<User> onItemClick;

    public ItemUserAdapter(OnItemClick<User> onItemClick) {
        super(new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return Objects.equals(oldItem.getUsername(), newItem.getUsername());
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                return false;
            }
        });

        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ItemUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemUserAdapter.ViewHolder holder, int position) {
        User user = getItem(position);
        Context context = holder.itemView.getContext();

        if (!TextUtils.isEmpty(user.getProfilePicture())) {
            Glide.with(context)
                    .load(user.getProfilePicture())
                    .into(holder.binding.itemUserCivDp);
        }

        holder.binding.itemUserTvUsername.setText(user.getUsername());
        holder.binding.itemUserTvFullname.setText(user.getFullName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(v, user);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        public ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
