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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.*;

import ru.buylist.KeyboardUtils;
import ru.buylist.R;
import ru.buylist.models.BuyList;
import ru.buylist.models.Product;
import ru.buylist.models.ProductLab;

import static ru.buylist.data.BuyListDbSchema.*;

public class ProductFragment extends Fragment {

    private static final String ARG_BUY_LIST_ID = "buy_list_id";
    private static final int height = 40;
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


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_product, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.buylist_edit:
//                return true;
//            default:
//                updateList();
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    private void updateList() {
//        ProductLab.get(getActivity()).updateBuyList(buyList);
//        callbacks.onProductUpdated(buyList);
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

        onNewProductButtonClick();
        updateProductListUi();
        onCreateButtonClick();
        onShowProductsButtonClick();
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
                KeyboardUtils.showKeyboard(productField, getActivity());
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
                KeyboardUtils.hideKeyboard(productField, getActivity());
            }
        });
    }

    private void onShowProductsButtonClick() {
        visibilityFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    visibilityFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off_black));
                    updateProductListUi();
                    flag = false;
                } else {
                    visibilityFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_black));
                    updateProductListUi();
                    flag = true;
                }
            }
        });

    }

    private void addNewProduct() {
        Product product = new Product();
        product.setBuylistId(buyList.getId() + buyList.getTitle());
        product.setName(productField.getText().toString());
        product.setAmount(amountField.getText().toString());
        product.setUnit(unitField.getText().toString());

        if (!isInGlobalDatabase(product)) {
            ProductLab.get(getActivity()).addGlobalProduct(product);
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
        List<Product> productList = ProductLab.get(getActivity()).getProductList(buyList.getId() + buyList.getTitle());
        String report = buyList.getTitle() + productList;
        return report;
    }

    public interface Callbacks {
        void onProductUpdated(BuyList buyList);

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
        private FrameLayout frameLayoutBottom;
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
            frameLayoutTop.setOnClickListener(this);
            onDeleteButtonClick();
            onEditButtonClick();

            frameLayoutBottom = itemView.findViewById(R.id.bottom_product);
            cardView = itemView.findViewById(R.id.card_product);
        }

        void bind(Product product) {
            this.product = product;
            productName.setText(product.getName());
            amount.setText(getString(R.string.amount_of_product, product.getAmount(), product.getUnit()));

            if (product.isPurchased()) {
                productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                if (flag) {
                    hideLayout();
                } else {
                    showLayout();
                }
            }

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_product));
            cardView.setBackgroundColor(0);
        }

        private void hideLayout() {
            itemView.setVisibility(View.GONE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        private void showLayout() {
            itemView.setVisibility(View.VISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            updateProductListUi();
        }

        private void onDeleteButtonClick() {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductLab productLab = ProductLab.get(getActivity());
                    productLab.deleteFromDb(product.getProductId().toString(),
                            ProductTable.NAME,
                            ProductTable.Cols.PRODUCT_ID);
                    adapter.notifyItemRemoved(getAdapterPosition());
                    adapter.closeAllItems();
                    updateProductListUi();
                }
            });
        }

        private void onEditButtonClick() {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newProductLayout.setVisibility(View.VISIBLE);
                    productField.setText(product.getName());
                    amountField.setText(product.getAmount());
                    unitField.setText(product.getUnit());

                    ProductLab productLab = ProductLab.get(getActivity());
                    productLab.deleteFromDb(product.getProductId().toString(),
                            ProductTable.NAME,
                            ProductTable.Cols.PRODUCT_ID);
                    adapter.closeAllItems();
                }
            });
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
                    holder.cardView.setBackgroundColor(Color.WHITE);
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
                    holder.cardView.setBackgroundColor(0);
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
