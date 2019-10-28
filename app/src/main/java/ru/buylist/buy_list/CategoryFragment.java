package ru.buylist.buy_list;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Category;
import ru.buylist.data.entity.Item;
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

    private BuyListViewModel viewModel;

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
        viewModel.hideActivityLayout();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initUi(view);
        return view;
    }

    private void initUi(View view) {
        productName = view.findViewById(R.id.product_name_text);
        categoryText = view.findViewById(R.id.new_category_name);
        spinnerButton = view.findViewById(R.id.category_spinner);
        buttonNext = view.findViewById(R.id.button_next);
        buttonSkip = view.findViewById(R.id.button_skip);

        productName.setText(item.getName());

        setAdapter();

        spinnerButton.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
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
                    SnackbarUtils.showSnackbar(v, getString(R.string.category_is_empty));
                    break;
                }
                viewModel.updateCategory(categoryText.getText().toString(), item, type);
                break;
            case R.id.button_skip:
                viewModel.skipCategory(item, type);
                break;
        }
    }

    private List<Category> getCategories() {
        String[] standardCategories = getResources().getStringArray(R.array.category);
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < standardCategories.length; i++) {
            categories.add(getCategory(i));
            Category baseCategory = viewModel.getCategory(categories.get(i).getName());
            if (baseCategory == null || baseCategory.getName() == null) {
                viewModel.addCategory(categories.get(i));
            }
        }
        return categories;
    }

    private Category getCategory(int i) {
        String[] standardCategories = getResources().getStringArray(R.array.category);
        String[] standardColor = getResources().getStringArray(R.array.category_color);
        Category category = new Category();
        category.setName(standardCategories[i]);
        category.setColor(standardColor[i]);
        return category;
    }

    private void setAdapter() {
        List<Category> categories = getCategories();
        CategoryAdapter adapter = new CategoryAdapter(getActivity(),
                R.layout.item_category_row,
                categories);
        categoryText.setAdapter(adapter);
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

        View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_category_row, parent, false);
            TextView label = row.findViewById(R.id.text_view_category_name);
            ImageView icon = row.findViewById(R.id.img_category_circle);

            label.setText(categories.get(position).getName());

            switch (categories.get(position).getName()) {
                case "Продукты":
                    icon.setColorFilter(Color.parseColor("#56CCF2"));
                    return row;
                case "Товары для дома":
                    icon.setColorFilter(Color.parseColor("#4CB5AB"));
                    return row;
                case "Красота и здоровье":
                    icon.setColorFilter(Color.parseColor("#EB5757"));
                    return row;
                case "Детям и мамам":
                    icon.setColorFilter(Color.parseColor("#F2994A"));
                    return row;
                case "Авто Мото":
                    icon.setColorFilter(Color.parseColor("#F2E148"));
                    return row;
                case "Зоотовары":
                    icon.setColorFilter(Color.parseColor("#D766FF"));
                    return row;
                default:
                    icon.setColorFilter(Color.BLACK);
                    return row;
            }

        }
    }

}
