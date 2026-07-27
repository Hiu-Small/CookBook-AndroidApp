package com.example.cookbook;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private TextView tvSavedCount;
    private TextView tvStatSaved, tvStatAvgKcal, tvStatAvgMin;
    private Button btnSortNewest, btnSortAZ, btnLevel;
    private RecyclerView rvFavorites;

    private FavoritesAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseHelper dbHelper;

    // Giả định userId hiện tại là 1 (Sau này bạn lấy từ Session/SharedPreferences khi đăng nhập)
    private int currentUserId = 1;

    // Chuỗi SQL đếm bậc độ khó: Easy -> 1, Medium -> 2, Hard -> 3
    private static final String SQL_SORT_EASY_TO_HARD =
            "CASE LOWER(TRIM(r.difficulty)) WHEN 'easy' THEN 1 WHEN 'medium' THEN 2 WHEN 'hard' THEN 3 ELSE 4 END ASC";
    private static final String SQL_SORT_HARD_TO_EASY =
            "CASE LOWER(TRIM(r.difficulty)) WHEN 'easy' THEN 1 WHEN 'medium' THEN 2 WHEN 'hard' THEN 3 ELSE 4 END DESC";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ các View từ XML
        tvSavedCount = view.findViewById(R.id.tvSavedCount);
        btnSortNewest = view.findViewById(R.id.btnSortNewest);
        btnSortAZ = view.findViewById(R.id.btnSortAZ);
        btnLevel = view.findViewById(R.id.btnLevel);
        rvFavorites = view.findViewById(R.id.rvFavorites);

        // ⚡ Ánh xạ các TextView thống kê ở đáy
        tvStatSaved = view.findViewById(R.id.tvStatSaved);
        tvStatAvgKcal = view.findViewById(R.id.tvStatAvgKcal);
        tvStatAvgMin = view.findViewById(R.id.tvStatAvgMin);

        //LẤY USER_ID ĐÃ LƯU LÚC ĐĂNG NHẬP
        android.content.SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
        currentUserId = pref.getInt("KEY_USER_ID", -1);

        // 2. Khởi tạo DatabaseHelper
        dbHelper = DatabaseHelper.getInstance(requireContext());

        // 3. Khởi tạo RecyclerView dạng lưới 2 cột
        recipeList = new ArrayList<>();
        adapter = new FavoritesAdapter(recipeList);
        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvFavorites.setAdapter(adapter);

        // 4. Mặc định tải danh sách xếp theo "Mới nhất" (favoriteAt giảm dần)
        loadFavoriteRecipes("f.favoriteAt DESC");

        // 5. Bắt sự kiện bấm nút "Newest"
        btnSortNewest.setOnClickListener(v -> {
            loadFavoriteRecipes("f.favoriteAt DESC");

            btnSortNewest.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2B2D42")));
            btnSortNewest.setTextColor(Color.parseColor("#FFFFFF"));

            btnSortAZ.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnSortAZ.setTextColor(Color.parseColor("#8C919E"));

            btnLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnLevel.setTextColor(Color.parseColor("#8C919E"));
        });

        // 6. Bắt sự kiện bấm nút "A → Z"
        btnSortAZ.setOnClickListener(v -> {
            loadFavoriteRecipes("r.title ASC");

            btnSortNewest.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnSortNewest.setTextColor(Color.parseColor("#8C919E"));

            btnSortAZ.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2B2D42")));
            btnSortAZ.setTextColor(Color.parseColor("#FFFFFF"));

            btnLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnLevel.setTextColor(Color.parseColor("#8C919E"));
        });

        btnLevel.setOnClickListener(v -> {
            String currentText = btnLevel.getText().toString();

            if((btnSortNewest.getBackgroundTintList().getDefaultColor() == Color.parseColor("#2B2D42") || btnSortAZ.getBackgroundTintList().getDefaultColor() == Color.parseColor("#2B2D42")) && currentText.contains("▼")){
                loadFavoriteRecipes(SQL_SORT_HARD_TO_EASY);
                btnLevel.setText("Level ▼");
            }
            else if((btnSortNewest.getBackgroundTintList().getDefaultColor() == Color.parseColor("#2B2D42") || btnSortAZ.getBackgroundTintList().getDefaultColor() == Color.parseColor("#2B2D42")) && currentText.contains("▲")){
                loadFavoriteRecipes(SQL_SORT_EASY_TO_HARD);
                btnLevel.setText("Level ▲");
            }
            else if (currentText.contains("▲")) {
                loadFavoriteRecipes(SQL_SORT_HARD_TO_EASY);
                btnLevel.setText("Level ▼");
            } else if(currentText.contains("▼")) {
                loadFavoriteRecipes(SQL_SORT_EASY_TO_HARD);
                btnLevel.setText("Level ▲");
            }

            btnSortNewest.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnSortNewest.setTextColor(Color.parseColor("#8C919E"));

            btnSortAZ.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FBF8F5")));
            btnSortAZ.setTextColor(Color.parseColor("#8C919E"));

            btnLevel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2B2D42")));
            btnLevel.setTextColor(Color.parseColor("#FFFFFF"));
        });
    }

    // Hàm truy vấn lại dữ liệu từ CSDL và cập nhật UI
    private void loadFavoriteRecipes(String orderBy) {
        recipeList.clear();
        recipeList.addAll(dbHelper.getFavoriteRecipes(currentUserId, orderBy));

        // Cập nhật số lượng món lưu (Ví dụ: "2 saved recipes")
        tvSavedCount.setText(recipeList.size() + " saved recipes");

        // Báo Adapter cập nhật lại giao diện hiển thị
        adapter.notifyDataSetChanged();

        updateStatsCard();
    }

    private void updateStatsCard() {
        int savedCount = recipeList.size();

        // Xử lý trường hợp danh sách trống (tránh chia cho 0)
        if (savedCount == 0) {
            tvStatSaved.setText("0");
            tvStatAvgKcal.setText("0");
            tvStatAvgMin.setText("0");
            return;
        }

        int totalKcal = 0;
        int totalMin = 0;

        for (Recipe recipe : recipeList) {
            totalKcal += recipe.getCalories();
            totalMin += recipe.getCookTime();
        }

        int avgKcal = totalKcal / savedCount;
        int avgMin = totalMin / savedCount;

        tvStatSaved.setText(String.valueOf(savedCount));
        tvStatAvgKcal.setText(String.valueOf(avgKcal));
        tvStatAvgMin.setText(String.valueOf(avgMin));
    }
}