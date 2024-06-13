package com.example.recipebox.ui;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.recipebox.R;
import com.example.recipebox.databinding.ItemRecipeBinding;
import com.example.recipebox.model.Recipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder {
    ItemRecipeBinding binding;

    public RecipeViewHolder(ItemRecipeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Recipe recipe) {
        binding.tvTitle.setText(recipe.getTitle());
        binding.tvDescription.setText(recipe.getDescription());
        Glide.with(this.itemView.getContext())
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.recipe)
                .fallback(R.drawable.recipe)
                .into(binding.imgRecipe);
    }
}