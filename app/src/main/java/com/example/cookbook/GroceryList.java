package com.example.cookbook;

public class GroceryList {
    private int listId;
    private int userId;
    private String title;
    private String createdAt;

    public GroceryList() {}

    public GroceryList(int listId, int userId, String title, String createdAt) {
        this.listId = listId;
        this.userId = userId;
        this.title = title;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}