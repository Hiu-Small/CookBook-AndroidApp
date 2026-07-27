package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvUserNameHeader, tvTodayPickTitle, tvTodayPickRating, tvSeeAll;
    private RecyclerView rvCategories, rvPopularRecipes;
    private MaterialButton btnSpin;
    private MaterialCardView cardTodayPick, btnSearch;
    private EditText edtSearch;
    private ImageView imgTodayPick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        // Ánh xạ View
        tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);
        tvTodayPickTitle = view.findViewById(R.id.tvTodayPickTitle);
        tvTodayPickRating = view.findViewById(R.id.tvTodayPickRating);
        imgTodayPick = view.findViewById(R.id.imgTodayPick);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvPopularRecipes = view.findViewById(R.id.rvPopularRecipes);
        btnSpin = view.findViewById(R.id.btnSpin);
        cardTodayPick = view.findViewById(R.id.cardTodayPick);
        btnSearch = view.findViewById(R.id.btnSearch);
        edtSearch = view.findViewById(R.id.edtSearch);
        tvSeeAll = view.findViewById(R.id.tvSeeAll);

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

        btnSearch.setOnClickListener(v -> {
            String query = edtSearch.getText().toString().trim();
            navigateToSearch(query);
        });

        tvSeeAll.setOnClickListener(v -> {
            navigateToSearch("");
        });

        return view;
    }

    private void navigateToSearch(String query) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_QUERY", query);
        searchFragment.setArguments(bundle);

        // Đồng bộ Thanh điều hướng (Bottom Navigation)
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_search);
        }

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();
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
        // Thêm mục "Tất cả" vào đầu danh sách
        categoryList.add(0, new Category(-1, "Tất cả"));
        
        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);

        adapter.setOnItemClickListener(category -> {
            if (category.getCategoryId() == -1) {
                loadPopularRecipes();
                Toast.makeText(getContext(), "Hiển thị tất cả món phổ biến", Toast.LENGTH_SHORT).show();
            } else {
                loadPopularRecipesByCategory(category.getCategoryId());
                Toast.makeText(getContext(), "Lọc theo: " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularRecipes() {
        List<Recipe> popularList = dbHelper.getPopularRecipes(4);
        // ⚡ Lấy currentUserId từ SharedPreferences
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int currentUserId = pref.getInt("KEY_USER_ID", -1);
        RecipeAdapter adapter = new RecipeAdapter(popularList, currentUserId, dbHelper);
        rvPopularRecipes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvPopularRecipes.setAdapter(adapter);

        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getRecipeId());
            intent.putExtra("RECIPE_NAME", recipe.getTitle());
            startActivity(intent);
        });
    }

    private void loadPopularRecipesByCategory(int categoryId) {
        List<Recipe> popularList = dbHelper.getPopularRecipesByCategory(categoryId, 4);
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int currentUserId = pref.getInt("KEY_USER_ID", -1);
        RecipeAdapter adapter = new RecipeAdapter(popularList, currentUserId, dbHelper);
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
            tvTodayPickRating.setText("⭐ " + randomRecipe.getRating());

            int imageResId = requireContext().getResources().getIdentifier(
                    randomRecipe.getImage(),
                    "drawable",
                    requireContext().getPackageName()
            );

            if (imageResId != 0) {
                imgTodayPick.setImageResource(imageResId);
            } else {
                imgTodayPick.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            cardTodayPick.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("RECIPE_ID", randomRecipe.getRecipeId());
                intent.putExtra("RECIPE_NAME", randomRecipe.getTitle());
                startActivity(intent);
            });
        }
    }
}
