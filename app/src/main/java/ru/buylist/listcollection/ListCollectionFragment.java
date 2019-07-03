package ru.buylist.listcollection;

import android.content.*;

import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.*;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.*;

import ru.buylist.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.data.BuyList;
import ru.buylist.data.Category;
import ru.buylist.data.Product;
import ru.buylist.data.ProductLab;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class ListCollectionFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_BUY_LIST_ID = "buy_list_id";
    private boolean flag = true;

    private BuyList buyList;

    private Button templatesButton;
    private Button recipeButton;
    private Button shareButton;
    private FloatingActionButton newProductFab;
    private FloatingActionButton visibilityFab;
    private ImageButton createProduct;

    private EditText productField;
    private EditText amountField;
    private EditText unitField;

    private LinearLayout newProductLayout;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    private PackageManager packageManager;

    private Callbacks callbacks;

    public static ListCollectionFragment newInstance(UUID buylistId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUY_LIST_ID, buylistId);

        ListCollectionFragment productFragment = new ListCollectionFragment();
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

//    private void updateList() {
//        ProductLab.get(getActivity()).updateBuyList(buyList);
//        callbacks.onBuyListUpdated(buyList);
//    }

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
        visibilityFab = view.findViewById(R.id.show_purchased_products_fab);

        recyclerView = view.findViewById(R.id.products_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createProduct.setOnClickListener(this);
        newProductFab.setOnClickListener(this);
        visibilityFab.setOnClickListener(this);

        updateProductListUi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_product:
                if (productField.getText().length() != 0) addNewProduct();
                newProductLayout.setVisibility(View.GONE);
                newProductFab.show();
                visibilityFab.show();
                KeyboardUtils.hideKeyboard(productField, getActivity());
                break;
            case R.id.new_product_fab:
                newProductLayout.setVisibility(View.VISIBLE);
                newProductFab.hide();
                visibilityFab.hide();
                KeyboardUtils.showKeyboard(productField, getActivity());
                break;
            case R.id.show_purchased_products_fab:
                if (flag) {
                    visibilityFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off_black));
                    flag = false;
                } else {
                    visibilityFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_black));
                    flag = true;
                }
                updateProductListUi();
                break;
        }
    }

    public void updateProductListUi() {
        ProductLab productLab = ProductLab.get(getActivity());
        List<Product> products = productLab.getProductList(buyList.getId().toString());

        if (adapter == null) {
            adapter = new ProductAdapter(products);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        }
    }

    private void addNewProduct() {
        Product product = new Product();
        product.setBuylistId(buyList.getId().toString());
        product.setName(productField.getText().toString());
        product.setAmount(amountField.getText().toString());
        product.setUnit(unitField.getText().toString());

        if (!isInGlobalDatabase(product)) {
            callbacks.onProductCreated(product);
        }

        ProductLab.get(getActivity()).addProducts(product);
        updateProductListUi();
        clearFields();
    }

    private boolean isInGlobalDatabase(Product product) {
        Product globalProduct = ProductLab.get(getActivity()).getGlobalProduct(product.getName(),
                GlobalProductsTable.Cols.PRODUCT_NAME,
                GlobalProductsTable.NAME);
        if (globalProduct.getName() == null) {
            return false;
        } else {
            product.setCategory(globalProduct.getCategory());
            return true;
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
        List<Product> productList = ProductLab.get(getActivity()).getProductList(buyList.getId().toString());
        String report = buyList.getTitle() + productList;
        return report;
    }

    private String getColor(Product product) {
        Product globalProduct = ProductLab.get(getActivity()).getGlobalProduct(
                product.getName(),
                GlobalProductsTable.Cols.PRODUCT_NAME,
                GlobalProductsTable.NAME);
        if (globalProduct.getCategory() == null) {
            return "#000000";
        } else {
            Category category = ProductLab.get(getActivity()).getCategory(
                    globalProduct.getCategory(),
                    CategoryTable.Cols.CATEGORY_NAME,
                    CategoryTable.NAME);
            return category.getColor();
        }
    }


    public interface Callbacks {
        void onBuyListUpdated(BuyList buyList);

        void onProductCreated(Product product);
    }


    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SwipeLayout swipeLayout;
        private Product product;
        private ImageView category;
        private TextView productName;
        private TextView amount;
        private ImageButton edit;
        private ImageButton delete;
        private FrameLayout frameLayoutTop;
        private CardView cardView;

        ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_product, parent, false));
            swipeLayout = itemView.findViewById(R.id.swipe_layout_product);
            category = itemView.findViewById(R.id.category);
            productName = itemView.findViewById(R.id.product_text);
            amount = itemView.findViewById(R.id.product_amount);
            edit = itemView.findViewById(R.id.edit_product);
            delete = itemView.findViewById(R.id.delete_product);
            frameLayoutTop = itemView.findViewById(R.id.surface_product);
            cardView = itemView.findViewById(R.id.card_product);

            frameLayoutTop.setOnClickListener(this);
            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
        }

        void bind(Product product) {
            this.product = product;
            category.setColorFilter(Color.parseColor(getColor(product)));
            productName.setText(product.getName());
            amount.setText(getString(R.string.amount_of_product, product.getAmount(), product.getUnit()));

            if (product.isPurchased()) {
                productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                if (flag) {
                    hideLayout();
                } else {
                    showLayout();
                }
            } else {
                productName.setPaintFlags(productName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                showLayout();
            }

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_product));
            cardView.setBackgroundColor(0);
        }

        private void hideLayout() {
            itemView.setVisibility(View.INVISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        private void showLayout() {
            itemView.setVisibility(View.VISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        @Override
        public void onClick(View v) {
            ProductLab productLab = ProductLab.get(getActivity());
            switch (v.getId()) {
                case R.id.surface_product:
                    if (product.isPurchased()) {
                        productName.setPaintFlags(productName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        product.setPurchased(false);
                    } else {
                        productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        product.setPurchased(true);
                    }
                    productLab.updateProduct(product);
                    updateProductListUi();
                    break;
                case R.id.delete_product:
                    productLab.deleteFromDb(product.getProductId().toString(),
                            ProductTable.NAME,
                            ProductTable.Cols.PRODUCT_ID);
                    adapter.notifyItemRemoved(getAdapterPosition());
                    adapter.closeAllItems();
                    updateProductListUi();
                    break;
                case R.id.edit_product:
                    newProductLayout.setVisibility(View.VISIBLE);
                    productField.setText(product.getName());
                    amountField.setText(product.getAmount());
                    unitField.setText(product.getUnit());

                    productLab.deleteFromDb(product.getProductId().toString(),
                            ProductTable.NAME,
                            ProductTable.Cols.PRODUCT_ID);
                    adapter.closeAllItems();
                    break;
            }

        }
    }


    private class ProductAdapter extends RecyclerSwipeAdapter<ProductHolder> {
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
        public void onBindViewHolder(@NonNull final ProductHolder holder, int i) {
            Product product = products.get(i);
            holder.bind(product);

            mItemManger.bindView(holder.itemView, i);

            holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    holder.cardView.setBackgroundColor(Color.LTGRAY);
                    mItemManger.closeAllExcept(layout);
                }

                @Override
                public void onOpen(SwipeLayout layout) {
                }

                @Override
                public void onStartClose(SwipeLayout layout) {
                }

                @Override
                public void onClose(SwipeLayout layout) {
                    holder.cardView.setBackgroundColor(Color.WHITE);
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }


        void setProducts(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe_layout_product;
        }
    }

}
