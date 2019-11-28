package ru.buylist.buy_list;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Category;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.FragmentCategoryBinding;
import ru.buylist.databinding.ItemCategoryRowBinding;
import ru.buylist.pattern_list.PatternListActivity;
import ru.buylist.pattern_list.PatternListViewModel;
import ru.buylist.utils.SnackbarUtils;

public class CategoryFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private static final String ARG_CATEGORY = "args_category";
    private static final String ARG_TYPE = "args_type";
    private String type;
    private Item item;

    private TextView productName;
    private AutoCompleteTextView categoryText;
    private ImageButton spinnerButton;
    private Button buttonNext;
    private Button buttonSkip;

    private FragmentCategoryBinding binding;
    private BuyListViewModel viewModel;
    private CategoryAdapter adapter;
    private CirclesAdapter circlesAdapter;

    public static CategoryFragment newInstance(long itemId, String type) {
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY, itemId);
        args.putString(ARG_TYPE, type);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        Log.i(TAG, "CategoryFragment set ars: " + args.getLong(ARG_CATEGORY));
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = BuyListActivity.obtainViewModel(getActivity());
        long itemId = getArguments().getLong(ARG_CATEGORY);
        type = getArguments().getString(ARG_TYPE);
        Log.i(TAG, "CategoryFragment get args: " + itemId + " " + type);
        do {
            item = viewModel.getItem(itemId);
        } while (item == null);


        // скрытие кнопки fab и bottomNavigation
        PatternListViewModel patternViewmodel = PatternListActivity.obtainViewModel(getActivity());
        patternViewmodel.bottomShow.set(false);
        patternViewmodel.fabShow.set(false);
        viewModel.hideActivityLayout();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);
        binding.setViewmodel(viewModel);

        initUi(binding.getRoot());
        return binding.getRoot();
    }

    private void initUi(View view) {
        productName = view.findViewById(R.id.product_name_text);
        categoryText = view.findViewById(R.id.new_category_name);
        spinnerButton = view.findViewById(R.id.category_spinner);
        buttonNext = view.findViewById(R.id.button_next);
        buttonSkip = view.findViewById(R.id.button_skip);

        productName.setText(item.getName());

        subscribeCategories();

        spinnerButton.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);

        setupSnackbar();
        setupCircleAdapter();
        onPrevNextButtonClick();
    }

    private void setupSnackbar() {
        viewModel.getSnackbarMessage().observe(this, msg -> {
            if (msg != null) {
                SnackbarUtils.showSnackbar(getView(), getString(msg));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_spinner:
                categoryText.showDropDown();
                break;
            case R.id.button_next:
                if (categoryText.getText().toString().isEmpty()) {
                    // категория не выбрана, отображаем snackbar
                    viewModel.setSnackbarText(R.string.category_is_empty);
                    break;
                }
                viewModel.updateCategory(categoryText.getText().toString(), circlesAdapter.getColor(), item);
                break;
            case R.id.button_skip:
                viewModel.skipCategory(item);
                break;
        }
    }

    private void subscribeCategories() {
        viewModel.getLiveCategories().observe(this, newCategories -> {
            if (adapter == null) {
                adapter = new CategoryAdapter(getActivity(),
                        R.layout.item_category_row, newCategories, rowCallback);
            } else {
                adapter.setList(newCategories);
            }
            categoryText.setAdapter(adapter);
        });
    }

    private void setupCircleAdapter() {
        if (circlesAdapter == null) {
            circlesAdapter = new CirclesAdapter(getColors());
        } else {
            circlesAdapter.setColors(getColors());
        }
        binding.recyclerCircles.setAdapter(circlesAdapter);

        binding.recyclerCircles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.showHideCirclesButton(isFirstPositionVisible(), isLastPositionVisible());
            }
        });
    }

    private List<Category> getColors() {
        String[] standardColors = getResources().getStringArray(R.array.category_color);
        List<Category> colors = new ArrayList<>();
        for (String standardColor : standardColors) {
            Category color = new Category();
            color.setColor(standardColor);
            color.setSelected(false);
            colors.add(color);
        }
        return colors;
    }

    private boolean isFirstPositionVisible() {
        LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
        int pos = layoutManager.findFirstVisibleItemPosition();
        return (pos > 0);
    }

    private boolean isLastPositionVisible() {
        LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
        int position = layoutManager.findLastVisibleItemPosition();
        return (position >= circlesAdapter.getItemCount() - 1);
    }

    private void onPrevNextButtonClick() {
        LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
        int totalItemCount = circlesAdapter.getItemCount();

        binding.btnPrevCircles.setOnClickListener(v -> {
            int firstVisibleItemIndex = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemIndex > 0) {
                layoutManager.smoothScrollToPosition(binding.recyclerCircles, null, firstVisibleItemIndex-1);
            }
        });

        binding.btnNextCircles.setOnClickListener(v -> {
            if (totalItemCount <= 0) return;

            int lastVisibleItemIndex = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemIndex >= totalItemCount) return;

            layoutManager.smoothScrollToPosition(binding.recyclerCircles, null, lastVisibleItemIndex+1);
        });
    }

    private void selectCircle(String selectedColor) {
        List<Category> categories = getColors();
        for (Category category : categories) {
            if (category.getColor().equals(selectedColor)) {
                category.setSelected(true);
                circlesAdapter.setColors(categories);
                return;
            }
        }
    }

    private final CategoryCallback.CategoryRowCallback rowCallback = new CategoryCallback.CategoryRowCallback() {
        @Override
        public void onRowClick(Category category) {
            selectCircle(category.getColor());
            categoryText.setText(category.getName());
            categoryText.dismissDropDown();
        }
    };



    public class CategoryAdapter extends ArrayAdapter<Category> {
        private List<Category> categories;
        private CategoryCallback.CategoryRowCallback callback;

        CategoryAdapter(Context context, int layoutId, List<Category> categories, CategoryCallback.CategoryRowCallback callback) {
            super(context, layoutId, categories);
            this.categories = categories;
            this.callback = callback;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private void setList(List<Category> categories) {
            this.categories = categories;
            notifyDataSetChanged();
        }

        View getCustomView(int position, View convertView, ViewGroup parent) {
            ItemCategoryRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_category_row, parent, false);

            binding.setCategory(categories.get(position));
            binding.setCallback(callback);

            String color = categories.get(position).getColor();

            if (color == null) {
                binding.imgCategoryCircle.setColorFilter(Color.parseColor(CategoryInfo.COLOR));
            } else {
                binding.imgCategoryCircle.setColorFilter(Color.parseColor(color));
            }

            return binding.getRoot();
        }
    }

}
