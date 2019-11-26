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
import android.widget.ImageView;
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
                        R.layout.item_category_row, newCategories);
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
                viewModel.showHidePrevCirclesButton(isFirstPositionVisible());
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
        int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
        return (pos > 0);
    }



    public class CategoryAdapter extends ArrayAdapter<Category> {
        private List<Category> categories;

        CategoryAdapter(Context context, int textViewResourceId, List<Category> categories) {
            super(context, textViewResourceId, categories);
            this.categories = categories;
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
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_category_row, parent, false);
            TextView label = row.findViewById(R.id.text_view_category_name);
            ImageView icon = row.findViewById(R.id.img_category_circle);
            String color = categories.get(position).getColor();

            label.setText(categories.get(position).getName());

            if (color == null) {
                icon.setColorFilter(Color.parseColor(CategoryInfo.COLOR));
            } else {
                icon.setColorFilter(Color.parseColor(color));
            }

            return row;
        }
    }

}
