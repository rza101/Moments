package com.pahat.moments.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ItemPostBinding;
import com.pahat.moments.ui.OnItemClick;

public class ItemPostAdapter extends ListAdapter<Post, ItemPostAdapter.ViewHolder> {
    private final OnItemClick<Post> onItemClick;

    public ItemPostAdapter(OnItemClick<Post> onItemClick) {
        super(new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return false;
            }
        });
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ItemPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPostAdapter.ViewHolder holder, int position) {
        Post data = getItem(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(data);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        public ViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
