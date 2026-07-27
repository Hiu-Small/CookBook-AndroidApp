package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText edtSearchInput;
    private TextView tvResultCount, tvSortBy;
    private RecyclerView rvSearchResults;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseHelper dbHelper;
    private int userId;

    // Filter states
    private String selectedTime = "Any time";
    private String selectedDifficulty = "All";
    private String selectedSort = "Popular";
    private String selectedStatus = "All";

    // View components for filters
    private MaterialCardView cardTimeAny, cardTime15, cardTime30, cardTime60, cardTimeAbove60;
    private MaterialCardView cardDiffAll, cardDiffEasy, cardDiffMedium, cardDiffHard;
    private MaterialCardView cardStatusAll, cardStatusCooked, cardStatusNotCooked;
    private TextView tvTimeAny, tvTime15, tvTime30, tvTime60, tvTimeAbove60;
    private TextView tvDiffAll, tvDiffEasy, tvDiffMedium, tvDiffHard;
    private TextView tvStatusAll, tvStatusCooked, tvStatusNotCooked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = pref.getInt("KEY_USER_ID", -1);

        // Bind Views
        edtSearchInput = view.findViewById(R.id.edtSearchInput);
        tvResultCount = view.findViewById(R.id.tvResultCount);
        tvSortBy = view.findViewById(R.id.tvSortBy);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        // Time filter views
        cardTimeAny = view.findViewById(R.id.cardTimeAny);
        cardTime15 = view.findViewById(R.id.cardTime15);
        cardTime30 = view.findViewById(R.id.cardTime30);
        cardTime60 = view.findViewById(R.id.cardTime60);
        cardTimeAbove60 = view.findViewById(R.id.cardTimeAbove60);
        tvTimeAny = view.findViewById(R.id.tvTimeAny);
        tvTime15 = view.findViewById(R.id.tvTime15);
        tvTime30 = view.findViewById(R.id.tvTime30);
        tvTime60 = view.findViewById(R.id.tvTime60);
        tvTimeAbove60 = view.findViewById(R.id.tvTimeAbove60);

        // Difficulty filter views
        cardDiffAll = view.findViewById(R.id.cardDiffAll);
        cardDiffEasy = view.findViewById(R.id.cardDiffEasy);
        cardDiffMedium = view.findViewById(R.id.cardDiffMedium);
        cardDiffHard = view.findViewById(R.id.cardDiffHard);
        tvDiffAll = view.findViewById(R.id.tvDiffAll);
        tvDiffEasy = view.findViewById(R.id.tvDiffEasy);
        tvDiffMedium = view.findViewById(R.id.tvDiffMedium);
        tvDiffHard = view.findViewById(R.id.tvDiffHard);

        // Status filter views
        cardStatusAll = view.findViewById(R.id.cardStatusAll);
        cardStatusCooked = view.findViewById(R.id.cardStatusCooked);
        cardStatusNotCooked = view.findViewById(R.id.cardStatusNotCooked);
        tvStatusAll = view.findViewById(R.id.tvStatusAll);
        tvStatusCooked = view.findViewById(R.id.tvStatusCooked);
        tvStatusNotCooked = view.findViewById(R.id.tvStatusNotCooked);

        // Khởi tạo RecyclerView
        recipeList = new ArrayList<>();
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int currentUserId = pref.getInt("KEY_USER_ID", -1);
        adapter = new RecipeAdapter(recipeList, currentUserId, dbHelper);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(adapter);

        // Xử lý sự kiện click vào item
        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getRecipeId());
            intent.putExtra("RECIPE_NAME", recipe.getTitle());
            startActivity(intent);
        });

        // Set Up Listeners
        setupFilterListeners();
        setupSearchListener();
        setupSortListener();

        // Nhận từ khóa từ Bundle (nếu có)
        if (getArguments() != null) {
            String query = getArguments().getString("SEARCH_QUERY");
            if (query != null) {
                edtSearchInput.setText(query);
            }
        }

        // Initial load
        applyFilters();

        return view;
    }

    private void setupSearchListener() {
        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterListeners() {
        // Time Listeners
        cardTimeAny.setOnClickListener(v -> selectTime("Any time"));
        cardTime15.setOnClickListener(v -> selectTime("< 15 min"));
        cardTime30.setOnClickListener(v -> selectTime("< 30 min"));
        cardTime60.setOnClickListener(v -> selectTime("< 60 min"));
        cardTimeAbove60.setOnClickListener(v -> selectTime(">= 60 min"));

        // Difficulty Listeners
        cardDiffAll.setOnClickListener(v -> selectDifficulty("All"));
        cardDiffEasy.setOnClickListener(v -> selectDifficulty("Easy"));
        cardDiffMedium.setOnClickListener(v -> selectDifficulty("Medium"));
        cardDiffHard.setOnClickListener(v -> selectDifficulty("Hard"));

        // Status Listeners
        cardStatusAll.setOnClickListener(v -> selectStatus("All"));
        cardStatusCooked.setOnClickListener(v -> selectStatus("Cooked"));
        cardStatusNotCooked.setOnClickListener(v -> selectStatus("Not Cooked"));
    }

    private void setupSortListener() {
        tvSortBy.setOnClickListener(v -> showSortMenu());
    }

    private void selectTime(String time) {
        selectedTime = time;
        updateFilterUI();
        applyFilters();
    }

    private void selectDifficulty(String difficulty) {
        selectedDifficulty = difficulty;
        updateFilterUI();
        applyFilters();
    }

    private void selectStatus(String status) {
        selectedStatus = status;
        updateFilterUI();
        applyFilters();
    }

    private void showSortMenu() {
        PopupMenu popup = new PopupMenu(requireContext(), tvSortBy);
        popup.getMenu().add("Popular");
        popup.getMenu().add("Cooking Time");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle() != null) {
                selectedSort = item.getTitle().toString();
                tvSortBy.setText("Sort by: " + selectedSort + " ▾");
                applyFilters();
            }
            return true;
        });
        popup.show();
    }

    private void applyFilters() {
        String query = edtSearchInput.getText().toString().trim();
        recipeList.clear();

        List<Recipe> results = dbHelper.getFilteredRecipes(userId, query, selectedTime, selectedDifficulty, selectedSort, selectedStatus);
        recipeList.addAll(results);

        tvResultCount.setText(recipeList.size() + " recipes found");
        adapter.notifyDataSetChanged();
    }

    private void updateFilterUI() {
        // Time Filters
        updateCardUI(cardTimeAny, tvTimeAny, selectedTime.equals("Any time"));
        updateCardUI(cardTime15, tvTime15, selectedTime.equals("< 15 min"));
        updateCardUI(cardTime30, tvTime30, selectedTime.equals("< 30 min"));
        updateCardUI(cardTime60, tvTime60, selectedTime.equals("< 60 min"));
        updateCardUI(cardTimeAbove60, tvTimeAbove60, selectedTime.equals(">= 60 min"));

        // Difficulty Filters
        updateCardUI(cardDiffAll, tvDiffAll, selectedDifficulty.equals("All"));
        updateCardUI(cardDiffEasy, tvDiffEasy, selectedDifficulty.equals("Easy"));
        updateCardUI(cardDiffMedium, tvDiffMedium, selectedDifficulty.equals("Medium"));
        updateCardUI(cardDiffHard, tvDiffHard, selectedDifficulty.equals("Hard"));

        // Status Filters
        updateCardUI(cardStatusAll, tvStatusAll, selectedStatus.equals("All"));
        updateCardUI(cardStatusCooked, tvStatusCooked, selectedStatus.equals("Cooked"));
        updateCardUI(cardStatusNotCooked, tvStatusNotCooked, selectedStatus.equals("Not Cooked"));
    }

    private void updateCardUI(MaterialCardView card, TextView tv, boolean isSelected) {
        if (isSelected) {
            card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_primary));
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            card.setCardElevation(4f);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_bg_gray));
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark));
            card.setCardElevation(0f);
        }
    }
}
