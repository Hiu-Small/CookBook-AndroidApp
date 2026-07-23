package com.example.cookbook;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class GroceryChildAdapter extends RecyclerView.Adapter<GroceryChildAdapter.ViewHolder> {

    private List<GroceryItem> itemList;
    private OnChildActionListener listener;

    public interface OnChildActionListener {
        void onCheckChanged(GroceryItem item, boolean isChecked);
        void onDeleteItem(GroceryItem item);
    }

    public GroceryChildAdapter(List<GroceryItem> itemList, OnChildActionListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryItem item = itemList.get(position);

        String fullText = (item.getQuantity() != null && !item.getQuantity().isEmpty())
                ? item.getQuantity() + " " + item.getIngredientName()
                : item.getIngredientName();

        holder.cbItem.setText(fullText);

        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(item.isChecked());

        updateCardStyle(holder, item.isChecked());

        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            updateCardStyle(holder, isChecked);
            if (listener != null) listener.onCheckChanged(item, isChecked);
        });

        // Bắt sự kiện bấm Nút Trừ màu hồng
        holder.btnDeleteItem.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteItem(item);
        });
    }

    private void updateCardStyle(ViewHolder holder, boolean isChecked) {
        if (isChecked) {
            holder.cardItem.setCardBackgroundColor(Color.parseColor("#E6F7F5"));
            holder.cardItem.setStrokeColor(Color.parseColor("#A7F3D0"));
            holder.cardItem.setStrokeWidth(3);
            holder.cardItem.setCardElevation(0);
            holder.cbItem.setTextColor(Color.parseColor("#8C919E"));
            holder.cbItem.setPaintFlags(holder.cbItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.cardItem.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.cardItem.setStrokeWidth(0);
            holder.cardItem.setCardElevation(3);
            holder.cbItem.setTextColor(Color.parseColor("#2B2D42"));
            holder.cbItem.setPaintFlags(holder.cbItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() { return itemList != null ? itemList.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardItem;
        CheckBox cbItem;
        MaterialButton btnDeleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.cardItem);
            cbItem = itemView.findViewById(R.id.cbItem);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}