package com.pahat.moments.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.firebase.model.UserList;
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.UserFollow;
import com.pahat.moments.databinding.ToolbarChildrenBinding;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Utilities {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

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

    public static String getInitialName(String fullName) {
        String[] splitName = fullName.split("\\s+");
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

    public static String isoDateToPrettyDate(String isoDate) {
        String result = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ROOT);
        try {
            Date date = simpleDateFormat.parse(isoDate);
            result = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ROOT).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void initChildToolbar(AppCompatActivity activity, ToolbarChildrenBinding binding, String title) {
        activity.setSupportActionBar(binding.getRoot());
        binding.toolbarTitle.setText(title);
        binding.toolbarBack.setOnClickListener(v -> activity.finish());
    }

    public static void makeToast(Context context, String text) {
        SuperActivityToast.create(context, new Style(), Style.TYPE_STANDARD)
                .setText(text)
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_STANDARD)
                .setTextColor(Color.BLACK)
                .setColor(Color.parseColor("#3FBDF1"))
                .setAnimations(Style.ANIMATIONS_FADE)
                .show();
    }

    public static String numberToText(long value) {
        if (value == Long.MIN_VALUE) return numberToText(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + numberToText(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static UserList likeListToUserList(List<PostLike> postLikeList) {
        List<User> userList = new ArrayList<>();

        for (PostLike postLike : postLikeList) {
            userList.add(new User(null, postLike.getUsername(), postLike.getFullName(), postLike.getImageUrl()));
        }

        return new UserList(userList);
    }

    public static UserList followerListToUserList(List<UserFollow> userFollowList) {
        List<User> userList = new ArrayList<>();

        for (UserFollow userFollow : userFollowList) {
            userList.add(new User(null, userFollow.getUsername(), userFollow.getFullName(), userFollow.getImageUrl()));
        }

        return new UserList(userList);
    }

    public static UserList followingListToUserList(List<UserFollow> userFollowList) {
        List<User> userList = new ArrayList<>();

        for (UserFollow userFollow : userFollowList) {
            userList.add(new User(null, userFollow.getUsernameFollowing(), userFollow.getFullNameFollowing(), userFollow.getImageUrlFollowing()));
        }

        return new UserList(userList);
    }
}
