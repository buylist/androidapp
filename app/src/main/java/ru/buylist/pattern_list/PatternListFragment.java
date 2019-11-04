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

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentPatternListBinding;

import static ru.buylist.utils.ItemClickCallback.*;

public class PatternListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_PATTERN_ID = "pattern_id";

    private Collection collection;
    private List<Item> items;

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
        viewModel = PatternListActivity.obtainViewModel(getActivity());
        viewModel.bottomShow.set(true);

        long collectionId = getArguments() != null ? getArguments().getLong(ARG_PATTERN_ID, 0) : 0;
        subscribeUi(viewModel.getItems(collectionId));
        subscribeCollection(viewModel.getCollection(collectionId));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pattern_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

        adapter = new PatternListAdapter(itemCallback);
        binding.recyclerItems.setAdapter(adapter);
        setupFab();
        return binding.getRoot();
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

    // callback кликов по кнопке переноса товаров в список
    private final PatternListCallback callback = new PatternListCallback() {
        @Override
        public void onToMoveButtonClick(List<Item> items) {
            viewModel.openDialog();
        }
    };

    // callback'и для адаптера, возвращает клики по кнопкам свайпа
    private final ItemCallback itemCallback = new ItemCallback() {
        @Override
        public void onItemClick(Item item) {
            viewModel.btnToMoveShow.set(true);
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

}
