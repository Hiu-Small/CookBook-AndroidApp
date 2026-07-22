package com.example.cookbook;

public class Ingredient {
    private int ingredientId;
    private int recipeId;
    private String ingredientName;
    private String quantity;

    public Ingredient() {}

    public Ingredient(int ingredientId, int recipeId, String ingredientName, String quantity) {
        this.ingredientId = ingredientId;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}