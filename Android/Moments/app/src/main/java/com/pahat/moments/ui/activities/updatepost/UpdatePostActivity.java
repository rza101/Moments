package com.pahat.moments.ui.activities.updatepost;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityUpdatePostBinding;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.util.Utilities;

public class UpdatePostActivity extends AppCompatActivity {

    private ActivityUpdatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Update Post");

        binding.updatePostBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = binding.updatePostEtCaption.getText().toString();
                if (TextUtils.isEmpty(caption)) {
                    binding.updatePostEtCaption.setError("Enter a caption");
                    return;
                }
//                if (caption ==) {
//                    binding.updatePostEtCaption.setError("Caption cannot be same as before");
//                    return;
//                }
            }
        });
    }
}