package ru.buylist.collection_lists;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.EditText;

import java.util.List;

import ru.buylist.databinding.FragmentCollectionBinding;
import ru.buylist.utils.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.data.entity.Collection;

import static ru.buylist.collection_lists.CollectionType.*;
import static ru.buylist.utils.ItemClickCallback.*;

public class CollectionFragment extends Fragment {
    private static final String TAG = "TAG";

    private EditText nameCollection;

    private CollectionAdapter buyListAdapter;
    private CollectionAdapter patternAdapter;
    private CollectionAdapter recipeAdapter;
    private FragmentCollectionBinding binding;

    private Callbacks callbacks;
    private CollectionViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collection, container, false);

        setupAdapter();
        initUi(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_buy_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CollectionViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setCallback(collectionCallback);
        subscribeBuyList(viewModel.getCollectionOfList());
        subscribePatternList(viewModel.getCollectionOfPattern());
        subscribeRecipeList(viewModel.getCollectionOfRecipe());
    }

    private void initUi(View view) {
        nameCollection = view.findViewById(R.id.field_name_buy_list);

        binding.cardBuyList.setBackgroundColor(0);
        binding.cardPattern.setBackgroundColor(0);
        binding.cardRecipe.setBackgroundColor(0);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        onNewCollectionButtonClick();
        onCreateBuyListButtonClick();
    }

    public void setupAdapter() {
        buyListAdapter = new CollectionAdapter(itemClickCallback);
        binding.recyclerBuyList.setAdapter(buyListAdapter);

        patternAdapter = new CollectionAdapter(itemClickCallback);
        binding.recyclerPattern.setAdapter(patternAdapter);

        recipeAdapter = new CollectionAdapter(itemClickCallback);
        binding.recyclerRecipe.setAdapter(recipeAdapter);

        Log.i(TAG, "Collection adapters created");
    }

    private void subscribeBuyList(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "BuyList updated.");
                buyListAdapter.setLists(collections);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribePatternList(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "Pattern updated.");
                patternAdapter.setLists(collections);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribeRecipeList(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "Recipe updated.");
                recipeAdapter.setLists(collections);
            }
            binding.executePendingBindings();
        });
    }

    private void onNewCollectionButtonClick() {
        binding.btnNewBuyList.setOnClickListener(v -> {
            viewModel.layoutBuyListShow.set(true);
            viewModel.recyclerBuyListShow.set(true);
            KeyboardUtils.showKeyboard(nameCollection, getActivity());
        });

        binding.btnNewPattern.setOnClickListener(v -> {
            viewModel.layoutPatternListShow.set(true);
            viewModel.recyclerPatternListShow.set(true);
            KeyboardUtils.showKeyboard(binding.fieldNamePatternList, getActivity());
        });

        binding.btnNewRecipe.setOnClickListener(v -> {
            viewModel.layoutRecipeListShow.set(true);
            viewModel.recyclerRecipeListShow.set(true);
            KeyboardUtils.showKeyboard(binding.fieldNameRecipeList, getActivity());

        });

        closeAllItems();
    }

    private void onCreateBuyListButtonClick() {
        binding.btnCreateBuyList.setOnClickListener(v -> {
            if (nameCollection.getText().length() != 0) {
                Collection collection = new Collection();
                collection.setTitle(nameCollection.getText().toString());
                collection.setType(BuyList);
                viewModel.addCollection(collection);
                Log.i(TAG, "Collection new name: " + collection.getTitle());
            }

            nameCollection.setText("");
            viewModel.layoutBuyListShow.set(false);
            KeyboardUtils.hideKeyboard(nameCollection, getActivity());

        });

        binding.btnCreatePatternList.setOnClickListener(v -> {
            if (binding.fieldNamePatternList.getText().length() != 0) {
                Collection collection = new Collection();
                collection.setTitle(binding.fieldNamePatternList.getText().toString());
                collection.setType(PATTERN);
                viewModel.addCollection(collection);
            }

            binding.fieldNamePatternList.setText("");
            binding.layoutPatternListFields.setVisibility(View.GONE);
            KeyboardUtils.hideKeyboard(binding.fieldNamePatternList, getActivity());
        });

        binding.btnCreateRecipeList.setOnClickListener(v -> {
            if (binding.fieldNameRecipeList.getText().length() != 0) {
                Collection collection = new Collection();
                collection.setTitle(binding.fieldNameRecipeList.getText().toString());
                collection.setType(RECIPE);
                viewModel.addCollection(collection);
            }

            binding.fieldNameRecipeList.setText("");
            binding.layoutRecipeListFields.setVisibility(View.GONE);
            KeyboardUtils.hideKeyboard(binding.fieldNameRecipeList, getActivity());
        });
    }

    private void closeAllItems() {
        buyListAdapter.closeAllItems();
        patternAdapter.closeAllItems();
        recipeAdapter.closeAllItems();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_home:
                    callbacks.showHome();
                    return true;
                case R.id.action_lists:

                    return true;
                case R.id.action_templates:

                    return true;
                case R.id.action_recipe:

                    return true;
                case R.id.action_settings:

                    return true;
            }
            return false;
        }
    };
    private final CollectionClickCallback collectionCallback = new CollectionClickCallback() {
        @Override
        public void onBuyListCardClick() {
            viewModel.openOrCloseCards(BuyList);
            closeAllItems();
        }

        @Override
        public void onPatternListCardClick() {
            viewModel.openOrCloseCards(PATTERN);
            closeAllItems();
        }

        @Override
        public void onRecipeListCardClick() {
            viewModel.openOrCloseCards(RECIPE);
            closeAllItems();
        }

        @Override
        public void onNewBuyListButtonClick() {

        }

        @Override
        public void onNewPatternListButtonClick() {

        }

        @Override
        public void onNewRecipeListButtonClick() {

        }
    };

    private final ItemCollectionCallback itemClickCallback = new ItemCollectionCallback() {
        @Override
        public void onListItemClick(Collection collection) {
            callbacks.onCollectionSelected(collection);
            Log.i(TAG, "On collection click: " + collection.getId());
        }

        @Override
        public void onDeleteButtonClick(Collection collection) {
            viewModel.deleteCollection(collection);
            closeAllItems();
            Log.i(TAG, "Delete collection: " + collection.getId());
        }

        @Override
        public void onEditButtonClick(Collection collection) {
            closeAllItems();
            viewModel.editCollection(collection);

            Log.i(TAG, "Edit collection: " + collection.getId());
        }
    };

    public interface Callbacks {
        void onCollectionSelected(Collection collection);

        void showHome();
    }
}
