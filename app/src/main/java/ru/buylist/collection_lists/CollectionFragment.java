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

import java.util.List;

import ru.buylist.databinding.FragmentCollectionBinding;
import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.utils.SnackbarUtils;

import static ru.buylist.collection_lists.CollectionType.*;
import static ru.buylist.utils.ItemClickCallback.*;

public class CollectionFragment extends Fragment {
    private static final String TAG = "TAG";

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

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupAdapter();
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

        // получаем ViewModel, передаем в макет ее и callback'и
        viewModel = ViewModelProviders.of(this).get(CollectionViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setCallback(collectionCallback);

        // подписываемся на изменения в списках
        subscribeBuyList(viewModel.getCollectionOfList());
        subscribePatternList(viewModel.getCollectionOfPattern());
        subscribeRecipeList(viewModel.getCollectionOfRecipe());
        setupSnackbar();
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
                viewModel.saveToTemporaryStorage(collections, BuyList);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribePatternList(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "Pattern updated.");
                patternAdapter.setLists(collections);
                viewModel.saveToTemporaryStorage(collections, PATTERN);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribeRecipeList(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "Recipe updated.");
                recipeAdapter.setLists(collections);
                viewModel.saveToTemporaryStorage(collections, RECIPE);
            }
            binding.executePendingBindings();
        });
    }

    private void setupSnackbar() {
        viewModel.getSnackbarMessage().observe(this, msg -> {
            if (msg != null) {
                SnackbarUtils.showSnackbar(getView(), getString(msg));
            }
        });
    }

    // устанавливаем кнопку создания
    // для создания новой коллекции передаем в метод 0
    // для обновления существующей коллекции - ее идентификатор
    private void setupCreateButton(final long collectionId) {
        binding.btnCreateBuyList.setOnClickListener(v -> {
            viewModel.saveCollection(collectionId, BuyList);
            Log.i(TAG, "CollectionFragment save BuyList.");
        });

        binding.btnCreatePatternList.setOnClickListener(v -> {
            viewModel.saveCollection(collectionId, PATTERN);
            Log.i(TAG, "CollectionFragment save PatternList.");
        });

        binding.btnCreateRecipeList.setOnClickListener(v -> {
            viewModel.saveCollection(collectionId, RECIPE);
            Log.i(TAG, "CollectionFragment save RecipeList.");
        });
    }

    // закрывает все свайпы
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

    // callback'и кликов по карточкам и по newButton
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
            viewModel.addCollection(BuyList);
            setupCreateButton(0);
            closeAllItems();
        }

        @Override
        public void onNewPatternListButtonClick() {
            viewModel.addCollection(PATTERN);
            setupCreateButton(0);
            closeAllItems();
        }

        @Override
        public void onNewRecipeListButtonClick() {
            viewModel.addCollection(RECIPE);
            setupCreateButton(0);
            closeAllItems();
        }
    };

    // callback'и для адаптеров, возвращают клики по кнопкам в свайпе
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
            setupCreateButton(collection.getId());
            Log.i(TAG, "Edit collection: " + collection.getId());
        }
    };

    // callback'и для activity
    public interface Callbacks {
        void onCollectionSelected(Collection collection);

        void showHome();
    }
}
