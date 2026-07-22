package com.example.cookbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {
    private DatabaseHelper dbHelper;

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        TextView tvSignUp = view.findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new RegisterFragment()).addToBackStack(null).commit();
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = dbHelper.getUserId(email, password);

            if (userId != -1) {
                // 2. LƯU USER_ID VÀO SHAREDPREFERENCES (PHIÊN ĐĂNG NHẬP)
                android.content.SharedPreferences pref = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE);
                pref.edit().putInt("KEY_USER_ID", userId).apply();

                Toast.makeText(requireContext(), "Đăng nhập thành công! 🎉", Toast.LENGTH_SHORT).show();

                // 3. Chuyển sang MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
