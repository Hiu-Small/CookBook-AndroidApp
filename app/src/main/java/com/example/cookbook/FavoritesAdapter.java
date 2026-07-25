package com.example.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Recipe> recipeList;
    private OnItemClickListener listener;

    // Interface bắt sự kiện khi bấm vào 1 thẻ món ăn (để chuyển sang màn hình Chi tiết)
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Constructor nhận danh sách món ăn
    public FavoritesAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        Context context = holder.itemView.getContext();

        // 1. Hiển thị thông tin dạng chữ
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvCookTime.setText("⏰ " + String.valueOf(recipe.getCookTime()) + " min");
        holder.tvDifficulty.setText(recipe.getDifficulty());
        holder.tvRating.setText("⭐ " + String.valueOf(recipe.getRating()));

        // 2. Chuyển tên chuỗi "img_pho_bo" thành ID ảnh trong res/drawable
        int imageResId = context.getResources().getIdentifier(
                recipe.getImage(),
                "drawable",
                context.getPackageName()
        );

        // Nạp ảnh vào ImageView
        if (imageResId != 0) {
            holder.imgRecipe.setImageResource(imageResId);
        } else {
            // Hiển thị ảnh mặc định hệ thống nếu không tìm thấy tên file trong drawable
            holder.imgRecipe.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // 3. Xử lý sự kiện khi bấm vào cả thẻ món ăn
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    // Class ViewHolder ánh xạ các View bên trong item_recipe_card.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView tvTitle, tvCookTime, tvDifficulty, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            tvTitle = itemView.findViewById(R.id.tvRecipeName);
            tvCookTime = itemView.findViewById(R.id.tvCookTime);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}