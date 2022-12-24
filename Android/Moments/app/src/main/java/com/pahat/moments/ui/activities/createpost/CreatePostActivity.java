package com.pahat.moments.ui.activities.createpost;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pahat.moments.databinding.ActivityCreatePostBinding;
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