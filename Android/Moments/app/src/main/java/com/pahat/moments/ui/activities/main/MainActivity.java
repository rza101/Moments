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
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.R;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.databinding.ActivityMainBinding;
import com.pahat.moments.ui.activities.about.AboutActivity;
import com.pahat.moments.ui.activities.changepass.ChangePassActivity;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.ui.activities.editprofile.EditProfileActivity;
import com.pahat.moments.ui.activities.login.LoginActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;
import com.pahat.moments.util.Constants;
import com.pahat.moments.util.Utilities;

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
                    .child(Constants.FIREBASE_USERS_DB_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = task.getResult().getValue(User.class);
                            binding.toolbar.toolbarFullname.setText(user.getFullName());
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
                } else if (id == R.id.menu_popup_main_edit_profile) {
                    startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_saved_posts) {
                    startActivity(new Intent(MainActivity.this, SavedPostActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_change_pass) {
                    startActivity(new Intent(MainActivity.this, ChangePassActivity.class));
                    return true;
                } else if (id == R.id.menu_popup_main_logout) {
                    FirebaseAuth.getInstance().signOut();
                    FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if (firebaseAuth.getCurrentUser() == null) {
                                FirebaseAuth.getInstance().removeAuthStateListener(this);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Utilities.makeToast(MainActivity.this, "Failed to sign out");
                            }
                        }
                    });
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