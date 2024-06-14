package com.example.recipebox.model;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Recipe implements Serializable {
    @Exclude
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private List<String> ingredients;
    private List<String> instructions;
    private String creator;
    private Timestamp createdAt;

    public Recipe() {
    }

    public Recipe(String title, String description, List<String> ingredients, List<String> instructions, String creator) {
        this.title = title;
        this.imageUrl = "";
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.creator = creator;
        createdAt = new Timestamp(new Date());
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
