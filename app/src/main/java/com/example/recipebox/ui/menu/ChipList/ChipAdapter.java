package com.example.recipebox.ui.menu.ChipList;

import static com.example.recipebox.ui.RecipeFragment.ARG_RECIPE;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebox.R;
import com.example.recipebox.databinding.ChipItemBinding;
import com.example.recipebox.databinding.ItemRecipeBinding;
import com.example.recipebox.model.Recipe;
import com.example.recipebox.ui.RecipeViewHolder;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipViewHolder> {
    private final List<String> valuesLst;
    private final boolean isNumbered;


    public ChipAdapter(List<String> valuesLst) {
        this.valuesLst = valuesLst;
        isNumbered = false;

    }

    public ChipAdapter(List<String> valuesLst, boolean isNumbered) {
        this.valuesLst = valuesLst;
        this.isNumbered = isNumbered;

    }


    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChipItemBinding binding = ChipItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ChipViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        String val = valuesLst.get(position);
        if (isNumbered) {
            holder.bind((position + 1) + ". " + val);
        } else {
            holder.bind(val);
        }
    }

    @Override
    public int getItemCount() {
        return valuesLst.size();
    }


}
