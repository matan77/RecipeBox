package com.example.recipebox.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.bumptech.glide.Glide;
import com.example.recipebox.R;
import com.example.recipebox.databinding.FragmentMenuBinding;
import com.example.recipebox.databinding.MenuHeaderBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuFragment extends Fragment {


    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMenuBinding binding = FragmentMenuBinding.inflate(inflater, container, false);


        Toolbar toolbar = binding.toolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // Set up the navigation drawer
        DrawerLayout drawer = binding.getRoot();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = binding.navigationView;

        View headerView = navigationView.getHeaderView(0);
        MenuHeaderBinding menuHeaderBinding = MenuHeaderBinding.bind(headerView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            menuHeaderBinding.tvDisplayName.setText(String.format(getString(R.string.hello_s),
                    user.getDisplayName()));
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

            if (photoUrl != null) {
                Glide.with(this).load(photoUrl)
                        .placeholder(R.drawable.proflie)
                        .fallback(R.drawable.proflie).circleCrop()
                        .into(menuHeaderBinding.imgProfile);
            }
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_my_recipes) {
                // Handle My Recipes
            } else if (itemId == R.id.nav_explore) {
                // Handle Explore
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_menuFragment_to_navigation_login);
            }
            drawer.closeDrawers();
            return true;
        });

        return binding.getRoot();
    }
}
