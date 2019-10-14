package ru.buylist.lists;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.BuyList;
import ru.buylist.databinding.ItemListBinding;

/**
 * Адаптер для коллекции списков
 **/

public class BuyListAdapter extends RecyclerSwipeAdapter<BuyListAdapter.BuyListHolder> {
    private final BuyListClickCallback callback;
    private List<BuyList> lists;

    BuyListAdapter(List<BuyList> lists, BuyListClickCallback callback) {
        this.lists = lists;
        this.callback = callback;
    }

    @NonNull
    @Override
    public BuyListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemListBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_list,
                viewGroup, false);
        binding.setCallback(callback);  // отслеживание кликов по итемам, кнопкам редактирования и удаления
        return new BuyListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final BuyListHolder holder, int i) {
        BuyList buyList = lists.get(i);
        holder.bind(buyList);

        // обработка свайпа
        mItemManger.bindView(holder.itemView, i);
        holder.binding.swipeLayoutList.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);
                holder.binding.cardLists.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
                holder.binding.cardLists.setBackgroundColor(Color.WHITE);
                holder.binding.bottomList.setBackgroundColor(Color.WHITE);
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
        return lists == null ? 0 : lists.size();
    }

    void setLists(List<BuyList> lists) {
        this.lists = lists;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout_list;
    }

    static class BuyListHolder extends RecyclerView.ViewHolder {
        ItemListBinding binding;
        private BuyList buyList;

        BuyListHolder(ItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BuyList buyList) {
            this.buyList = buyList;
            binding.setItem(buyList);  // устанавливает значение переменной item в item_list
            binding.buyListName.setText(buyList.getTitle());
            binding.swipeLayoutList.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.swipeLayoutList.addDrag(SwipeLayout.DragEdge.Right, binding.bottomList);
            binding.cardLists.setBackgroundColor(0);
            binding.executePendingBindings();
        }
    }
}


