package com.example.cookbook;

public class CookHistory {
    private int historyId;
    private int userId;
    private int recipeId;
    private String cookedDate;

    public CookHistory() {}

    public CookHistory(int historyId, int userId, int recipeId, String cookedDate) {
        this.historyId = historyId;
        this.userId = userId;
        this.recipeId = recipeId;
        this.cookedDate = cookedDate;
    }

    // Getters and Setters
    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getCookedDate() { return cookedDate; }
    public void setCookedDate(String cookedDate) { this.cookedDate = cookedDate; }
}