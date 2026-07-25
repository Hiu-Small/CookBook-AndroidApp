package com.example.cookbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class GroceryParentAdapter extends RecyclerView.Adapter<GroceryParentAdapter.ViewHolder> {

    private List<GrocerySection> sectionList;
    private OnParentActionListener parentListener;

    public interface OnParentActionListener {
        void onCheckChanged(GroceryItem item, boolean isChecked);
        void onDeleteItem(GroceryItem item);
        void onDeleteSection(GrocerySection section);
    }

    public GroceryParentAdapter(List<GrocerySection> sectionList, OnParentActionListener parentListener) {
        this.sectionList = sectionList;
        this.parentListener = parentListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrocerySection section = sectionList.get(position);

        holder.tvSectionTitle.setText(section.getSectionTitle());

        int completed = 0;
        for (GroceryItem item : section.getItemList()) {
            if (item.isChecked()) completed++;
        }
        holder.tvSectionProgress.setText(completed + "/" + section.getItemList().size());

        // Cấu hình Child Adapter
        GroceryChildAdapter childAdapter = new GroceryChildAdapter(section.getItemList(), new GroceryChildAdapter.OnChildActionListener() {
            @Override
            public void onCheckChanged(GroceryItem item, boolean isChecked) {
                if (parentListener != null) parentListener.onCheckChanged(item, isChecked);
            }

            @Override
            public void onDeleteItem(GroceryItem item) {
                if (parentListener != null) parentListener.onDeleteItem(item);
            }
        });

        holder.rvChildItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvChildItems.setAdapter(childAdapter);

        // Bắt sự kiện bấm Dấu X màu hồng góc phải món
        holder.btnDeleteSection.setOnClickListener(v -> {
            if (parentListener != null) parentListener.onDeleteSection(section);
        });
    }

    @Override
    public int getItemCount() { return sectionList != null ? sectionList.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle, tvSectionProgress;
        MaterialButton btnDeleteSection;
        RecyclerView rvChildItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            tvSectionProgress = itemView.findViewById(R.id.tvSectionProgress);
            btnDeleteSection = itemView.findViewById(R.id.btnDeleteSection);
            rvChildItems = itemView.findViewById(R.id.rvChildItems);
        }
    }
}