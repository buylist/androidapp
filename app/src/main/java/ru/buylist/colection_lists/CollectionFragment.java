package ru.buylist.colection_lists;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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

import static ru.buylist.colection_lists.CollectionType.*;

public class CollectionFragment extends Fragment {
    private static final String TAG = "TAG";

    private EditText nameCollection;

    private CollectionAdapter adapter;
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
        subscribeUi(viewModel.getCollectionOfList());
    }

    private void initUi(View view) {
        nameCollection = view.findViewById(R.id.field_name_buy_list);

        binding.cardBuyList.setBackgroundColor(0);
        binding.cardPattern.setBackgroundColor(0);
        binding.cardRecipe.setBackgroundColor(0);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        onNewBuyListButtonClick();
        onCreateBuyListButtonClick();
        onCardListViewClick();
    }

    public void setupAdapter() {
        adapter = new CollectionAdapter(collectionClickCallback);
        binding.recyclerBuyList.setAdapter(adapter);
        Log.i(TAG, "Buylist adapter created");
    }

    private void subscribeUi(LiveData<List<Collection>> liveData) {
        liveData.observe(this, collections -> {
            if (collections != null) {
                Log.i(TAG, "Collection livedata new size: " + collections.size());
                adapter.setLists(collections);
            }
            binding.executePendingBindings();
        });
    }

    private void onNewBuyListButtonClick() {
        binding.btnNewBuyList.setOnClickListener(v -> {
            binding.setIsLoading(true); // отображение слоя с полями для ввода
            binding.setShow(true);      // отображение recyclerView
            adapter.closeAllItems();
            KeyboardUtils.showKeyboard(nameCollection, getActivity());
        });
    }

    private void onCardListViewClick() {
        binding.cardBuyList.setOnClickListener(v -> {
            adapter.closeAllItems();
            nameCollection.setText("");
            binding.setIsLoading(false);  // скрытие слоя с полями для ввода
            KeyboardUtils.hideKeyboard(nameCollection, getActivity());

            // скрытие / отображение элементов списка
            if (!binding.getShow()) {
                binding.setShow(true);
            } else {
                binding.setShow(false);
            }
        });
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
            binding.setIsLoading(false);  // скрытие слоя с полями для ввода
            KeyboardUtils.hideKeyboard(nameCollection, getActivity());

        });
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

    private final CollectionClickCallback collectionClickCallback = new CollectionClickCallback() {
        @Override
        public void onListItemClick(Collection collection) {
            callbacks.onCollectionSelected(collection);
            Log.i(TAG, "On collection click: " + collection.getId());
        }

        @Override
        public void onDeleteButtonClick(Collection collection) {
            binding.cardBuyList.setBackgroundColor(0);
            viewModel.deleteCollection(collection);
            adapter.closeAllItems();
            Log.i(TAG, "Delete collection: " + collection.getId());
        }

        @Override
        public void onEditButtonClick(Collection collection) {
            binding.setIsLoading(true);
            adapter.closeAllItems();

            binding.fieldNameBuyList.setText(collection.getTitle());
            Log.i(TAG, "Edit collection: " + collection.getId());
        }
    };

    public interface Callbacks {
        void onCollectionSelected(Collection collection);

        void showHome();
    }
}
