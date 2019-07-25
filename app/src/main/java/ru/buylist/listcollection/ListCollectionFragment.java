package ru.buylist.listcollection;

import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.*;

import ru.buylist.R;
import ru.buylist.data.BuyList;
import ru.buylist.data.Product;
import ru.buylist.databinding.FragmentListCollectionBinding;
import ru.buylist.databinding.ItemProductBinding;


public class ListCollectionFragment extends Fragment {

    private static final String ARG_BUY_LIST_ID = "buy_list_id";

    private BuyList buyList;

    private Button templatesButton;
    private Button recipeButton;

    private EditText productField;

    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    private PackageManager packageManager;

    private ListCollectionViewModel viewModel;


    public static ListCollectionFragment newInstance(UUID buylistId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUY_LIST_ID, buylistId);

        ListCollectionFragment fragment = new ListCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ListCollectionActivity.obtainViewModel(getActivity());
        UUID buylistId = (UUID) getArguments().getSerializable(ARG_BUY_LIST_ID);
        buyList = viewModel.getBuyList(buylistId);
        viewModel.showActivityLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.updateBuyList(buyList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentListCollectionBinding binding = FragmentListCollectionBinding.inflate(inflater, container, false);
        binding.setViewmodel(viewModel);
        initUi(binding.getRoot());
        setupFab();
        return binding.getRoot();
    }

    public void updateUi() {
        List<Product> products = viewModel.getProducts(buyList.getId());

        if (adapter == null) {
            adapter = new ProductAdapter(products);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        }
    }

    private void setupFab() {
        FloatingActionButton newProductFab = getActivity().findViewById(R.id.fab_new_product);
        FloatingActionButton visibilityFab = getActivity().findViewById(R.id.fab_visibility);

        newProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.showNewProductLayout(productField);
                setupCreateButton(null);
            }
        });

        visibilityFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.showAllProducts();
                updateUi();
            }
        });
    }

    private void setupCreateButton(final UUID productId) {
        ImageButton createButton = getActivity().findViewById(R.id.create_product);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.saveProduct(productField, buyList.getId().toString(), productId);
                updateUi();
            }
        });
    }

    private void initUi(View view) {
        getActivity().setTitle(buyList.getTitle());

        templatesButton = view.findViewById(R.id.templates_button);
        recipeButton = view.findViewById(R.id.recipe_button);
        productField = view.findViewById(R.id.product_field);

        recyclerView = view.findViewById(R.id.products_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUi();
    }


    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Product product;
        private ItemProductBinding binding;

        ProductHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.surfaceProduct.setOnClickListener(this);
            binding.deleteProduct.setOnClickListener(this);
            binding.editProduct.setOnClickListener(this);
        }

        void bind(Product product) {
            this.product = product;
            binding.setProduct(product);
            binding.executePendingBindings();

            binding.category.setColorFilter(viewModel.getColor(product));

            viewModel.setViewVisibility(itemView, product);

            binding.swipeLayoutProduct.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.swipeLayoutProduct.addDrag(SwipeLayout.DragEdge.Right, binding.bottomProduct);

            binding.cardProduct.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.surface_product:
                    viewModel.checkProduct(product);
                    updateUi();
                    break;
                case R.id.delete_product:
                    viewModel.makeAction(viewModel.DELETE, product);
                    adapter.notifyItemRemoved(getAdapterPosition());
                    adapter.closeAllItems();
                    updateUi();
                    break;
                case R.id.edit_product:
                    viewModel.editProduct(product);
                    setupCreateButton(product.getProductId());
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
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            ItemProductBinding binding = ItemProductBinding.inflate(inflater, viewGroup, false);
            return new ProductHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductHolder holder, int i) {
            Product product = products.get(i);
            holder.bind(product);

            mItemManger.bindView(holder.itemView, i);

            holder.binding.swipeLayoutProduct.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    holder.binding.cardProduct.setBackgroundColor(Color.LTGRAY);
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
                    holder.binding.cardProduct.setBackgroundColor(0);
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
