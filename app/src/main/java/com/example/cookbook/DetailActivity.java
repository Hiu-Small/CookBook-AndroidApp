package com.example.cookbook;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DetailActivity extends AppCompatActivity {
    private int servingCount = 4;
    private TextView tvServingCount, tvServingPeople;

    // Tabs & Containers
    private MaterialCardView cardTabIngredients, cardTabSteps;
    private TextView tvTabIngredients, tvTabSteps;
    private LinearLayout layoutIngredientsContainer, layoutStepsContainer;
    private View bottomButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tab views
        cardTabIngredients = findViewById(R.id.cardTabIngredients);
        cardTabSteps = findViewById(R.id.cardTabSteps);
        tvTabIngredients = findViewById(R.id.tvTabIngredients);
        tvTabSteps = findViewById(R.id.tvTabSteps);

        // Containers
        layoutIngredientsContainer = findViewById(R.id.layoutIngredientsContainer);
        layoutStepsContainer = findViewById(R.id.layoutStepsContainer);
        bottomButtonContainer = findViewById(R.id.bottomButtonContainer);

        // Serving Counter
        tvServingCount = findViewById(R.id.tvServingCount);
        tvServingPeople = findViewById(R.id.tvServingPeople);
    }

    private void setupListeners() {
        // Sự kiện chuyển Tab Ingredients
        cardTabIngredients.setOnClickListener(v -> switchTab(true));

        // Sự kiện chuyển Tab Steps
        cardTabSteps.setOnClickListener(v -> switchTab(false));

        // Tăng giảm khẩu phần ăn
        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            if (servingCount > 1) {
                servingCount--;
                updateServingText();
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            servingCount++;
            updateServingText();
        });

        // Nút thêm vào Shopping List
        findViewById(R.id.btnAddToShoppingList).setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm nguyên liệu vào Shopping List!", Toast.LENGTH_SHORT).show();
        });
    }

    // Logic chuyển đổi qua lại giữa 2 Tab
    private void switchTab(boolean isIngredientsSelected) {
        if (isIngredientsSelected) {
            // Highlight Tab Ingredients
            cardTabIngredients.setCardBackgroundColor(Color.WHITE);
            cardTabIngredients.setCardElevation(2f);
            tvTabIngredients.setTextColor(Color.parseColor("#202433"));

            // Un-highlight Tab Steps
            cardTabSteps.setCardBackgroundColor(Color.TRANSPARENT);
            cardTabSteps.setCardElevation(0f);
            tvTabSteps.setTextColor(Color.parseColor("#8C919E"));

            // Show/Hide Containers
            layoutIngredientsContainer.setVisibility(View.VISIBLE);
            layoutStepsContainer.setVisibility(View.GONE);
            bottomButtonContainer.setVisibility(View.VISIBLE);
        } else {
            // Highlight Tab Steps
            cardTabSteps.setCardBackgroundColor(Color.WHITE);
            cardTabSteps.setCardElevation(2f);
            tvTabSteps.setTextColor(Color.parseColor("#202433"));

            // Un-highlight Tab Ingredients
            cardTabIngredients.setCardBackgroundColor(Color.TRANSPARENT);
            cardTabIngredients.setCardElevation(0f);
            tvTabIngredients.setTextColor(Color.parseColor("#8C919E"));

            // Show/Hide Containers
            layoutIngredientsContainer.setVisibility(View.GONE);
            layoutStepsContainer.setVisibility(View.VISIBLE);
            bottomButtonContainer.setVisibility(View.GONE);
        }
    }

    private void updateServingText() {
        tvServingCount.setText(String.valueOf(servingCount));
        tvServingPeople.setText(servingCount + " people");
    }
}
