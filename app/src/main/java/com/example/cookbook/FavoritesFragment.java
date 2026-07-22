package com.example.cookbook;

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
    private Button btnSortNewest, btnSortAZ;
    private RecyclerView rvFavorites;

    private FavoritesAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseHelper dbHelper;

    // Giả định userId hiện tại là 1 (Sau này bạn lấy từ Session/SharedPreferences khi đăng nhập)
    private int currentUserId = 1;

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
        rvFavorites = view.findViewById(R.id.rvFavorites);

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
        });

        // 6. Bắt sự kiện bấm nút "A → Z"
        btnSortAZ.setOnClickListener(v -> {
            loadFavoriteRecipes("r.title ASC");
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
    }
}