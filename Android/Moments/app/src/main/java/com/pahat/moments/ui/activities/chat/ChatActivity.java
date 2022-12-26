package com.pahat.moments.ui.activities.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIService;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.ChatMessage;
import com.pahat.moments.data.network.model.Data;
import com.pahat.moments.data.network.model.Sender;
import com.pahat.moments.data.network.model.ViewData;
import com.pahat.moments.databinding.ActivityChatBinding;
import com.pahat.moments.databinding.ItemChatBinding;
import com.pahat.moments.ui.adapters.ChatViewHolder;
import com.pahat.moments.util.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    private static final String TAG = ChatActivity.class.getSimpleName();

    private String mUsername;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mRoot, mRef;
    private FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> mFirebaseAdapter;
    private String userId;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    final Uri uri = result.getData().getData();
                    Log.d(TAG, "Uri : " + uri.toString());

                    ChatMessage tempMessage = new ChatMessage(null, userId, LOADING_IMAGE_URL);
                    mRoot.child("messages").push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference = FirebaseStorage.getInstance()
                                                .getReference(mAuth.getCurrentUser().getUid())
                                                .child(key)
                                                .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write database", databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "fullname");

        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("messages");

        userId = mAuth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference();
        mRef = mRoot.child("users").child(userId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUsername = user.getFullName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mUsername = "Anonymous";
            }
        });

        binding.chatRvChat.setItemAnimator(null);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        binding.chatRvChat.setLayoutManager(mLinearLayoutManager);

        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @NonNull
            @Override
            public ChatMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                if (chatMessage != null) {
                    chatMessage.setId(snapshot.getKey());
                }
                return chatMessage;
            }
        };

        mRef = mRoot.child("messages");
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(mRef, parser)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull final ChatMessage model) {
                mRoot.child("users").child(model.getSender()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (model.getSender().equals(userId)) {
                            holder.itemChatBinding.itemChatClChatBoxLeft.setVisibility(View.GONE);
                            holder.itemChatBinding.itemChatCivProfileLeft.setVisibility(View.GONE);
                            holder.itemChatBinding.itemChatTvChatBoxRight.setVisibility(View.VISIBLE);
                            holder.itemChatBinding.itemChatCivProfileRight.setVisibility(View.VISIBLE);

                            if (model.getText() != null) {
                                holder.itemChatBinding.itemChatTvChatBoxRight.setText(model.getText());
                                holder.itemChatBinding.itemChatTvChatBoxRight.setVisibility(View.VISIBLE);
                                holder.itemChatBinding.itemChatIvImageRight.setVisibility(View.GONE);
                            } else if (model.getImageUrl() != null) {
                                String imageUrl = model.getImageUrl();
                                if (imageUrl.startsWith("gs://")) {
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUrl = task.getResult().toString();
                                                Glide.with(holder.itemChatBinding.itemChatIvImageRight.getContext())
                                                        .load(downloadUrl)
                                                        .into(holder.itemChatBinding.itemChatIvImageRight);
                                            } else {
                                                Log.w(TAG, "Getting download url failed!", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    Glide.with(holder.itemChatBinding.itemChatIvImageRight.getContext())
                                            .load(model.getImageUrl())
                                            .into(holder.itemChatBinding.itemChatIvImageRight);
                                }
                                holder.itemChatBinding.itemChatIvImageRight.setVisibility(View.VISIBLE);
                                holder.itemChatBinding.itemChatTvChatBoxRight.setVisibility(View.GONE);
                            }
                        } else {
                            holder.itemChatBinding.itemChatClChatBoxRight.setVisibility(View.GONE);
                            holder.itemChatBinding.itemChatCivProfileRight.setVisibility(View.GONE);
                            holder.itemChatBinding.itemChatTvChatBoxLeft.setVisibility(View.VISIBLE);
                            holder.itemChatBinding.itemChatCivProfileLeft.setVisibility(View.VISIBLE);

                            if (model.getText() != null) {
                                holder.itemChatBinding.itemChatTvChatBoxLeft.setText(model.getText());
                                holder.itemChatBinding.itemChatTvChatBoxLeft.setVisibility(View.VISIBLE);
                                holder.itemChatBinding.itemChatIvImageLeft.setVisibility(View.GONE);
                            } else if (model.getImageUrl() != null) {
                                String imageUrl = model.getImageUrl();
                                if (imageUrl.startsWith("gs://")) {
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUrl = task.getResult().toString();
                                                Glide.with(holder.itemChatBinding.itemChatIvImageLeft.getContext())
                                                        .load(downloadUrl)
                                                        .into(holder.itemChatBinding.itemChatIvImageLeft);
                                            } else {
                                                Log.w(TAG, "Getting download url failed!", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    Glide.with(holder.itemChatBinding.itemChatIvImageLeft.getContext())
                                            .load(model.getImageUrl())
                                            .into(holder.itemChatBinding.itemChatIvImageLeft);
                                }
                                holder.itemChatBinding.itemChatIvImageLeft.setVisibility(View.VISIBLE);
                                holder.itemChatBinding.itemChatTvChatBoxRight.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//                    return new ChatViewHolder(inflater.inflate(R.layout.item_chat, parent, false));
                return new ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                //Lottie?
            }
        };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int chatMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (chatMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        binding.chatRvChat.scrollToPosition(positionStart);
                    }
                }
            });

            binding.chatRvChat.setAdapter(mFirebaseAdapter);

            binding.chatEtMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() > 0) {
                        binding.chatIbSend.setEnabled(true);
                    } else {
                        binding.chatIbSend.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            binding.chatIbSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessage chatMessage = new ChatMessage(binding.chatEtMessage.getText().toString(),
                            userId,
                            null);
                    mRoot.child("messages").push().setValue(chatMessage);
                    binding.chatEtMessage.setText("");

                    Data data = new Data(mUsername, chatMessage.getText(), userId);
                    Sender sender = new Sender(data, "/topics/messages");
                    sendNotification(sender);
                }
            });

            binding.chatIbImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
                }
            });
        }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getMetadata().getReference().getDownloadUrl()
                            .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        ChatMessage chatMessage = new ChatMessage(null, userId, task.getResult().toString());
                                        mRoot.child("messages").child(key).setValue(chatMessage);

                                        Data data = new Data(mUsername, "Image Message", userId, task.getResult().toString());
                                        Sender sender = new Sender(data, "/topics/messages");
                                        sendNotification(sender);
                                    }
                                }
                            });
                } else {
                    Log.w(TAG, "Image upload task failed!", task.getException());
                }
            }
        });
    }

    private String getInitialName(String fullName) {
        String splitName[] = fullName.split("\\s+");
        int splitCount = splitName.length;

        if (splitCount == 1) {
            return "" + fullName.charAt(0) + fullName.charAt(0);
        } else {
            int firstSpace = fullName.indexOf(" ");
            String firstName = fullName.substring(0, firstSpace);

            int lastSpace = fullName.lastIndexOf(" ");
            String lastName = fullName.substring(lastSpace + 1);

            return "" + firstName.charAt(0) + lastName.charAt(0);
        }
    }

    private void sendNotification(Sender sender) {
        APIService api = APIUtil.getRetrofit().create(APIService.class);
        Call<ViewData> call = api.sendNotification(sender);
        call.enqueue(new Callback<ViewData>() {
            @Override
            public void onResponse(Call<ViewData> call, Response<ViewData> response) {
                if (response.code() == 200) {
                    System.out.println("Response : " + response.body().getMessage_id());
                    if (response.body().getMessage_id() != null) {
//                        Toast.makeText(ChatActivity.this, "Pesan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Pesan gagal dikirim!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Response " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ViewData> call, Throwable t) {
                System.out.println("Retrofit Error : " + t.getMessage());
                Toast.makeText(ChatActivity.this, "Retrofit Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}