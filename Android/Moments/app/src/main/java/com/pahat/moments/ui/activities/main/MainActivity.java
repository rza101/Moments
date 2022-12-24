package com.pahat.moments.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.pahat.moments.R;
import com.pahat.moments.databinding.ActivityMainBinding;
import com.pahat.moments.ui.activities.about.AboutActivity;
import com.pahat.moments.ui.activities.changepass.ChangePassActivity;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.getRoot());
        binding.toolbar.toolbarFullname.setText("Fullname");

        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        NavigationUI.setupWithNavController(binding.mainBnv, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController,
                                             @NonNull NavDestination navDestination,
                                             @Nullable Bundle bundle) {
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
            }
        });

        binding.toolbar.toolbarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.menu_main_popup_about) {
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            return true;
                        } else if (id == R.id.menu_main_popup_saved_posts) {
                            startActivity(new Intent(MainActivity.this, SavedPostActivity.class));
                            return true;
                        } else if (id == R.id.menu_main_popup_change_pass) {
                            startActivity(new Intent(MainActivity.this, ChangePassActivity.class));
                            return true;
                        } else if (id == R.id.menu_main_popup_logout) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            return true;
                        }

                        return false;
                    }
                });

                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.menu_main_popup, popupMenu.getMenu());
                popupMenu.show();
            }
        });

        binding.toolbar.toolbarAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });
    }
}