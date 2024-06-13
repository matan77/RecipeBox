package com.example.recipebox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebox.R;
import com.example.recipebox.databinding.ItemRecipeBinding;
import com.example.recipebox.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private final List<Recipe> recipeLst;

    public RecipeAdapter(List<Recipe> recipeLst) {
        this.recipeLst = recipeLst;
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
            Navigation.findNavController(v).navigate(R.id.action_myRecpiesFragment_to_addRecipeFragment);
//            Bundle bundle = new Bundle();
//            bundle.putString("recipeId", recipeLst.get(position).getId());
//            Navigation.findNavController(v).navigate(R.id.action_listFragment_to_detailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return recipeLst.size();
    }


}
