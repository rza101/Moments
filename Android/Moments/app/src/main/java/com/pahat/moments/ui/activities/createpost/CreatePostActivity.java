package com.pahat.moments.ui.activities.createpost;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.pahat.moments.databinding.ActivityCreatePostBinding;
import com.pahat.moments.ui.activities.forgotpass.ForgotPassActivity;
import com.pahat.moments.util.Utilities;

import java.io.File;
import java.io.IOException;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utilities.initChildToolbar(this, binding.toolbar, "Create Post");

        binding.createPostBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = binding.createPostEtCaption.getText().toString();
                Drawable image = binding.createPostIvPreview.getDrawable();
                if (TextUtils.isEmpty(caption)) {
                    binding.createPostEtCaption.setError("Enter your email address!");
                    return;
                }
                if (image == null) {
                    SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_STANDARD)
                            .setText("Please enter an image")
                            .setDuration(Style.DURATION_LONG)
                            .setFrame(Style.FRAME_STANDARD)
                            .setColor(Color.parseColor("#3FBDF1"))
                            .setAnimations(Style.ANIMATIONS_FADE)
                            .show();
//                    Toast.makeText(CreatePostActivity.this, "Please enter an image", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private File createTempImageFile(Context context) {
        File file = null;

        try {
            file = File.createTempFile(
                    System.currentTimeMillis() + "_" + (Math.random() * 1000),
                    ".jpg",
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}