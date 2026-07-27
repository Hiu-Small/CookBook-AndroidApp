package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    // Views
    private TextView tvHeaderName, tvHeaderEmail, tvRecipesCookedCount, tvFavoritesSavedCount;
    private EditText etFullName, etEmail, etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSaveChanges, btnLogOut;

    private DatabaseHelper dbHelper;
    private int currentUserId = -1;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View
        initViews(view);

        // 2. Khởi tạo Database & Lấy User ID từ SharedPreferences
        dbHelper = DatabaseHelper.getInstance(requireContext());
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = pref.getInt("KEY_USER_ID", -1);

        // 3. Tải dữ liệu động lên giao diện
        loadUserData();

        // 4. Bắt sự kiện Nút Save Changes
        btnSaveChanges.setOnClickListener(v -> saveUserChanges());

        // 5. Bắt sự kiện Nút Log Out
        btnLogOut.setOnClickListener(v -> showLogoutDialog());
    }

    private void initViews(View view) {
        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvHeaderEmail = view.findViewById(R.id.tvHeaderEmail);
        tvRecipesCookedCount = view.findViewById(R.id.tvRecipesCookedCount);
        tvFavoritesSavedCount = view.findViewById(R.id.tvFavoritesSavedCount);

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnLogOut = view.findViewById(R.id.btnLogOut);
    }

    private void loadUserData() {
        if (currentUserId == -1) return;

        // Lấy thông tin user từ CSDL
        currentUser = dbHelper.getUserById(currentUserId);

        if (currentUser != null) {
            // Hiển thị tên & email trên Header
            tvHeaderName.setText(currentUser.getFullName());
            tvHeaderEmail.setText(currentUser.getEmail());

            // Điền thông tin vào các ô nhập
            etFullName.setText(currentUser.getFullName());
            etEmail.setText(currentUser.getEmail());
        }

        // Lấy đếm Recipes Cooked & Favorites Saved
        int cookedCount = dbHelper.getCookedRecipesCount(currentUserId);
        int favoritesCount = dbHelper.getFavoritesSavedCount(currentUserId);

        tvRecipesCookedCount.setText(String.valueOf(cookedCount));
        tvFavoritesSavedCount.setText(String.valueOf(favoritesCount));
    }

    private void saveUserChanges() {
        if (currentUser == null) return;

        String newFullName = etFullName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        String currentPassInput = etCurrentPassword.getText().toString().trim();
        String newPassInput = etNewPassword.getText().toString().trim();
        String confirmPassInput = etConfirmPassword.getText().toString().trim();

        // Validate cơ bản
        if (newFullName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng không để trống Họ tên và Email!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isProfileUpdated = false;
        boolean isPasswordUpdated = false;

        // 1. Cập nhật Full Name và Email nếu có thay đổi
        if (!newFullName.equals(currentUser.getFullName()) || !newEmail.equals(currentUser.getEmail())) {
            isProfileUpdated = dbHelper.updateUserProfile(currentUserId, newFullName, newEmail);
        }

        // 2. Xử lý Đổi Mật Khẩu (nếu người dùng có điền vào các ô mật khẩu)
        if (!currentPassInput.isEmpty() || !newPassInput.isEmpty() || !confirmPassInput.isEmpty()) {

            // Kiểm tra xem đã nhập đủ 3 ô chưa
            if (currentPassInput.isEmpty() || newPassInput.isEmpty() || confirmPassInput.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin để đổi mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra Mật khẩu hiện tại có đúng không
            if (!currentPassInput.equals(currentUser.getPassword())) {
                Toast.makeText(requireContext(), "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra Mật khẩu mới và Mật khẩu xác nhận có khớp không
            if (!newPassInput.equals(confirmPassInput)) {
                Toast.makeText(requireContext(), "Mật khẩu mới và xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật Mật khẩu vào CSDL
            isPasswordUpdated = dbHelper.updateUserPassword(currentUserId, newPassInput);

            // Xóa sạch các ô nhập mật khẩu sau khi đổi thành công
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");
        }

        // Thông báo kết quả cho người dùng
        if (isProfileUpdated || isPasswordUpdated) {
            Toast.makeText(requireContext(), "Lưu thay đổi thành công!", Toast.LENGTH_SHORT).show();
            loadUserData(); // Tải lại dữ liệu để cập nhật Header
        } else {
            Toast.makeText(requireContext(), "Không có thay đổi nào được ghi nhận.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        // 1. Xóa Session trong SharedPreferences
        SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        // 2. Chuyển hướng về màn hình Login (Xóa toàn bộ Backstack)
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();

        Toast.makeText(requireContext(), "Đã đăng xuất tài khoản!", Toast.LENGTH_SHORT).show();
    }
}