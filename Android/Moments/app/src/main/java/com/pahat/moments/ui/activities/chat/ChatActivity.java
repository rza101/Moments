package com.pahat.moments.ui.activities.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.pahat.moments.data.firebase.model.Chat;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ActivityChatBinding;
import com.pahat.moments.ui.adapters.FirebaseChatAdapter;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChatActivity extends AppCompatActivity {

    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String USER_INTENT_KEY = "USER_INTENT_KEY";

    private ActivityChatBinding binding;
    private FirebaseChatAdapter firebaseChatAdapter;

    private User senderUser;
    private User receiverUser;

    private List<Chat> chatList;

    private Uri tempCameraUri;

    private boolean loadSuccess = true;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    final Uri uri = result.getData().getData();
                    Log.d(TAG, "Uri : " + uri.toString());

//                    ChatMessage tempMessage = new ChatMessage(null, userId, LOADING_IMAGE_URL);
//                    mRoot.child("messages").push()
//                            .setValue(tempMessage, (databaseError, databaseReference) -> {
//                                if (databaseError == null) {
//                                    String key = databaseReference.getKey();
//                                    StorageReference storageReference = FirebaseStorage.getInstance()
//                                            .getReference(mAuth.getCurrentUser().getUid())
//                                            .child(key)
//                                            .child(uri.getLastPathSegment());
//
//                                    putImageInStorage(storageReference, uri, key);
//                                } else {
//                                    Log.w(TAG, "Unable to write database", databaseError.toException());
//                                }
//                            });
                }
            }
        }
    });

    private ActivityResultLauncher<Intent> cameraIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                final Uri imageUri = tempCameraUri;
            }
        }
    });

    private ActivityResultLauncher<Intent> galleryIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    final Uri imageUri = result.getData().getData();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!getIntent().hasExtra(USER_INTENT_KEY)) {
            Utilities.makeToast(this, "Invalid access");
            finish();
            return;
        }

        new Thread(() -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(1);

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
                receiverUser = getIntent().getParcelableExtra(USER_INTENT_KEY);

                Utilities.initChildToolbar(this, binding.toolbar, receiverUser.getFullName());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                linearLayoutManager.setStackFromEnd(true);

                firebaseChatAdapter = new FirebaseChatAdapter(senderUser, receiverUser);
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

                FirebaseDatabase.getInstance()
                        .getReference(Constants.FIREBASE_CHATS_DB_REF)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                chatList = new ArrayList<>();

                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Chat chat = child.getValue(Chat.class);
                                    chat.setChatId(child.getKey());

                                    if((chat.getSender().equals(senderUser.getUsername()) && chat.getReceiver().equals(receiverUser.getUsername())) ||
                                            (chat.getReceiver().equals(senderUser.getUsername()) && chat.getSender().equals(receiverUser.getUsername()))){
                                        chatList.add(chat);
                                    }
                                }

                                Collections.sort(chatList, (o1, o2) ->
                                        (int) (o2.getTimestamp() - o1.getTimestamp()));

                                firebaseChatAdapter.submitList(chatList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                runOnUiThread(() -> Utilities.makeToast(ChatActivity.this, "Failed to get chat data"));
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
                    Chat chat = new Chat(
                            senderUser.getUsername(),
                            senderUser.getFullName(),
                            senderUser.getProfilePicture(),
                            receiverUser.getUsername(),
                            receiverUser.getFullName(),
                            receiverUser.getProfilePicture(),
                            binding.chatEtMessage.getText().toString(),
                            null,
                            System.currentTimeMillis()
                    );

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.FIREBASE_CHATS_DB_REF)
                            .push()
                            .setValue(chat);

                    binding.chatEtMessage.setText("");

                    // SEND FCM API

                });

                binding.chatIbImage.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
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

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this, task -> {
            if (task.isSuccessful()) {
                task.getResult()
                        .getMetadata()
                        .getReference()
                        .getDownloadUrl()
                        .addOnCompleteListener(ChatActivity.this, task1 -> {
                            if (task1.isSuccessful()) {
//                                ChatMessage chatMessage = new ChatMessage(null, senderUser.getUsername(), task1.getResult().toString());
//                                FirebaseDatabase.getInstance()
//                                        .getReference()
//                                        .child(Constants.FIREBASE_CHATS_DB_REF)
//                                        .child(key)
//                                        .setValue(chatMessage);

                                // SEND FCM
//                                Data data = new Data(mUsername, "Image Message", userId, task1.getResult().toString());
//                                Sender sender = new Sender(data, "/topics/messages");
//                                sendNotification(sender);
                            }
                        });
            } else {
                Log.w(TAG, "Image upload task failed!", task.getException());
            }
        });
    }

    private void sendNotification() {
//        APIService api = APIUtil.getRetrofit().create(APIService.class);
//        Call<ViewData> call = api.sendNotification(sender);
//        call.enqueue(new Callback<ViewData>() {
//            @Override
//            public void onResponse(Call<ViewData> call, Response<ViewData> response) {
//                if (response.code() == 200) {
//                    System.out.println("Response : " + response.body().getMessage_id());
//                    if (response.body().getMessage_id() != null) {
////                        Toast.makeText(ChatActivity.this, "Pesan berhasil dikirim!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ChatActivity.this, "Pesan gagal dikirim!", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ChatActivity.this, "Response " + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ViewData> call, Throwable t) {
//                System.out.println("Retrofit Error : " + t.getMessage());
//                Toast.makeText(ChatActivity.this, "Retrofit Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}