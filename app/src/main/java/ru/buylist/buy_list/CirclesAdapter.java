package ru.buylist.buy_list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Category;
import ru.buylist.databinding.ItemCategoryCircleBinding;

public class CirclesAdapter extends RecyclerView.Adapter<CirclesAdapter.CirclesHolder> {

    private List<Category> categories;
    private String color;

    public CirclesAdapter(List<Category> categories) {
        this.categories = categories;
        color = CategoryInfo.COLOR;
    }

    @NonNull
    @Override
    public CirclesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryCircleBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_category_circle, parent, false);

        binding.setCallback(callback);
        return new CirclesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CirclesHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public String getColor() {
        return color;
    }

    public void setColors(List<Category> colors) {
        categories = colors;
        notifyDataSetChanged();
    }

    private final CategoryCallback.CircleCallback callback = new CategoryCallback.CircleCallback() {
        @Override
        public void onCircleClick(Category category) {
            if (category.isSelected()) {
                category.setSelected(false);
                color = CategoryInfo.COLOR;
            } else {
                category.setSelected(true);
                color = category.getColor();
            }

            for (Category cat : categories) {
                if (!cat.getColor().equals(category.getColor())) {
                    cat.setSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    };


    /**
     * Holder
     */
    static class CirclesHolder extends RecyclerView.ViewHolder {
        ItemCategoryCircleBinding binding;

        public CirclesHolder(ItemCategoryCircleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category) {
            binding.setCategory(category);
            binding.circle.setColorFilter(Color.parseColor(category.getColor()));
        }

    }
}
