package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvUserNameHeader, tvTodayPickTitle, tvTodayPickTime;
    private RecyclerView rvCategories, rvPopularRecipes;
    private MaterialButton btnSpin;
    private MaterialCardView cardTodayPick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        // Ánh xạ View
        tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);
        tvTodayPickTitle = view.findViewById(R.id.tvTodayPickTitle);
        tvTodayPickTime = view.findViewById(R.id.tvTodayPickTime);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvPopularRecipes = view.findViewById(R.id.rvPopularRecipes);
        btnSpin = view.findViewById(R.id.btnSpin);
        cardTodayPick = view.findViewById(R.id.cardTodayPick);

        // Hiển thị tên người dùng
        displayUserName();

        // Hiển thị danh mục
        loadCategories();

        // Hiển thị món phổ biến
        loadPopularRecipes();

        // Today's Pick ngẫu nhiên
        loadTodayPick();

        btnSpin.setOnClickListener(v -> {
            loadTodayPick();
            Toast.makeText(getContext(), "Đang chọn món mới... ✨", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void displayUserName() {
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = pref.getInt("KEY_USER_ID", -1);
        if (userId != -1) {
            String fullName = dbHelper.getUserFullName(userId);
            tvUserNameHeader.setText(fullName);
        }
    }

    private void loadCategories() {
        List<Category> categoryList = dbHelper.getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);

        adapter.setOnItemClickListener(category -> {
            Toast.makeText(getContext(), "Chọn danh mục: " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPopularRecipes() {
        List<Recipe> popularList = dbHelper.getPopularRecipes(4);
        RecipeAdapter adapter = new RecipeAdapter(popularList);
        rvPopularRecipes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvPopularRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getRecipeId());
            intent.putExtra("RECIPE_NAME", recipe.getTitle());
            startActivity(intent);
        });
    }

    private void loadTodayPick() {
        Recipe randomRecipe = dbHelper.getRandomRecipe();
        if (randomRecipe != null) {
            tvTodayPickTitle.setText(randomRecipe.getTitle());
            tvTodayPickTime.setText("⏱ " + randomRecipe.getCookTime() + " min");

            cardTodayPick.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("RECIPE_ID", randomRecipe.getRecipeId());
                intent.putExtra("RECIPE_NAME", randomRecipe.getTitle());
                startActivity(intent);
            });
        }
    }
}
