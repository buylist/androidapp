package ru.buylist.buy_list;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.ItemProductBinding;

public class BuyListAdapter extends RecyclerSwipeAdapter<BuyListAdapter.ShoppingListHolder> {

    private static final String TAG = "TAG";

    private final BuyListCallback callback;
    private List<Item> items;

    public BuyListAdapter(BuyListCallback callback) {
        this.callback = callback;
    }

    @Override
    public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProductBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_product,
                parent, false);
        binding.setCallback(callback);  // отслеживание кликов
        return new ShoppingListHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ShoppingListHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);

        mItemManger.bindView(holder.itemView, position);

        holder.binding.layoutSwipeItem.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);
                holder.binding.cardTopItemSwipe.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
                holder.binding.cardTopItemSwipe.setBackgroundColor(0);
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


    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
        Log.i(TAG, "ShoppingList update item. New size: " + items.size());
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_swipe_item;
    }


    /**
     * Holder
     */
    static class ShoppingListHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        Item item;

        ShoppingListHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Item item) {
            this.item = item;
            binding.setItem(item);
            binding.layoutSwipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.layoutSwipeItem.addDrag(SwipeLayout.DragEdge.Right, binding.layoutBottomSwipe);
            binding.cardTopItemSwipe.setBackgroundColor(0);
            binding.executePendingBindings();
        }
    }
}
