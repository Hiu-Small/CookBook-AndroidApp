package com.example.cookbook;

public class Favorite {
    private int userId;
    private int recipeId;
    private String favoriteAt;

    public Favorite() {}

    public Favorite(int userId, int recipeId, String favoriteAt) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.favoriteAt = favoriteAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getFavoriteAt() { return favoriteAt; }
    public void setFavoriteAt(String favoriteAt) { this.favoriteAt = favoriteAt; }
}