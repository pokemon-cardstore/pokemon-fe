package com.example.pokemonshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.model.Category;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;
    private OnCategoryClickListener onCategoryClickListener;
    private int selectedPosition = 0; // Default to first item (All)

    public CategoryRecyclerViewAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textViewName.setText(category.getName());

        // Set visual feedback for selected category
        if (position == selectedPosition) {
            // Selected category styling
            holder.textViewName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.itemView.setBackgroundResource(R.drawable.rounded_button_background);
        } else {
            // Unselected category styling
            holder.textViewName.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.itemView.setBackgroundResource(R.drawable.rounded_recyclerview_background);
        }

        holder.itemView.setOnClickListener(v -> {
            // Update selected position
            int previousPosition = selectedPosition;
            selectedPosition = position;
            
            // Notify adapter to update visual feedback
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.category_name);
        }
    }
}
