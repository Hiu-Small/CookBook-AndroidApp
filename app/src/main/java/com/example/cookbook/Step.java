package com.example.cookbook;

public class Step {
    private int stepId;
    private int recipeId;
    private int stepNumber;
    private String description;
    private String image;

    public Step() {}

    public Step(int stepId, int recipeId, int stepNumber, String description, String image) {
        this.stepId = stepId;
        this.recipeId = recipeId;
        this.stepNumber = stepNumber;
        this.description = description;
        this.image = image;
    }

    // Getters and Setters
    public int getStepId() { return stepId; }
    public void setStepId(int stepId) { this.stepId = stepId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}