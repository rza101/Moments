package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.pahat.moments.R;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ItemPostBinding;
import com.pahat.moments.ui.OnItemClick;

public class ItemPostAdapter extends ListAdapter<Post, ItemPostAdapter.ViewHolder> {
    private final OnItemClick<Post> onItemClick;
    private final OnItemClick<Post> onMoreClick;

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
        this.onMoreClick = null;
    }

    public ItemPostAdapter(OnItemClick<Post> onItemClick, OnItemClick<Post> onMoreClick) {
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
        this.onMoreClick = onMoreClick;
    }

    @NonNull
    @Override
    public ItemPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPostAdapter.ViewHolder holder, int position) {
        Post data = getItem(position);
        Context context = holder.itemView.getContext();

        Glide.with(context)
                .load(data.getImageUrl())
                .placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_broken_image_24))
                .override(Target.SIZE_ORIGINAL)
                .into(holder.binding.itemPostIvPicture);

        holder.binding.itemPostTvCaption.setText(data.getCaption());
        if (onMoreClick != null) {
            holder.binding.itemPostIvMore.setOnClickListener(v -> onMoreClick.onClick(v, data));
        } else {
            holder.binding.itemPostIvMore.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> onItemClick.onClick(v, data));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        public ViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
