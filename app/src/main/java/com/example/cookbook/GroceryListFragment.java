package com.example.cookbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment {

    private TextView tvProgressCount, tvCompleteMessage;
    private ProgressBar progressBar;
    private MaterialButton btnClearCompleted, btnAddItem, btnClearAllList;
    private RecyclerView rvGrocery;

    private DatabaseHelper dbHelper;
    private GroceryParentAdapter parentAdapter;
    private List<GrocerySection> sectionList;

    private int currentUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View
        tvProgressCount = view.findViewById(R.id.tvProgressCount);
        tvCompleteMessage = view.findViewById(R.id.tvCompleteMessage);
        progressBar = view.findViewById(R.id.progressBar);
        btnClearCompleted = view.findViewById(R.id.btnClearCompleted);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        btnClearAllList = view.findViewById(R.id.btnClearAllList);
        rvGrocery = view.findViewById(R.id.rvGrocery);

        // 2. Lấy User Session
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = pref.getInt("KEY_USER_ID", -1);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sectionList = new ArrayList<>();

        // 3. Khởi tạo Adapter với đủ 3 sự kiện
        parentAdapter = new GroceryParentAdapter(sectionList, new GroceryParentAdapter.OnParentActionListener() {
            @Override
            public void onCheckChanged(GroceryItem item, boolean isChecked) {
                dbHelper.updateGroceryItemCheck(item.getItemId(), isChecked);
                parentAdapter.notifyDataSetChanged();
                updateProgressAndUI();
            }

            @Override
            public void onDeleteItem(GroceryItem item) {
                // Xóa 1 nguyên liệu (Nút trừ)
                dbHelper.deleteGroceryItem(item.getItemId());
                loadData();
            }

            @Override
            public void onDeleteSection(GrocerySection section) {
                // Xóa toàn bộ 1 món (Nút X)
                dbHelper.deleteGrocerySection(currentUserId, section.getRecipeId());
                loadData();
            }
        });

        rvGrocery.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGrocery.setAdapter(parentAdapter);

        loadData();

        // 4. Xóa các món đã tick chọn
        btnClearCompleted.setOnClickListener(v -> {
            dbHelper.clearCompletedGroceryItems(currentUserId);
            loadData();
        });

        // 5. Thêm món mới
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        // 6. Xóa TOÀN BỘ danh sách (Nút Clear All List dưới cùng)
        btnClearAllList.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa toàn bộ danh sách đi chợ không?")
                    .setPositiveButton("Xóa tất cả", (dialog, which) -> {
                        dbHelper.clearAllGroceryList(currentUserId);
                        loadData();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void loadData() {
        if (currentUserId != -1) {
            sectionList.clear();
            sectionList.addAll(dbHelper.getGroupedGroceryList(currentUserId));
            parentAdapter.notifyDataSetChanged();
            updateProgressAndUI();
        }
    }

    private void updateProgressAndUI() {
        int totalItems = 0;
        int completedItems = 0;

        for (GrocerySection section : sectionList) {
            for (GroceryItem item : section.getItemList()) {
                totalItems++;
                if (item.isChecked()) completedItems++;
            }
        }

        tvProgressCount.setText(completedItems + "/" + totalItems);
        progressBar.setMax(totalItems == 0 ? 1 : totalItems);
        progressBar.setProgress(completedItems);

        boolean isAllChecked = (totalItems > 0 && completedItems == totalItems);

        if (isAllChecked) {
            progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00B8A9")));
            tvCompleteMessage.setVisibility(View.VISIBLE);
        } else {
            progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF6B35")));
            tvCompleteMessage.setVisibility(View.GONE);
        }

        btnClearCompleted.setText("🗑 Clear " + completedItems + " completed");
        btnClearCompleted.setEnabled(completedItems > 0);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm nguyên liệu mới");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText etName = new EditText(requireContext());
        etName.setHint("Tên nguyên liệu (VD: Sữa tươi)");
        layout.addView(etName);

        final EditText etQty = new EditText(requireContext());
        etQty.setHint("Số lượng (VD: 1 hộp)");
        layout.addView(etQty);

        builder.setView(layout);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String qty = etQty.getText().toString().trim();

            if (!name.isEmpty()) {
                dbHelper.addGroceryItem(currentUserId, name, qty);
                loadData();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên nguyên liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}