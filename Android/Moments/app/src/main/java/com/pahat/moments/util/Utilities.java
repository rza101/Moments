package com.pahat.moments.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.pahat.moments.databinding.ToolbarChildrenBinding;

import java.io.File;
import java.io.IOException;

public class Utilities {
    public static File createTempImageFile(Context context) {
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

    public static void initChildToolbar(AppCompatActivity activity, ToolbarChildrenBinding binding, String title) {
        activity.setSupportActionBar(binding.getRoot());
        binding.toolbarTitle.setText(title);
        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public static void makeToast(Context context, String text) {
        SuperActivityToast.create(context, new Style(), Style.TYPE_STANDARD)
                .setText(text)
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_STANDARD)
                .setTextColor(Color.parseColor("#9F9F9F"))
                .setColor(Color.parseColor("#3FBDF1"))
                .setAnimations(Style.ANIMATIONS_FADE)
                .show();
    }
}
