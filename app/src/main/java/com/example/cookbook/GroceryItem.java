package com.example.cookbook;

public class GroceryItem {
    private int itemId;
    private int listId;
    private String ingredientName;
    private String quantity;
    private boolean isChecked;
    private Integer recipeId; // Dùng Integer để chấp nhận giá trị null nếu món này không thuộc recipe nào

    public GroceryItem() {}

    public GroceryItem(int itemId, int listId, String ingredientName, String quantity, boolean isChecked, Integer recipeId) {
        this.itemId = itemId;
        this.listId = listId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.isChecked = isChecked;
        this.recipeId = recipeId;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }

    public Integer getRecipeId() { return recipeId; }
    public void setRecipeId(Integer recipeId) { this.recipeId = recipeId; }
}