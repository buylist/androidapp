package ru.buylist.fragments;

import android.content.*;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.*;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.*;

import ru.buylist.R;
import ru.buylist.data.BuyListDbSchema;
import ru.buylist.models.BuyList;
import ru.buylist.models.Product;
import ru.buylist.models.ProductLab;
import ru.buylist.swipe_helper.ITouchHelperAdapter;
import ru.buylist.swipe_helper.ITouchHelperHolder;
import ru.buylist.swipe_helper.TouchHelperCallback;

public class ProductFragment extends Fragment {

    private static final String ARG_BUY_LIST_ID = "buy_list_id";

    private BuyList buyList;

    private Button templatesButton;
    private Button recipeButton;
    private Button shareButton;
    private FloatingActionButton newProductFab;
    private ImageButton createProduct;

    private EditText productField;
    private EditText amountField;
    private EditText unitField;

    private LinearLayout newProductLayout;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

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
        View view = inflater.inflate(R.layout.fragment_products, container, false);
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

        templatesButton = view.findViewById(R.id.templates_button);
        recipeButton = view.findViewById(R.id.recipe_button);
        productField = view.findViewById(R.id.product_field);
        amountField = view.findViewById(R.id.amount_field);
        unitField = view.findViewById(R.id.unit_field);
        createProduct = view.findViewById(R.id.create_product);
        newProductLayout = view.findViewById(R.id.new_product);
        newProductFab = view.findViewById(R.id.new_product_fab);

        recyclerView = view.findViewById(R.id.products_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        onNewProductButtonClick();
        updateProductListUi();
        onCreateButtonClick();

        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
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

    private void onNewProductButtonClick() {
        newProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProductLayout.setVisibility(View.VISIBLE);
                newProductFab.hide();
                showKeyboard(true);
            }
        });
    }

    private void onCreateButtonClick() {
        createProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productField.getText().length() != 0) {
                    addNewProduct();
                }
                newProductLayout.setVisibility(View.GONE);
                newProductFab.show();
                showKeyboard(false);
            }
        });

    }

    private void addNewProduct() {
        if (productField != null) {
            Product product = new Product();
            product.setBuylistId(buyList.getId() + buyList.getTitle());
            product.setName(productField.getText().toString());
            product.setAmount(amountField.getText().toString());
            product.setUnit(unitField.getText().toString());
            ProductLab.get(getActivity()).addProducts(product);
            updateProductListUi();
        }
        clearFields();
    }

    private void showKeyboard(boolean flag) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (flag) {
            productField.requestFocus();
            imm.showSoftInput(productField, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(productField.getWindowToken(), 0);
        }
    }

    private void clearFields() {
        productField.setText("");
        amountField.setText("");
        unitField.setText("");
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

    private void updateProductTable(final List<Product> products) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ProductLab productLab = ProductLab.get(getActivity());
                productLab.updateProductTable(products);
            }
        }).start();
    }


    public interface Callbacks {
        void onProductUpdated(BuyList buyList);
    }


    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ITouchHelperHolder {
        private Product product;
        private ImageView category;
        private TextView productName;
        private TextView amount;
        private ImageButton edit;
        private ImageButton delete;

        ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_product, parent, false));
            category = itemView.findViewById(R.id.circle);
            productName = itemView.findViewById(R.id.product_text);
            amount = itemView.findViewById(R.id.product_amount);
            edit = itemView.findViewById(R.id.edit_product);
            delete = itemView.findViewById(R.id.delete_product);

            itemView.setOnClickListener(this);
        }

        void bind(Product product) {
            this.product = product;
            productName.setText(product.getName());
            amount.setText(product.getAmount() + " " + product.getUnit());

            if (product.isPurchased()) productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        @Override
        public void onClick(View v) {
            if (product.isPurchased()) {
                productName.setPaintFlags(productName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                product.setPurchased(false);
            } else {
                productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                product.setPurchased(true);
            }
            ProductLab.get(getActivity()).updateProduct(product);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> implements ITouchHelperAdapter {
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

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            Collections.swap(products, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);

            updateProductTable(products);
        }

        @Override
        public void onItemDismiss(int position) {
            ProductLab productLab = ProductLab.get(getActivity());
            productLab.deleteFromDb(products.get(position).getName(),
                    BuyListDbSchema.ProductTable.NAME,
                    BuyListDbSchema.ProductTable.Cols.PRODUCT_NAME);
            notifyItemRemoved(position);
            updateProductListUi();
        }
    }

}
