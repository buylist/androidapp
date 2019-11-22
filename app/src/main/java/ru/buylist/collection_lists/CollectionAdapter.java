package ru.buylist.collection_lists;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.*;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.databinding.ItemCollectionListBinding;

import static ru.buylist.utils.ItemClickCallback.*;

/**
 * Адаптер для коллекции списков
 **/

public class CollectionAdapter extends RecyclerSwipeAdapter<CollectionAdapter.BuyListHolder> {
    private static final String TAG = "TAG";

    private final ItemCollectionCallback callback;
    private List<Collection> lists;

    CollectionAdapter(ItemCollectionCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public BuyListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCollectionListBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_collection_list,
                viewGroup, false);
        binding.setCallback(callback);  // отслеживание кликов по итемам, кнопкам редактирования и удаления
        return new BuyListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final BuyListHolder holder, int i) {
        Collection collection = lists.get(i);
        holder.bind(collection);

        // обработка свайпа
        mItemManger.bind(holder.itemView, i);
        holder.binding.layoutSwipe.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);
                holder.binding.layoutSwipe.setBackgroundResource(R.drawable.horizontal_border);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
                holder.binding.layoutSwipe.setBackground(null);
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

    void setLists(final List<Collection> lists) {
        this.lists = lists;
        notifyDataSetChanged();
        Log.i(TAG, "Collection update");
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_swipe;
    }

    /**
     * Holder
     */
    static class BuyListHolder extends RecyclerView.ViewHolder {
        ItemCollectionListBinding binding;
        private Collection collection;

        BuyListHolder(ItemCollectionListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Collection collection) {
            this.collection = collection;
            binding.setItem(collection);  // устанавливает значение переменной item в item_collection_list
            binding.textViewCollectionName.setText(collection.getTitle());
            binding.layoutSwipe.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.layoutSwipe.addDrag(SwipeLayout.DragEdge.Right, binding.layoutBottomSwipe);
            binding.cardTopSwipe.setBackgroundColor(0);
            binding.executePendingBindings();
        }
    }
}


