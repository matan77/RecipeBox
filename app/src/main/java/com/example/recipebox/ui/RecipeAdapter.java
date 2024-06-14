package com.example.recipebox.ui;

import static com.example.recipebox.ui.RecipeFragment.ARG_RECIPE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebox.R;
import com.example.recipebox.databinding.ItemRecipeBinding;
import com.example.recipebox.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private final List<Recipe> recipeLst;
    private final NavController navController;
    

    public RecipeAdapter(List<Recipe> recipeLst, NavController navController) {
        this.recipeLst = recipeLst;
        this.navController = navController;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeLst.get(position);
        holder.bind(recipe);

        holder.itemView.setOnClickListener(v -> {

            Bundle args = new Bundle();
            args.putSerializable(ARG_RECIPE, recipe);
            navController.navigate(R.id.recpieFragment, args);

        });

    }

    @Override
    public int getItemCount() {
        return recipeLst.size();
    }


}
