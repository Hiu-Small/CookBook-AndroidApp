package com.example.cookbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipeList;
    private int currentUserId;
    private DatabaseHelper dbHelper;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecipeAdapter(List<Recipe> recipeList, int currentUserId, DatabaseHelper dbHelper) {
        this.recipeList = recipeList;
        this.currentUserId = currentUserId;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvCookTime.setText("⏰ " + recipe.getCookTime() + " min");
        holder.tvDifficulty.setText(recipe.getDifficulty());

        // Đổi màu tag độ khó
        String difficulty = recipe.getDifficulty() != null ? recipe.getDifficulty().trim() : "";
        holder.tvDifficulty.setText(difficulty);
        if (difficulty.equalsIgnoreCase("Easy")) {
            holder.tvDifficulty.setBackgroundColor(Color.parseColor("#E6F8F6"));
            holder.tvDifficulty.setTextColor(Color.parseColor("#3CAEAE"));
        } else if (difficulty.equalsIgnoreCase("Medium")) {
            holder.tvDifficulty.setBackgroundColor(Color.parseColor("#FFF6E0"));
            holder.tvDifficulty.setTextColor(Color.parseColor("#EAB42D"));
        } else {
            holder.tvDifficulty.setBackgroundColor(Color.parseColor("#FDECF0"));
            holder.tvDifficulty.setTextColor(Color.parseColor("#D97567"));
        }

        holder.tvRating.setText("⭐ " + recipe.getRating());

        // ⚡ 1. KIỂM TRA MÓN ĂN ĐÃ YÊU THÍCH CHƯA ĐỂ ĐỔI MÀU SAO VÀNG / TRẮNG
        if (dbHelper != null && currentUserId != -1) {
            boolean isFav = dbHelper.isFavorite(currentUserId, recipe.getRecipeId());
            updateStarColor(holder.imgStarIcon, isFav);

            // ⚡ 2. BẤM NÚT TIM: TOGGLE FAVORITE VÀ CẬP NHẬT MÀU MỚI LẬP TỨC
            holder.btnHeart.setOnClickListener(v -> {
                boolean newState = dbHelper.toggleFavorite(currentUserId, recipe.getRecipeId());
                updateStarColor(holder.imgStarIcon, newState);
            });
        }

        int imageResId = context.getResources().getIdentifier(
                recipe.getImage(),
                "drawable",
                context.getPackageName()
        );

        if (imageResId != 0) {
            holder.imgRecipe.setImageResource(imageResId);
        } else {
            holder.imgRecipe.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(recipe);
            }
        });
    }

    // Hàm hỗ trợ đổi màu Ngôi sao (Vàng / Trắng)
    private void updateStarColor(ImageView imgStarIcon, boolean isFav) {
        if (imgStarIcon != null) {
            if (isFav) {
                imgStarIcon.setColorFilter(Color.parseColor("#FFD700")); // Vàng
            } else {
                imgStarIcon.setColorFilter(Color.parseColor("#FFFFFF")); // Trắng
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe, imgStarIcon;
        MaterialCardView btnHeart;
        TextView tvTitle, tvCookTime, tvDifficulty, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            btnHeart = itemView.findViewById(R.id.btnHeart);
            imgStarIcon = itemView.findViewById(R.id.imgStarIcon);
            tvTitle = itemView.findViewById(R.id.tvRecipeName);
            tvCookTime = itemView.findViewById(R.id.tvCookTime);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}