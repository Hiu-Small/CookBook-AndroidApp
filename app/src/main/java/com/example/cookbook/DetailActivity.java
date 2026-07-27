package com.example.cookbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private int recipeId;
    private int userId;
    private DatabaseHelper dbHelper;

    private TextView tvRecipeTitle, tvRecipeRating, tvRecipeDescription, tvCookTime, tvCalories, tvServes;
    private TextView tvRecipeDifficulty, tvRecipeCategory;
    private TextView tvMarkAsCooked;
    private ImageView imgRecipeHero, imgFavorite, imgMarkAsCooked;
    private MaterialCardView btnFavorite, btnMarkAsCooked;

    // Tabs & Containers
    private MaterialCardView cardTabIngredients, cardTabSteps;
    private TextView tvTabIngredients, tvTabSteps;
    private LinearLayout layoutIngredientsContainer, layoutStepsContainer;
    private View bottomButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = DatabaseHelper.getInstance(this);
        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        SharedPreferences pref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = pref.getInt("KEY_USER_ID", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadRecipeData();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        imgRecipeHero = findViewById(R.id.imgRecipeHero);
        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvRecipeRating = findViewById(R.id.tvRecipeRating);
        tvRecipeDescription = findViewById(R.id.tvRecipeDescription);
        tvCookTime = findViewById(R.id.tvCookTime);
        tvServes = findViewById(R.id.tvServes);
        tvCalories = findViewById(R.id.tvCalories);
        tvRecipeDifficulty = findViewById(R.id.tvRecipeDifficulty);
        tvRecipeCategory = findViewById(R.id.tvRecipeCategory);
        imgFavorite = findViewById(R.id.imgFavorite);
        btnFavorite = findViewById(R.id.btnFavorite);

        btnMarkAsCooked = findViewById(R.id.btnMarkAsCooked);
        tvMarkAsCooked = findViewById(R.id.tvMarkAsCooked);
        imgMarkAsCooked = findViewById(R.id.imgMarkAsCooked);

        // Tab views
        cardTabIngredients = findViewById(R.id.cardTabIngredients);
        cardTabSteps = findViewById(R.id.cardTabSteps);
        tvTabIngredients = findViewById(R.id.tvTabIngredients);
        tvTabSteps = findViewById(R.id.tvTabSteps);

        // Containers
        layoutIngredientsContainer = findViewById(R.id.layoutIngredientsContainer);
        layoutStepsContainer = findViewById(R.id.layoutStepsContainer);
        bottomButtonContainer = findViewById(R.id.bottomButtonContainer);
    }

    private void loadRecipeData() {
        Recipe currentRecipe = dbHelper.getRecipeById(recipeId);
        if (currentRecipe == null) return;

        tvRecipeTitle.setText(currentRecipe.getTitle());
        tvRecipeRating.setText("⭐ " + currentRecipe.getRating());
        tvRecipeDescription.setText(currentRecipe.getDescription());
        tvCookTime.setText("⏱ " + currentRecipe.getCookTime() + " min");
        tvServes.setText("👥 " + currentRecipe.getServings() + " Servings");
        tvCalories.setText("🔥 " + currentRecipe.getCalories() + " kcal");
        tvRecipeDifficulty.setText(currentRecipe.getDifficulty());
        
        String categoryName = dbHelper.getCategoryNameById(currentRecipe.getCategoryId());
        tvRecipeCategory.setText(categoryName);

        // Load Image
        int imageResId = getResources().getIdentifier(currentRecipe.getImage(), "drawable", getPackageName());
        if (imageResId != 0) {
            imgRecipeHero.setImageResource(imageResId);
        }

        // Check States
        updateFavoriteUI();
        updateCookedUI();

        // Load Ingredients & Steps
        loadIngredients();
        loadSteps();
    }

    private void updateFavoriteUI() {
        if (dbHelper.isFavorite(userId, recipeId)) {
            imgFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            imgFavorite.setColorFilter(Color.parseColor("#FFC107")); // Gold/Yellow
        } else {
            imgFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            imgFavorite.setColorFilter(Color.WHITE);
        }
    }

    private void updateCookedUI() {
        if (dbHelper.isCooked(userId, recipeId)) {
            btnMarkAsCooked.setCardBackgroundColor(Color.parseColor("#D1D5DB")); // Gray
            tvMarkAsCooked.setText("Cooked ✅");
            imgMarkAsCooked.setAlpha(0.5f);
        } else {
            btnMarkAsCooked.setCardBackgroundColor(Color.WHITE);
            tvMarkAsCooked.setText("Mark as Cooked");
            imgMarkAsCooked.setAlpha(1.0f);
        }
    }

    private void setupListeners() {
        cardTabIngredients.setOnClickListener(v -> switchTab(true));
        cardTabSteps.setOnClickListener(v -> switchTab(false));

        findViewById(R.id.btnAddToShoppingList).setOnClickListener(v -> {
            dbHelper.addRecipeIngredientsToGroceryList(userId, recipeId);
            Toast.makeText(this, "All ingredients added to Shopping List! 🛒", Toast.LENGTH_SHORT).show();
        });

        btnMarkAsCooked.setOnClickListener(v -> {
            dbHelper.toggleCookHistory(userId, recipeId);
            updateCookedUI();
            String msg = dbHelper.isCooked(userId, recipeId) ? "Marked as Cooked! 🍳" : "Removed from History";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        btnFavorite.setOnClickListener(v -> {
            dbHelper.toggleFavorite(userId, recipeId);
            updateFavoriteUI();
            String msg = dbHelper.isFavorite(userId, recipeId) ? "Added to Favorites!" : "Removed from Favorites!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadIngredients() {
        layoutIngredientsContainer.removeAllViews();
        List<Ingredient> ingredients = dbHelper.getIngredientsByRecipeId(recipeId);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Ingredient ing : ingredients) {
            View itemView = inflater.inflate(R.layout.item_ingredient, layoutIngredientsContainer, false);
            TextView tvName = itemView.findViewById(R.id.tvIngredientName);
            TextView tvQty = itemView.findViewById(R.id.tvIngredientQuantity);
            
            tvName.setText(ing.getIngredientName());
            tvQty.setText(ing.getQuantity());
            
            layoutIngredientsContainer.addView(itemView);
        }
    }

    private void loadSteps() {
        layoutStepsContainer.removeAllViews();
        List<Step> steps = dbHelper.getStepsByRecipeId(recipeId);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Step step : steps) {
            View itemView = inflater.inflate(R.layout.item_step, layoutStepsContainer, false);
            TextView tvNumber = itemView.findViewById(R.id.tvStepNumber);
            TextView tvTitle = itemView.findViewById(R.id.tvStepTitle);
            TextView tvDesc = itemView.findViewById(R.id.tvStepDescription);
            
            tvNumber.setText(String.valueOf(step.getStepNumber()));
            tvTitle.setText("Step " + step.getStepNumber());
            tvDesc.setText(step.getDescription());
            
            itemView.findViewById(R.id.tvStepTime).setVisibility(View.GONE);

            layoutStepsContainer.addView(itemView);
        }
    }

    private void switchTab(boolean isIngredientsSelected) {
        if (isIngredientsSelected) {
            cardTabIngredients.setCardBackgroundColor(Color.WHITE);
            cardTabIngredients.setCardElevation(2f);
            tvTabIngredients.setTextColor(Color.parseColor("#202433"));

            cardTabSteps.setCardBackgroundColor(Color.TRANSPARENT);
            cardTabSteps.setCardElevation(0f);
            tvTabSteps.setTextColor(Color.parseColor("#8C919E"));

            layoutIngredientsContainer.setVisibility(View.VISIBLE);
            layoutStepsContainer.setVisibility(View.GONE);
            bottomButtonContainer.setVisibility(View.VISIBLE);
        } else {
            cardTabSteps.setCardBackgroundColor(Color.WHITE);
            cardTabSteps.setCardElevation(2f);
            tvTabSteps.setTextColor(Color.parseColor("#202433"));

            cardTabIngredients.setCardBackgroundColor(Color.TRANSPARENT);
            cardTabIngredients.setCardElevation(0f);
            tvTabIngredients.setTextColor(Color.parseColor("#8C919E"));

            layoutIngredientsContainer.setVisibility(View.GONE);
            layoutStepsContainer.setVisibility(View.VISIBLE);
            bottomButtonContainer.setVisibility(View.GONE);
        }
    }
}
