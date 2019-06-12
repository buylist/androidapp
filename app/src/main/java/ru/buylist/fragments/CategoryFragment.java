package ru.buylist.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import java.util.*;

import ru.buylist.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.models.Category;
import ru.buylist.models.Product;
import ru.buylist.models.ProductLab;

import static ru.buylist.data.BuyListDbSchema.*;

public class CategoryFragment extends Fragment implements IOnBackPressed, View.OnClickListener {

    private static final String ARG_CATEGORY = "args_category";
    private Product product;

    private TextView productName;
    private AutoCompleteTextView categoryText;
    private ImageButton spinnerButton;
    private Button buttonNext;
    private Button buttonSkip;

    private Callbacks callbacks;

    public static CategoryFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, id);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(ARG_CATEGORY);
        product = ProductLab.get(getActivity()).getProduct(id.toString(),
                ProductTable.Cols.PRODUCT_ID,
                ProductTable.NAME);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initUi(view);
        return view;
    }

    @Override
    public void onBackPressed() {
        callbacks.updateProductsList(product);
    }

    private void initUi(View view) {
        productName = view.findViewById(R.id.product_name_text);
        categoryText = view.findViewById(R.id.new_category_name);
        spinnerButton = view.findViewById(R.id.category_spinner);
        buttonNext = view.findViewById(R.id.button_next);
        buttonSkip = view.findViewById(R.id.button_skip);

        productName.setText(product.getName());

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
                ProductLab productLab = ProductLab.get(getActivity());
                Category category = productLab.getCategory(
                        categoryText.getText().toString(),
                        CategoryTable.Cols.CATEGORY_NAME,
                        CategoryTable.NAME);

                product.setCategory(category.getName());
                productLab.updateProduct(product);
                productLab.addGlobalProduct(product);
                callbacks.updateProductsList(product);
                break;
            case R.id.button_skip:
                callbacks.updateProductsList(product);
                break;
        }
    }

    private List<Category> getCategories() {
        String[] standardCategories = getResources().getStringArray(R.array.category);
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < standardCategories.length; i++) {
            categories.add(getCategory(i));
            Category baseCategory = ProductLab.get(getActivity()).getCategory(
                    categories.get(i).getName(),
                    CategoryTable.Cols.CATEGORY_NAME,
                    CategoryTable.NAME);
            if (baseCategory.getName() == null) {
                ProductLab.get(getActivity()).addCategory(categories.get(i));
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
        CategoryAdapter adapter = new CategoryAdapter(getActivity(),
                R.layout.spinner_category_row,
                getCategories());
        categoryText.setAdapter(adapter);
    }

    public interface Callbacks {
        void updateProductsList(Product product);
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
            View row = inflater.inflate(R.layout.spinner_category_row, parent, false);
            TextView label = row.findViewById(R.id.category_name);
            ImageView icon = row.findViewById(R.id.category_img);

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
