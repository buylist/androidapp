package ru.buylist.fragments;

import android.content.*;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import ru.buylist.R;
import ru.buylist.models.BuyList;
import ru.buylist.models.Product;
import ru.buylist.models.ProductLab;

import static ru.buylist.data.BuyListDbSchema.*;

public class ProductFragment extends Fragment {

    private static final String ARG_BUY_LIST_ID = "buy_list_id";

    private BuyList buyList;
    private EditText titleField;
    private Button patternButton;
    private Button recipeButton;
    private EditText productField;
    private ImageButton addButton;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    private Button shareButton;

    private PackageManager packageManager;

    private Callbacks callbacks;

    public static ProductFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUY_LIST_ID, productId);

        ProductFragment productFragment = new ProductFragment();
        productFragment.setArguments(args);
        return productFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID buylistId = (UUID) getArguments().getSerializable(ARG_BUY_LIST_ID);
        buyList = ProductLab.get(getActivity()).getBuyList(buylistId);
    }

    @Override
    public void onPause() {
        super.onPause();
        ProductLab.get(getActivity()).updateBuyList(buyList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        initUi(view);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_product, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buylist_edit:
                return true;
            default:
                updateList();
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList() {
        ProductLab.get(getActivity()).updateBuyList(buyList);
        callbacks.onProductUpdated(buyList);
    }

    private void initUi(View view) {
        getActivity().setTitle(buyList.getTitle());
        titleField = view.findViewById(R.id.list_title);
        titleField.setText(buyList.getTitle());
        onTitleFieldChangedListener();

        patternButton = view.findViewById(R.id.pattern_button);
        recipeButton = view.findViewById(R.id.recipe_button);
        productField = view.findViewById(R.id.product_name);

        addButton = view.findViewById(R.id.add_product);
        onAddButtonListener();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateProductListUi();
    }

    public void updateProductListUi() {
        ProductLab productLab = ProductLab.get(getActivity());
        List<Product> products = productLab.getProductList(buyList.getId() + buyList.getTitle());

        if (adapter == null) {
            adapter = new ProductAdapter(products);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        }
    }

    private void onTitleFieldChangedListener() {
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyList.setTitle(s.toString());
                updateList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onAddButtonListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = new Product();
                product.setBuylistId(buyList.getId() + buyList.getTitle());
                product.setName(productField.getText().toString());
                ProductLab.get(getActivity()).addProducts(product);
                updateProductListUi();
                productField.setText("");
            }
        });
    }

    private void onShareButtonClick() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = IntentBuilder.from(getActivity())
                        .setChooserTitle(getString(R.string.send_report))
                        .setType("text/plain")
                        .setSubject(getString(R.string.buylist_report_subject))
                        .setText(getBuyListReport())
                        .getIntent();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private String getBuyListReport() {
        List<Product> productList = ProductLab.get(getActivity()).getProductList(buyList.getId() + buyList.getTitle());
        String report = buyList.getTitle() + productList;
        return report;
    }

    public interface Callbacks {
        void onProductUpdated(BuyList buyList);
    }


    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Product product;
        private CheckBox isPurchased;
        private TextView productName;

        ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_product, parent, false));
            isPurchased = itemView.findViewById(R.id.is_purchased);
            productName = itemView.findViewById(R.id.product_text);

            itemView.setOnClickListener(this);
        }

        void bind(Product product) {
            this.product = product;
            productName.setText(product.getName());
            isPurchased.setChecked(product.isPurchased());
        }

        @Override
        public void onClick(View v) {
//            callbacks.onProductSelected(product);
        }

    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<Product> products;

        ProductAdapter(List<Product> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ProductHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, int i) {
            Product product = products.get(i);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }


        void setProducts(List<Product> products) {
            this.products = products;
        }

    }
}
