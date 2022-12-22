package com.pahat.moments.ui.activities.main;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.pahat.moments.R;
import com.pahat.moments.databinding.ActivityMainBinding;

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

                if(navDestination.getId() == R.id.menu_main_nav_home){
                    title = "Home";
                }else if(navDestination.getId() == R.id.menu_main_nav_chat){
                    title = "Chat";
                }else if(navDestination.getId() == R.id.menu_main_nav_profile){
                    title = "Profile";
                }

                binding.toolbar.toolbarTitle.setText(title);
            }
        });

        binding.mainIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.menu_main_popup, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }
}