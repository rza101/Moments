package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.pahat.moments.data.firebase.model.Chat;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ItemChatBinding;

public class FirebaseChatAdapter extends FirebaseRecyclerAdapter<Chat, FirebaseChatAdapter.ViewHolder> {
    private static final String TAG = FirebaseChatAdapter.class.getSimpleName();

    private User senderUser;
    private User receiverUser;

    public FirebaseChatAdapter(FirebaseRecyclerOptions<Chat> options, User senderUser, User receiverUser) {
        super(options);
        this.senderUser = senderUser;
        this.receiverUser = receiverUser;
    }

    @NonNull
    @Override
    public FirebaseChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat model) {
        Context context = holder.itemView.getContext();

        if (model.getSender().equals(senderUser.getUsername())) {
            // sender side
            holder.binding.itemChatClChatBoxLeft.setVisibility(View.GONE);
            holder.binding.itemChatCivProfileLeft.setVisibility(View.GONE);

            holder.binding.itemChatTvChatBoxRight.setVisibility(View.VISIBLE);
            holder.binding.itemChatCivProfileRight.setVisibility(View.VISIBLE);

            if(!TextUtils.isEmpty(senderUser.getProfilePicture())){
                Glide.with(context)
                        .load(senderUser.getProfilePicture())
                        .into(holder.binding.itemChatCivProfileRight);
            }

            if (model.getMessage() != null) {
                holder.binding.itemChatTvChatBoxRight.setVisibility(View.VISIBLE);
                holder.binding.itemChatIvImageRight.setVisibility(View.GONE);

                holder.binding.itemChatTvChatBoxRight.setText(model.getMessage());
            } else if (model.getImageUrl() != null) {
                holder.binding.itemChatTvChatBoxRight.setVisibility(View.GONE);
                holder.binding.itemChatIvImageRight.setVisibility(View.VISIBLE);

                if (model.getImageUrl().startsWith("gs://")) {
                    FirebaseStorage.getInstance()
                            .getReferenceFromUrl(model.getImageUrl())
                            .getDownloadUrl()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(context)
                                            .load(downloadUrl)
                                            .into(holder.binding.itemChatIvImageRight);
                                } else {
                                    Log.w(TAG, "Getting download url failed!", task.getException());
                                }
                            });
                } else {
                    Glide.with(context)
                            .load(model.getImageUrl())
                            .into(holder.binding.itemChatIvImageRight);
                }
            }
        } else {
            // receiver side
            holder.binding.itemChatClChatBoxRight.setVisibility(View.GONE);
            holder.binding.itemChatCivProfileRight.setVisibility(View.GONE);

            holder.binding.itemChatTvChatBoxLeft.setVisibility(View.VISIBLE);
            holder.binding.itemChatCivProfileLeft.setVisibility(View.VISIBLE);

            if(!TextUtils.isEmpty(receiverUser.getProfilePicture())){
                Glide.with(context)
                        .load(receiverUser.getProfilePicture())
                        .into(holder.binding.itemChatCivProfileLeft);
            }

            if (model.getMessage() != null) {
                holder.binding.itemChatTvChatBoxLeft.setVisibility(View.VISIBLE);
                holder.binding.itemChatIvImageLeft.setVisibility(View.GONE);

                holder.binding.itemChatTvChatBoxLeft.setText(model.getMessage());
            } else if (model.getImageUrl() != null) {
                holder.binding.itemChatTvChatBoxLeft.setVisibility(View.GONE);
                holder.binding.itemChatIvImageLeft.setVisibility(View.VISIBLE);

                if (model.getImageUrl().startsWith("gs://")) {
                    FirebaseStorage.getInstance()
                            .getReferenceFromUrl(model.getImageUrl())
                            .getDownloadUrl()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(context)
                                            .load(downloadUrl)
                                            .into(holder.binding.itemChatIvImageLeft);
                                } else {
                                    Log.w(TAG, "Getting download url failed!", task.getException());
                                }
                            });
                } else {
                    Glide.with(context)
                            .load(model.getImageUrl())
                            .into(holder.binding.itemChatIvImageLeft);
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemChatBinding binding;

        public ViewHolder(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
