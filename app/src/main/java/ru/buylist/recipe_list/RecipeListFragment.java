package ru.buylist.recipe_list;

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
import ru.buylist.buy_list.BuyListCallback;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentPatternListBinding;
import ru.buylist.databinding.FragmentRecipeListBinding;
import ru.buylist.pattern_list.PatternListActivity;
import ru.buylist.pattern_list.PatternListAdapter;
import ru.buylist.pattern_list.PatternListCallback;
import ru.buylist.pattern_list.PatternListViewModel;

public class RecipeListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_RECIPE_ID = "recipe_id";

    private Collection collection;
    private List<Item> items;

    private RecipeListViewModel viewModel;
    private FragmentRecipeListBinding binding;
//    private PatternListAdapter adapter;

    public static RecipeListFragment newInstance(long collectionId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RECIPE_ID, collectionId);

        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        viewModel = RecipeListActivity.obtainViewModel(getActivity());
        viewModel.bottomShow.set(true);

        long collectionId = getArguments() != null ? getArguments().getLong(ARG_RECIPE_ID, 0) : 0;
        subscribeUi(viewModel.getItems(collectionId));
        subscribeCollection(viewModel.getCollection(collectionId));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

//        adapter = new PatternListAdapter(itemCallback);
//        binding.recyclerIngredients.setAdapter(adapter);
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

    private void setupCreateButton(final long itemId) {
        binding.btnCreateItem.setOnClickListener(v -> {
            if (itemId == 0) {
                viewModel.saveItem(collection.getId());
            } else {
                viewModel.updateItem(itemId);
            }
        });
    }

    private final RecipeListCallback callback = new RecipeListCallback() {
        @Override
        public void onNewIngredientButtonClick() {
            viewModel.addNewItem();
            setupCreateButton(0);
        }

        @Override
        public void onNewInstructionButtonClick() {

        }

        @Override
        public void onToMoveButtonClick() {
            viewModel.openDialog();
        }
    };

//    private final BuyListCallback itemCallback = new BuyListCallback() {
//        @Override
//        public void onItemClick(Item item) {
//            viewModel.btnToMoveShow.set(true);
//        }
//
//        @Override
//        public void onDeleteButtonClick(Item item) {
//            viewModel.deleteItem(item);
//            adapter.closeAllItems();
//        }
//
//        @Override
//        public void onEditButtonClick(Item item) {
//            viewModel.editItem(item);
//            setupCreateButton(item.getId());
//            adapter.closeAllItems();
//        }
//    };

}
