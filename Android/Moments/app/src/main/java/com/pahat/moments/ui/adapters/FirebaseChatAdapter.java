package com.pahat.moments.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
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

            if (model.getMessage() != null) {
                holder.binding.itemChatIvImageRight.setVisibility(View.GONE);
                holder.binding.itemChatTvChatBoxRight.setText(model.getMessage());
            } else if (model.getImageUrl() != null) {
                holder.binding.itemChatClTextBoxRight.setVisibility(View.GONE);

                if (model.getImageUrl().startsWith("gs://")) {
                    FirebaseStorage.getInstance()
                            .getReferenceFromUrl(model.getImageUrl())
                            .getDownloadUrl()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(context)
                                            .load(downloadUrl)
                                            .override(Target.SIZE_ORIGINAL)
                                            .into(holder.binding.itemChatIvImageRight);
                                } else {
                                    Log.w(TAG, "Getting download url failed!", task.getException());
                                }
                            });
                } else {
                    Glide.with(context)
                            .load(model.getImageUrl())
                            .override(Target.SIZE_ORIGINAL)
                            .into(holder.binding.itemChatIvImageRight);
                }
            }
        } else {
            // receiver side
            holder.binding.itemChatClChatBoxRight.setVisibility(View.GONE);

            if (model.getMessage() != null) {
                holder.binding.itemChatIvImageLeft.setVisibility(View.GONE);
                holder.binding.itemChatTvChatBoxLeft.setText(model.getMessage());
            } else if (model.getImageUrl() != null) {
                holder.binding.itemChatClTextBoxLeft.setVisibility(View.GONE);

                if (model.getImageUrl().startsWith("gs://")) {
                    FirebaseStorage.getInstance()
                            .getReferenceFromUrl(model.getImageUrl())
                            .getDownloadUrl()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(context)
                                            .load(downloadUrl)
                                            .override(Target.SIZE_ORIGINAL)
                                            .into(holder.binding.itemChatIvImageLeft);
                                } else {
                                    Log.w(TAG, "Getting download url failed!", task.getException());
                                }
                            });
                } else {
                    Glide.with(context)
                            .load(model.getImageUrl())
                            .override(Target.SIZE_ORIGINAL)
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
