package com.pahat.moments.ui.activities.detailpost;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.ActivityDetailPostBinding;
import com.pahat.moments.util.Utilities;

public class DetailPostActivity extends AppCompatActivity {

    public static String POST_INTENT_KEY = "POST_INTENT_KEY";

    private ActivityDetailPostBinding binding;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getParcelableExtra(POST_INTENT_KEY) == null){
            Utilities.makeToast(this, "Invalid access");
            finish();
        }

        post = getIntent().getParcelableExtra(POST_INTENT_KEY);

        binding.detailPostIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}