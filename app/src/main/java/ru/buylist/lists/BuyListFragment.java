package ru.buylist.lists;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.EditText;

import java.util.List;

import ru.buylist.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.data.BuyList;
import ru.buylist.data.db.ProductLab;
import ru.buylist.databinding.FragmentBuyListBinding;

import static ru.buylist.data.db.BuyListDbSchema.BuyTable;
import static ru.buylist.data.db.BuyListDbSchema.ProductTable;
import static ru.buylist.lists.ItemListType.*;

public class BuyListFragment extends Fragment {

    private EditText nameCollection;

    private BuyListAdapter adapter;
    private FragmentBuyListBinding binding;

    private Callbacks callbacks;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_list, container, false);

        updateUI();
        initUi(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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

    private void initUi(View view) {
        nameCollection = view.findViewById(R.id.name_new_list_collection);

        binding.cardListView.setBackgroundColor(0);
        binding.cardPatternView.setBackgroundColor(0);
        binding.cardRecipeView.setBackgroundColor(0);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        updateUI();
        onNewCollectionButtonClick();
        onCreateCollectionButtonClick();
        onCardListViewClick();
    }

    public void updateUI() {
        ProductLab productLab = ProductLab.get(getActivity());
        List<BuyList> lists = productLab.getBuyLists();

        adapter = setupAdapter(adapter, lists, COLLECTION);
        adapter.notifyDataSetChanged();
    }

    private BuyListAdapter setupAdapter(BuyListAdapter adapter, List<BuyList> lists, String type) {
        if (adapter == null) {
            adapter = new BuyListAdapter(lists, buyListClickCallback);
            if (getRV(type) != null) {
                getRV(type).setAdapter(adapter);
            }
            return adapter;
        } else {
            adapter.setLists(lists);
            return adapter;
        }
    }

    private RecyclerView getRV(String type) {
        RecyclerView recyclerView;
        switch (type) {
            case COLLECTION:
                return binding.listCollectionRecyclerView;
            case PATTERN:
                return binding.patternCollectionRecyclerView;
            case RECIPE:
                return binding.recipeCollectionRecyclerView;
        }
        return null;
    }

    private void onNewCollectionButtonClick() {
        binding.newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.setIsLoading(true); // отображение слоя с полями для ввода
                binding.setShow(true);      // отображение recyclerView
                adapter.closeAllItems();
                KeyboardUtils.showKeyboard(nameCollection, getActivity());
            }
        });
    }

    private void onCardListViewClick() {
        binding.cardListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void onCreateCollectionButtonClick() {
        binding.createNewListCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameCollection.getText().length() != 0) {
                    BuyList buyList = new BuyList();
                    buyList.setTitle(nameCollection.getText().toString());
                    buyList.setType(COLLECTION);
                    ProductLab.get(getActivity()).addBuyList(buyList);
                }
                updateUI();
                nameCollection.setText("");
                binding.setIsLoading(false);  // скрытие слоя с полями для ввода
                KeyboardUtils.hideKeyboard(nameCollection, getActivity());

            }
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

    private final BuyListClickCallback buyListClickCallback = new BuyListClickCallback() {
        @Override
        public void onListItemClick(BuyList buyList) {
            callbacks.onBuyListSelected(buyList);
        }

        @Override
        public void onDeleteButtonClick(BuyList buyList) {
            binding.cardListView.setBackgroundColor(0);
            ProductLab productLab = ProductLab.get(getActivity());

            productLab.deleteFromDb(
                    buyList.getId(),
                    BuyTable.NAME,
                    BuyTable.Cols.UUID);
            productLab.deleteFromDb(
                    buyList.getId(),
                    ProductTable.NAME,
                    ProductTable.Cols.BUYLIST_ID);
            adapter.closeAllItems();
            updateUI();
        }

        @Override
        public void onEditButtonClick(BuyList buyList) {
            binding.setIsLoading(true);
            adapter.closeAllItems();

            binding.nameNewListCollection.setText(buyList.getTitle());
            ProductLab productLab = ProductLab.get(getActivity());
            productLab.deleteFromDb(
                    buyList.getId(),
                    BuyTable.NAME,
                    BuyTable.Cols.UUID);
        }
    };

    public interface Callbacks {
        void onBuyListSelected(BuyList buyList);

        void showHome();
    }
}
