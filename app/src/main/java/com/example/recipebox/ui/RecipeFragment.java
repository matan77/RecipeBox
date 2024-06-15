package com.example.recipebox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipebox.R;
import com.example.recipebox.databinding.FragmentRecipeBinding;
import com.example.recipebox.model.Recipe;
import com.example.recipebox.ui.menu.ChipList.ChipAdapter;
import com.google.android.material.transition.MaterialFadeThrough;


import java.util.List;

public class RecipeFragment extends Fragment {

    public static final String ARG_RECIPE = "recipe";
    private Recipe recipe;
    private FragmentRecipeBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.rvInstructions.setNestedScrollingEnabled(false);
        binding.rvIngredients.setNestedScrollingEnabled(false);

        binding.topAppBar.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigateUp();
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });


        if (recipe != null) {

            Glide.with(requireContext())
                    .load(recipe.getImageUrl()).placeholder(R.drawable.recipe).fallback(R.drawable.recipe)
                    .into(binding.imgRecipe);

            binding.topAppBar.setTitle(recipe.getTitle());
            binding.tvDescription.setText(recipe.getDescription());


            binding.rvIngredients.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            //
            binding.rvIngredients.setLayoutManager(new GridLayoutManager(binding.getRoot().getContext(), 2,
                    GridLayoutManager.HORIZONTAL, false));
            //
            binding.rvIngredients.setAdapter(new ChipAdapter(recipe.getIngredients()));


            binding.rvInstructions.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.rvInstructions.setAdapter(new ChipAdapter(recipe.getInstructions(), true));


        }

        return view;
    }


}
