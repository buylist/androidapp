package ru.buylist.pattern_list;

import android.arch.lifecycle.LiveData;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentPatternListBinding;
import ru.buylist.utils.SnackbarUtils;

import static ru.buylist.utils.ItemClickCallback.*;

public class PatternListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_PATTERN_ID = "pattern_id";

    private Collection collection;
    private List<Item> items;
    private List<Item> selectedItems;

    private PatternListViewModel viewModel;
    private FragmentPatternListBinding binding;
    private PatternListAdapter adapter;

    public static PatternListFragment newInstance(long collectionId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PATTERN_ID, collectionId);

        PatternListFragment fragment = new PatternListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        selectedItems = new ArrayList<>();

        viewModel = PatternListActivity.obtainViewModel(getActivity());
        viewModel.bottomShow.set(true);
        viewModel.fabShow.set(true);

        long collectionId = getArguments() != null ? getArguments().getLong(ARG_PATTERN_ID, 0) : 0;
        subscribeUi(viewModel.getItems(collectionId));
        subscribeCollection(viewModel.getCollection(collectionId));
        setupSnackbar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pattern_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

        adapter = new PatternListAdapter(itemCallback, swipeListener);
        binding.recyclerItems.setAdapter(adapter);
        setupFab();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.deleteSelected();
    }

    // подписка на изменения списка товаров
    private void subscribeUi(LiveData<List<Item>> liveData) {
        liveData.observe(this, newItems -> {
            if (newItems != null) {
                items.clear();
                items.addAll(newItems);
                viewModel.loadItems(newItems);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribeCollection(LiveData<Collection> liveData) {
        liveData.observe(this, newCollection -> collection = newCollection);
    }

    private void setupFab() {
        FloatingActionButton addItemButton = getActivity().findViewById(R.id.fab_new_item);
        addItemButton.setOnClickListener(v -> {
            viewModel.addNewItem();
            setupCreateButton(0);
        });
    }

    // устанавливаем кнопку создания товара
    // для создания нового товара передаем в метод 0
    // для обновления существующего товара - его идентификатор
    private void setupCreateButton(final long itemId) {
        binding.btnCreateItem.setOnClickListener(v -> {
            viewModel.saveItem(collection.getId(), itemId);
        });
    }

    private void setupSnackbar() {
        viewModel.getSnackbarMessage().observe(this, msg -> {
            if (msg != null) {
                SnackbarUtils.showSnackbar(getView(), getString(msg));
            }
        });
    }

    private void saveSelectedItem(Item selectedItem) {
        if (selectedItems.isEmpty()) {
            selectedItems.add(selectedItem);
            selectedItem.setPurchased(true);
            return;
        }

        for (Item item : selectedItems) {
            if (item.getId() == selectedItem.getId()) {
                selectedItems.remove(item);
                selectedItem.setPurchased(false);
                return;
            }
        }

        selectedItems.add(selectedItem);
        selectedItem.setPurchased(true);

    }

    // callback кликов по кнопке переноса товаров в список
    private final PatternListCallback callback = new PatternListCallback() {
        @Override
        public void onToMoveButtonClick(List<Item> items) {
            viewModel.transfer(selectedItems);
            selectedItems.clear();
        }
    };

    // callback'и для адаптера, возвращает клики по кнопкам свайпа
    private final ItemCallback itemCallback = new ItemCallback() {
        @Override
        public void onItemClick(Item item) {
            saveSelectedItem(item);
            viewModel.btnToMoveShow.set(!selectedItems.isEmpty());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDeleteButtonClick(Item item) {
            viewModel.deleteItem(item);
            adapter.closeAllItems();
        }

        @Override
        public void onEditButtonClick(Item item) {
            viewModel.editItem(item);
            setupCreateButton(item.getId());
            adapter.closeAllItems();
        }
    };

    private final SwipeLayout.SwipeListener swipeListener = new SwipeLayout.SwipeListener() {
        @Override
        public void onStartOpen(SwipeLayout layout) {
            layout.setBackgroundResource(R.drawable.horizontal_border);
            viewModel.fabShow.set(false);
        }

        @Override
        public void onOpen(SwipeLayout layout) {

        }

        @Override
        public void onStartClose(SwipeLayout layout) {

        }

        @Override
        public void onClose(SwipeLayout layout) {
            layout.setBackground(null);
            viewModel.fabShow.set(true);
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    };

}
