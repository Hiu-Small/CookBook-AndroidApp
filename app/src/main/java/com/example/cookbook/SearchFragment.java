package com.example.cookbook;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText edtSearchInput;
    private TextView tvResultCount;
    private RecyclerView rvSearchResults;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        edtSearchInput = view.findViewById(R.id.edtSearchInput);
        tvResultCount = view.findViewById(R.id.tvResultCount);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        // Khởi tạo RecyclerView
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSearchResults.setAdapter(adapter);

        // Xử lý sự kiện click vào item
        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getRecipeId());
            intent.putExtra("RECIPE_NAME", recipe.getTitle());
            startActivity(intent);
        });

        // Nhận từ khóa từ Bundle (nếu có)
        if (getArguments() != null) {
            String query = getArguments().getString("SEARCH_QUERY");
            if (query != null) {
                edtSearchInput.setText(query);
                performSearch(query);
            }
        }

        // Lắng nghe thay đổi nội dung tìm kiếm
        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void performSearch(String query) {
        recipeList.clear();
        if (query.isEmpty()) {
            // Khi ô tìm kiếm trống, hiển thị toàn bộ món ăn (sắp xếp theo Rating)
            List<Recipe> allRecipes = dbHelper.getPopularRecipes(50); // Lấy tối đa 50 món
            recipeList.addAll(allRecipes);
            tvResultCount.setText(recipeList.size() + " recipes found");
            adapter.notifyDataSetChanged();
            return;
        }

        List<Recipe> results = dbHelper.searchRecipes(query);
        recipeList.addAll(results);
        tvResultCount.setText(recipeList.size() + " recipes found");
        adapter.notifyDataSetChanged();
    }

}