package com.example.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView cardPhoBo = view.findViewById(R.id.cardPhoBo);

        // 2. Bắt sự kiện click
        cardPhoBo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);

            // (Tùy chọn) Truyền dữ liệu tên món sang nếu muốn dùng động sau này
            intent.putExtra("RECIPE_NAME", "Phở Bò Hà Nội");

            startActivity(intent);
        });

        // Ánh xạ View và viết logic riêng của trang chủ ở đây
        MaterialButton btnSpin = view.findViewById(R.id.btnSpin);
        btnSpin.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang chọn món ngẫu nhiên...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

}