package com.pahat.moments.ui.activities.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.pahat.moments.BuildConfig;
import com.pahat.moments.data.firebase.model.Chat;
import com.pahat.moments.data.firebase.model.ChatRoom;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.FCMResponse;
import com.pahat.moments.databinding.ActivityChatBinding;
import com.pahat.moments.ui.adapters.FirebaseChatAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public static final String CHAT_ROOM_INTENT_KEY = "USER_INTENT_KEY";
    private static final String TAG = ChatActivity.class.getSimpleName();
    private ActivityChatBinding binding;
    private FirebaseChatAdapter firebaseChatAdapter;

    private DatabaseReference chatRef, senderRoomRef, receiverRoomRef;

    private User senderUser;
    private User receiverUser;
    private ChatRoom chatRoom;

    private Uri tempCameraUri;

    private boolean loadSuccess = true;

    private ActivityResultLauncher<Intent> cameraIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                final Uri imageUri = tempCameraUri;
                sendImageMessage(imageUri);
            }
        }
    });

    private ActivityResultLauncher<Intent> galleryIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    final Uri imageUri = result.getData().getData();
                    sendImageMessage(imageUri);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!getIntent().hasExtra(CHAT_ROOM_INTENT_KEY)) {
            Utilities.makeToast(this, "Invalid access");
            finish();
            return;
        }

        chatRoom = getIntent().getParcelableExtra(CHAT_ROOM_INTENT_KEY);

        new Thread(() -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(2);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            senderUser = task.getResult().getValue(User.class);
                        } else {
                            Utilities.makeToast(ChatActivity.this, "Unable to get user detail");
                            loadSuccess = false;
                        }
                        countDownLatch1.countDown();
                    });

            FirebaseDatabase.getInstance()
                    .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(chatRoom.getChatRoomId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            chatRoom = task.getResult().getValue(ChatRoom.class);
                            chatRoom.setChatRoomId(task.getResult().getKey());
                        } else {
                            Utilities.makeToast(ChatActivity.this, "Unable to get room detail");
                            loadSuccess = false;
                        }
                        countDownLatch1.countDown();
                    });

            try {
                countDownLatch1.await();
            } catch (InterruptedException e) {
                Utilities.makeToast(ChatActivity.this, "Unable to get user detail");
                e.printStackTrace();
                return;
            }

            if (!loadSuccess) {
                return;
            }

            runOnUiThread(() -> {
                receiverUser = chatRoom.getParticipants().entrySet().iterator().next().getValue();

                Utilities.initChildToolbar(this, binding.toolbar, receiverUser.getFullName());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                linearLayoutManager.setStackFromEnd(true);

                chatRef = FirebaseDatabase.getInstance()
                        .getReference(Constants.FIREBASE_CHATS_DB_REF)
                        .child(chatRoom.getChatRoomId());
                senderRoomRef = FirebaseDatabase.getInstance()
                        .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                        .child(senderUser.getUserId())
                        .child(chatRoom.getChatRoomId());
                receiverRoomRef = FirebaseDatabase.getInstance()
                        .getReference(Constants.FIREBASE_CHAT_ROOMS_DB_REF)
                        .child(receiverUser.getUserId())
                        .child(chatRoom.getChatRoomId());

                FirebaseRecyclerOptions<Chat> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Chat>()
                        .setLifecycleOwner(ChatActivity.this)
                        .setQuery(chatRef, snapshot -> {
                            Chat chat = snapshot.getValue(Chat.class);

                            if (chat != null) {
                                chat.setChatId(snapshot.getKey());
                            }

                            return chat;
                        })
                        .build();

                firebaseChatAdapter = new FirebaseChatAdapter(firebaseRecyclerOptions, senderUser, receiverUser);
                firebaseChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);

                        int chatMessageCount = firebaseChatAdapter.getItemCount();
                        int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (chatMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            binding.chatRvChat.scrollToPosition(positionStart);
                        }
                    }
                });

                binding.chatRvChat.setLayoutManager(linearLayoutManager);
                binding.chatRvChat.setItemAnimator(null);
                binding.chatRvChat.setAdapter(firebaseChatAdapter);

                binding.chatEtMessage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        binding.chatIbSend.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                binding.chatIbSend.setOnClickListener(v -> {
                    String message = binding.chatEtMessage.getText().toString();
                    long timestamp = System.currentTimeMillis();

                    Chat chat = new Chat(
                            senderUser.getUsername(),
                            message,
                            null,
                            timestamp
                    );

                    chatRef.push().setValue(chat, (error, ref) -> {
                        if (error != null) {
                            Utilities.makeToast(ChatActivity.this, "Failed to send message");
                        } else {
                            HashMap<String, Object> lastMessageMap = new HashMap<>();
                            lastMessageMap.put("lastMessage", message);

                            HashMap<String, Object> lastTimestampMap = new HashMap<>();
                            lastTimestampMap.put("lastMessageTimestamp", timestamp);

                            senderRoomRef.updateChildren(lastMessageMap);
                            senderRoomRef.updateChildren(lastTimestampMap);
                            receiverRoomRef.updateChildren(lastMessageMap);
                            receiverRoomRef.updateChildren(lastTimestampMap);

                            sendNotification(receiverUser.getUsername(), message);
                        }
                    });

                    binding.chatEtMessage.setText("");
                });

                binding.chatIbImage.setOnClickListener(v -> {
                    galleryIntent.launch(
                            Intent.createChooser(
                                    new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                                    "Select an image"
                            )
                    );
                });

                binding.chatIbCamera.setOnClickListener(v -> {
                    File tempFile = Utilities.createTempImageFile(ChatActivity.this);
                    tempCameraUri = FileProvider.getUriForFile(ChatActivity.this,
                            BuildConfig.APPLICATION_ID,
                            tempFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.resolveActivity(getPackageManager());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri);

                    cameraIntent.launch(intent);
                });
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void sendImageMessage(Uri uri) {
        new Thread(() -> {
            String[] imageUrl = {"https://rhezarijaya.000webhostapp.com/broken_image.png"};
            boolean[] isSuccess = {true};

            CountDownLatch countDownLatch = new CountDownLatch(1);

            FirebaseStorage.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_MESSAGE_PICTURES_STORAGE_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(uri.getLastPathSegment())
                    .putFile(uri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            task.getResult()
                                    .getMetadata()
                                    .getReference()
                                    .getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            imageUrl[0] = task1.getResult().toString();
                                        } else {
                                            isSuccess[0] = false;
                                        }
                                        countDownLatch.countDown();
                                    });
                        } else {
                            runOnUiThread(() -> Utilities.makeToast(ChatActivity.this, "Failed to upload image"));
                            countDownLatch.countDown();
                        }
                    });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                isSuccess[0] = false;
            }

            if (!isSuccess[0]) {
                return;
            }

            long timestamp = System.currentTimeMillis();

            Chat chat = new Chat(
                    senderUser.getUsername(),
                    null,
                    imageUrl[0],
                    timestamp
            );

            chatRef.push().setValue(chat, (error, ref) -> {
                if (error != null) {
                    runOnUiThread(() -> Utilities.makeToast(ChatActivity.this, "Failed to send message"));
                } else {
                    HashMap<String, Object> lastMessageMap = new HashMap<>();
                    lastMessageMap.put("lastMessage", "📷 Image");

                    HashMap<String, Object> lastTimestampMap = new HashMap<>();
                    lastTimestampMap.put("lastMessageTimestamp", timestamp);

                    senderRoomRef.updateChildren(lastMessageMap);
                    senderRoomRef.updateChildren(lastTimestampMap);
                    receiverRoomRef.updateChildren(lastMessageMap);
                    receiverRoomRef.updateChildren(lastTimestampMap);

                    sendNotification(receiverUser.getUsername(), "📷 Image");
                }
            });
        }).start();
    }

    private void sendNotification(String username, String message) {
        APIUtil.getAPIService()
                .sendFCM(username, "Chat from " + username, message)
                .enqueue(new Callback<APIResponse<FCMResponse>>() {
                    @Override
                    public void onResponse(Call<APIResponse<FCMResponse>> call, Response<APIResponse<FCMResponse>> response) {

                    }

                    @Override
                    public void onFailure(Call<APIResponse<FCMResponse>> call, Throwable t) {

                    }
                });
    }
}