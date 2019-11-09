package ru.buylist.buy_list;

import android.arch.lifecycle.LiveData;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.collection_lists.CollectionType;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentBuyListBinding;

import static ru.buylist.utils.ItemClickCallback.*;


public class BuyListFragment extends Fragment {
    private static final String TAG = "TAG";

    private static final String ARG_COLLECTION_ID = "buy_list_id";

    private Collection collection;
    private List<Item> items;

    private BuyListViewModel viewModel;
    private FragmentBuyListBinding binding;

    private EditText productField;

    private BuyListAdapter adapter;

    public static BuyListFragment newInstance(long collectionId) {
        Bundle args = new Bundle();
        args.putLong(ARG_COLLECTION_ID, collectionId);

        BuyListFragment fragment = new BuyListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        items = new ArrayList<>();

        viewModel = BuyListActivity.obtainViewModel(getActivity());

        long collectionId = getArguments().getLong(ARG_COLLECTION_ID);
        Log.i(TAG, "BuyListFragment get collectionId: " + collectionId);

        viewModel.showActivityLayout();
        subscribeCollection(viewModel.getCollection(collectionId));
        subscribeUi(viewModel.getItems(collectionId));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.updateCollection(collection);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(buyListCallback);

        adapter = new BuyListAdapter(itemCallback);
        binding.recyclerItems.setAdapter(adapter);
        initUi(binding.getRoot());
        setupFab();
        return binding.getRoot();
    }

    private void initUi(View view) {
        productField = view.findViewById(R.id.field_name);
    }

    private void subscribeUi(LiveData<List<Item>> liveData) {
        liveData.observe(this, items -> {
            if (items != null) {
                this.items.clear();
                this.items.addAll(items);
                viewModel.loadItems(items);
            }
            binding.executePendingBindings();
            Log.i(TAG, "ShoppingList get new list livedata: ");
        });
    }

    private void subscribeCollection(LiveData<Collection> liveData) {
        liveData.observe(this, newCollection -> {
            collection = newCollection;
            Log.i(TAG, "ShoppingList get new collection livedata: " + newCollection.getId());
        });
    }

    private void setupFab() {
        FloatingActionButton newItemFab = getActivity().findViewById(R.id.fab_new_item);
        FloatingActionButton visibilityFab = getActivity().findViewById(R.id.fab_visibility);

        newItemFab.setOnClickListener(v -> {
            viewModel.showLayoutFields(productField);
            setupCreateButton(0);
        });
        Log.i(TAG, "ShoppingList newFAB activated");

        visibilityFab.setOnClickListener(v -> {
            viewModel.updateFiltering();
            viewModel.loadItems(items);
        });
        Log.i(TAG, "ShoppingList visFAB activated");
    }

    private void setupCreateButton(final long itemId) {
        ImageButton createButton = getActivity().findViewById(R.id.btn_create_item);
        createButton.setOnClickListener(v -> {
            viewModel.saveItem(productField, collection.getId(), itemId);
            Log.i(TAG, "ShoppingList save new item: " + itemId);
        });
    }

    private final BuyListCallback buyListCallback = new BuyListCallback() {
        @Override
        public void onPatternButtonClick() {
            viewModel.openDialog(CollectionType.PATTERN);
        }

        @Override
        public void onRecipeButtonClick() {
            viewModel.openDialog(CollectionType.RECIPE);
        }
    };

    private final ItemCallback itemCallback = new ItemCallback() {
        @Override
        public void onItemClick(Item item) {
            viewModel.checkItem(item);
            Log.i(TAG, "BuyList on item click: " + item.getId());
        }

        @Override
        public void onDeleteButtonClick(Item item) {
            Log.i(TAG, "BuyList delete item: " + item.getId());
            viewModel.deleteItem(item);
            adapter.closeAllItems();
        }

        @Override
        public void onEditButtonClick(Item item) {
            viewModel.editItem(item);
            setupCreateButton(item.getId());
            adapter.closeAllItems();
            Log.i(TAG, "BuyList edit item: " + item.getId());
        }
    };
}
