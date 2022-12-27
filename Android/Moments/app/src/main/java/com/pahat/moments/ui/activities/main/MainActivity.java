package com.pahat.moments.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ActivityMainBinding;
import com.pahat.moments.ui.activities.about.AboutActivity;
import com.pahat.moments.ui.activities.changepass.ChangePassActivity;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;
import com.pahat.moments.util.Constants;

public class MainActivity extends AppCompatActivity {

    public static String USER_INTENT_KEY = "USER_INTENT_KEY";

    private ActivityMainBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getParcelableExtra(USER_INTENT_KEY) != null) {
            user = getIntent().getParcelableExtra(USER_INTENT_KEY);
        } else {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_USERS_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);
                            binding.toolbar.toolbarFullname.setText(user.getFullName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        setSupportActionBar(binding.toolbar.getRoot());
        binding.toolbar.toolbarFullname.setText(user == null ? "" : user.getFullName());

        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        NavigationUI.setupWithNavController(binding.mainBnv, navController);

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            String title = "";

            if (navDestination.getId() == R.id.main_nav_home) {
                title = "Home";
            } else if (navDestination.getId() == R.id.main_nav_search) {
                title = "Search";
            } else if (navDestination.getId() == R.id.main_nav_chat) {
                title = "Chat";
            } else if (navDestination.getId() == R.id.main_nav_profile) {
                title = "Profile";
            }

            binding.toolbar.toolbarTitle.setText(title);
        });

        binding.toolbar.toolbarMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_popup_main_about) {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_saved_posts) {
                    startActivity(new Intent(MainActivity.this, SavedPostActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_change_pass) {
                    startActivity(new Intent(MainActivity.this, ChangePassActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_logout) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    return true;
                }

                return false;
            });

            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_popup_main, popupMenu.getMenu());
            popupMenu.show();
        });

        binding.toolbar.toolbarAddPost.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreatePostActivity.class))
        );
    }
}