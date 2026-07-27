package com.example.cookbook;

public class Recipe {
    private int recipeId;
    private int categoryId;
    private String title;
    private String description;
    private String image;
    private int cookTime;
    private String difficulty;
    private int servings;
    private int calories;
    private double rating;
    private String createdAt;

    public Recipe() {}

    public Recipe(int recipeId, int categoryId, String title, String description, String image,
                  int cookTime, String difficulty, int servings, int calories, double rating, String createdAt) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.image = image;
        this.cookTime = cookTime;
        this.difficulty = difficulty;
        this.servings = servings;
        this.calories = calories;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getCookTime() { return cookTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}