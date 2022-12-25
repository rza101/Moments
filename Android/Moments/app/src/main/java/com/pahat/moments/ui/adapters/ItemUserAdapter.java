package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ItemUserBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.util.Constants;

import java.util.Objects;

public class ItemUserAdapter extends ListAdapter<User, ItemUserAdapter.ViewHolder> {
    private final OnItemClick<User> onItemClick;

    protected ItemUserAdapter(OnItemClick<User> onItemClick) {
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

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.FIREBASE_USERS_REF)
                .orderByChild("username")
                .equalTo(user.getUsername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User snapshotUser = snapshot.getValue(User.class);

                        if (snapshotUser.getProfilePicture() != null) {
                            Glide.with(context)
                                    .load(snapshotUser.getProfilePicture())
                                    .into(holder.binding.itemUserCivDp);
                        }

                        holder.binding.itemUserTvUsername.setText(user.getUsername());
                        holder.binding.itemUserTvFullname.setText(snapshotUser.getFullName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClick.onClick(v, user);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
