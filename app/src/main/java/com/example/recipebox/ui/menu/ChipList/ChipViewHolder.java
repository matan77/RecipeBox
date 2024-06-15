package com.example.recipebox.ui.menu.ChipList;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipebox.R;
import com.example.recipebox.databinding.ChipItemBinding;
import com.example.recipebox.model.Recipe;
import com.google.android.material.chip.Chip;

public class ChipViewHolder extends RecyclerView.ViewHolder {
    ChipItemBinding binding;

    public ChipViewHolder(ChipItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;


    }

    public void bind(String val) {
        binding.chip.setText(val);

    }
}