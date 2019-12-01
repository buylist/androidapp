package ru.buylist.buy_list;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentBuyListBinding;
import ru.buylist.utils.SnackbarUtils;

import static ru.buylist.utils.ItemClickCallback.*;


public class BuyListFragment extends Fragment {
    private static final String TAG = "TAG";

    private static final String ARG_COLLECTION_ID = "buy_list_id";

    private Collection collection;
    private List<Item> items;
    private long collectionId;

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

        collectionId = getArguments().getLong(ARG_COLLECTION_ID);
        Log.i(TAG, "BuyListFragment get collectionId: " + collectionId);

        viewModel.showActivityLayout();
        subscribeCollection(viewModel.getCollection(collectionId));
        subscribeUi(viewModel.getItems(collectionId));
        setupSnackbar();
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

        adapter = new BuyListAdapter(itemCallback, swipeListener);
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
            viewModel.createNewItem(0);
        });
        Log.i(TAG, "ShoppingList newFAB activated");

        visibilityFab.setOnClickListener(v -> {
            viewModel.updateFiltering();
            viewModel.loadItems(items);
        });
        Log.i(TAG, "ShoppingList visFAB activated");

        // скрытие fab при  прокрутке с анимацией
        binding.recyclerItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.showHideFab(dy);
            }
        });
    }

    private void setupSnackbar() {
        viewModel.getSnackbarMessage().observe(this, msg -> {
            if (msg != null) {
                SnackbarUtils.showSnackbar(getView(), getString(msg));
            }
        });
    }

    private final BuyListCallback buyListCallback = new BuyListCallback() {
        @Override
        public void onPatternButtonClick() {
            viewModel.openPatternDialog(collection.getId());
        }

        @Override
        public void onRecipeButtonClick() {
            viewModel.openRecipeDialog(collection.getId());
        }
    };

    private final ItemCallback itemCallback = new ItemCallback() {
        @Override
        public void onItemClick(Item item) {
            viewModel.checkItem(item);
            adapter.closeAllItems();
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
            viewModel.createNewItem(item.getId());
            adapter.closeAllItems();
            Log.i(TAG, "BuyList edit item: " + item.getId());
        }
    };

    private final SwipeLayout.SwipeListener swipeListener = new SwipeLayout.SwipeListener() {
        @Override
        public void onStartOpen(SwipeLayout layout) {
            layout.setBackgroundResource(R.drawable.horizontal_border);
            viewModel.fabIsShown.set(false);
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
            viewModel.fabIsShown.set(true);
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    };
}
