package ru.buylist.recipe_list;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentRecipeListBinding;
import ru.buylist.pattern_list.PatternListAdapter;
import ru.buylist.utils.SnackbarUtils;

import static ru.buylist.utils.ItemClickCallback.*;

public class RecipeListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_RECIPE_ID = "recipe_id";

    private Collection collection;
    private List<Item> items;
    private List<Item> selectedItems;

    private RecipeListViewModel viewModel;
    private FragmentRecipeListBinding binding;
    private PatternListAdapter adapter;

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
        selectedItems = new ArrayList<>();

        viewModel = RecipeListActivity.obtainViewModel(getActivity());
        viewModel.bottomShow.set(true);

        long collectionId = getArguments() != null ? getArguments().getLong(ARG_RECIPE_ID, 0) : 0;
        subscribeUi(viewModel.getItems(collectionId));
        subscribeCollection(viewModel.getCollection(collectionId));
        setupSnackbar();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

        adapter = new PatternListAdapter(itemCallback, swipeListener);
        binding.recyclerIngredients.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.deleteSelected();
    }

    // подписка на изменения спика для обновления
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
        liveData.observe(this, newCollection -> {
            collection = newCollection;
            viewModel.loadInstruction(newCollection.getDescription());
        });

    }

    // устанавливаем кнопку создания товара
    // для создания нового товара передаем в метод 0
    // для редактирования существующего товара - его индентификатор
    private void setupCreateButton(final long itemId) {
        binding.btnCreateItem.setOnClickListener(v -> {
            viewModel.saveItem(collection.getId(), itemId);
        });

        // keyboard listener
        binding.fieldUnit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveItem(collection.getId(), itemId);
                return true;
            }
            return false;
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

    // callback'и кликов по кнопкам создания товара, инструкции, сохранение инструкции
    private final RecipeListCallback callback = new RecipeListCallback() {
        @Override
        public void onNewIngredientButtonClick() {
            viewModel.addNewItem();
            setupCreateButton(0);
        }

        @Override
        public void onNewInstructionButtonClick() {
            viewModel.fieldInstructionShow.set(true);
        }

        @Override
        public void onSaveInstructionButtonClick() {
            viewModel.saveInstruction(collection);
        }

        @Override
        public void onToMoveButtonClick() {
            viewModel.transfer(selectedItems);
            selectedItems.clear();
        }
    };

    // callback'и для адаптера, возвращает клики по кнопка свайпа
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
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    };

}
