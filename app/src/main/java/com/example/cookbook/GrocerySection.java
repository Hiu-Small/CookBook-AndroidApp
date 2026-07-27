package com.example.cookbook;

import java.util.List;

public class GrocerySection {
    // 1. Các thuộc tính (Fields)
    private String sectionTitle;       // Tên nhóm / Tên món ăn
    private Integer recipeId; // Thêm biến này (null đối với món tự thêm)
    private List<GroceryItem> itemList; // Danh sách nguyên liệu của món đó

    // 2. Hàm khởi tạo (Constructor)
    public GrocerySection(String sectionTitle, Integer recipeId,List<GroceryItem> itemList) {
        this.sectionTitle = sectionTitle;
        this.itemList = itemList;
        this.recipeId = recipeId;
    }

    public Integer getRecipeId() { return recipeId; }

    // 3. Getter và Setter
    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public List<GroceryItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<GroceryItem> itemList) {
        this.itemList = itemList;
    }
}