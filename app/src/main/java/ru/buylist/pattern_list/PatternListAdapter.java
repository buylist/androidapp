package ru.buylist.pattern_list;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.R;
import ru.buylist.buy_list.CategoryInfo;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.ItemProductBinding;

import static ru.buylist.utils.ItemClickCallback.*;

public class PatternListAdapter extends RecyclerSwipeAdapter<PatternListAdapter.PatternListHolder> {

    private final ItemCallback callback;
    private List<Item> items;

    public PatternListAdapter(ItemCallback callback) {
        this.callback = callback;
    }

    @Override
    public PatternListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProductBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_product,
                parent, false);
        binding.setCallback(callback);
        return new PatternListHolder(binding);
    }

    @Override
    public void onBindViewHolder(PatternListHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);

        mItemManger.bindView(holder.itemView, position);
        holder.binding.layoutSwipeItem.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);
                holder.binding.layoutSwipeItem.setBackgroundResource(R.drawable.horizontal_border);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
                holder.binding.layoutSwipeItem.setBackground(null);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_swipe_item;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    /**
     * Holder
     */
    static class PatternListHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        Item item;

        PatternListHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Item item) {
            this.item = item;
            binding.setItem(item);
            binding.imgCategoryCircle.setImageResource(R.drawable.circle_empty);
            bindColor(item);
            binding.layoutSwipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.layoutSwipeItem.addDrag(SwipeLayout.DragEdge.Right, binding.layoutBottomSwipe);
            binding.cardTopItemSwipe.setBackgroundColor(0);
            binding.executePendingBindings();
        }

        void bindColor(Item item) {
            if (item.getCategoryColor() == null) {
                binding.imgCategoryCircle.setColorFilter(Color.parseColor(CategoryInfo.COLOR));
            } else {
                binding.imgCategoryCircle.setColorFilter(Color.parseColor(item.getCategoryColor()));
            }
        }
    }
}
