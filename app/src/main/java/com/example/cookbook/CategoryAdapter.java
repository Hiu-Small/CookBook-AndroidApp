package com.example.cookbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private OnItemClickListener listener;
    private int selectedPosition = 0; // Mặc định chọn mục đầu tiên ("Tất cả")

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getCategoryName());

        // Kiểm tra xem item có đang được chọn hay không
        if (selectedPosition == position) {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.orange_primary));
            holder.tvCategoryName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.cardCategory.setCardElevation(4f);
        } else {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvCategoryName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_dark));
            holder.cardCategory.setCardElevation(1f);
        }
        
        holder.itemView.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            
            // Chỉ notify những item cần thiết để tối ưu
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        MaterialCardView cardCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            cardCategory = itemView.findViewById(R.id.cardCategory);
        }
    }
}