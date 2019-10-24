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
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.buy_list.BuyListCallback;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentPatternListBinding;

public class PatternListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_PATTERN_ID = "pattern_id";

    private Collection collection;
    private List<Item> items;

    private PatternListViewModel viewModel;
    private FragmentPatternListBinding binding;
    private PatternListAdapter adapter;

    public static PatternListFragment newInstance(long patternId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PATTERN_ID, patternId);

        PatternListFragment fragment = new PatternListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        viewModel = PatternListActivity.obtainViewModel(getActivity());

        long patternId = getArguments() != null ? getArguments().getLong(ARG_PATTERN_ID, 0) : 0;
        subscribeUi(viewModel.getItems(patternId));
        subscribeCollection(viewModel.getCollection(patternId));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pattern_list, container, false);
        binding.setViewmodel(viewModel);

        adapter = new PatternListAdapter(itemCallback);
        binding.recyclerItems.setAdapter(adapter);
        setupFab();
        return binding.getRoot();
    }

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

    private void setupCreateButton(final long itemId) {
        ImageButton createButton = getActivity().findViewById(R.id.btn_create_item);
        createButton.setOnClickListener(v -> {
            if (itemId == 0) {
                viewModel.saveItem(collection.getId());
            } else {
                return;
            }
        });
    }

    private final PatternListCallback callback = new PatternListCallback() {

        @Override
        public void onToMoveButtonClick(List<Item> items) {

        }
    };

    private final BuyListCallback itemCallback = new BuyListCallback() {
        @Override
        public void onItemClick(Item item) {

        }

        @Override
        public void onDeleteButtonClick(Item item) {

        }

        @Override
        public void onEditButtonClick(Item item) {
            setupCreateButton(item.getId());
        }
    };

}
