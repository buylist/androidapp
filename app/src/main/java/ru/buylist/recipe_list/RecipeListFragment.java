package ru.buylist.recipe_list;

import android.arch.lifecycle.LiveData;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentRecipeListBinding;
import ru.buylist.pattern_list.PatternListAdapter;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

        adapter = new PatternListAdapter(itemCallback);
        binding.recyclerIngredients.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.deleteSelectedCollection();
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
        liveData.observe(this, newCollection -> collection = newCollection);
    }

    // устанавливаем кнопку создания товара
    // для создания нового товара передаем в метод 0
    // для редактирования существующего товара - его индентификатор
    private void setupCreateButton(final long itemId) {
        binding.btnCreateItem.setOnClickListener(v -> {
            viewModel.saveItem(collection.getId(), itemId);
        });
    }

    private void saveSelectedItem(Item selectedItem) {
        if (selectedItems.isEmpty()) {
            selectedItems.add(selectedItem);
            return;
        }

        for (Item item : selectedItems) {
            if (item.getId() == selectedItem.getId()) {
                selectedItems.remove(item);
                return;
            }
        }

        selectedItems.add(selectedItem);
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
            String newInstruction = viewModel.instruction.get();
            binding.textViewInstruction.setText(newInstruction);
            viewModel.fieldInstructionShow.set(false);
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
