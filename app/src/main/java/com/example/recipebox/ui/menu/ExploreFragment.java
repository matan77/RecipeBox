package com.example.recipebox.ui.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.recipebox.R;
import com.example.recipebox.databinding.FragmentExploreBinding;

import com.example.recipebox.model.Recipe;
import com.example.recipebox.ui.RecipeAdapter;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private List<Recipe> recipeLst;
    private FirebaseFirestore db;
    private RecipeAdapter adapter;

    private DocumentSnapshot lastVisible;
    private String uid;
    FragmentExploreBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        recipeLst = new ArrayList<>();
        adapter = new RecipeAdapter(recipeLst, Navigation.findNavController(requireActivity(), R.id.fragment_container));
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();


        RecyclerView rvRecipes = binding.rvRecipes;

        rvRecipes.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        rvRecipes.setAdapter(adapter);

        loadRecipes();


        rvRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == recipeLst.size() - 1) {
                    loadMoreRecipes();
                }
            }
        });


        return binding.getRoot();
    }

    private void loadRecipes() {
        db.collection("recipes")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Recipe recipe = document.toObject(Recipe.class);
                            recipe.setId(document.getId());
                            recipeLst.add(recipe);
                        }
                        adapter.notifyDataSetChanged();

                        if (recipeLst.isEmpty()) {
                            binding.tvEmpty.setVisibility(View.GONE);
                        } else {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        }

                        if (!task.getResult().isEmpty()) {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }
                    }
                });
    }

    private void loadMoreRecipes() {
        if (lastVisible != null) {
            db.collection("recipes")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(10)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipe.setId(document.getId()); // Ensure ID is set
                                recipeLst.add(recipe);
                            }
                            adapter.notifyDataSetChanged();

                            if (!task.getResult().isEmpty()) {
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }
                        }
                    });
        }
    }


}