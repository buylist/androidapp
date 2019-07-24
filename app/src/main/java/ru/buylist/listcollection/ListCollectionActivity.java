package ru.buylist.listcollection;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

import ru.buylist.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.SingleFragmentActivity;
import ru.buylist.data.Product;
import ru.buylist.databinding.ActivityListCollectionBinding;


public class ListCollectionActivity extends SingleFragmentActivity {

    private static final String EXTRA_BUY_LIST_ID = "buy_list_id";

    private ListCollectionViewModel viewModel;

    public static Intent newIntent(Context context, UUID productId) {
        Intent intent = new Intent(context, ListCollectionActivity.class);
        intent.putExtra(EXTRA_BUY_LIST_ID, productId);
        return intent;
    }

    public static ListCollectionViewModel obtainViewModel(FragmentActivity activity) {
        ListCollectionViewModel viewModel = ViewModelProviders.of(activity).get(ListCollectionViewModel.class);
        return viewModel;
    }

    @Override
    protected Fragment createFragment() {
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_BUY_LIST_ID);
        return ListCollectionFragment.newInstance(id);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_list_collection;
    }

    @Override
    protected void setupViewModel() {
        ActivityListCollectionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_list_collection);
        viewModel = obtainViewModel(this);

        viewModel.getNewCategoryEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String productId) {
                createNewProduct(productId);
            }
        });

        viewModel.getAddProductEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String buylistId) {
                updateProductsList(buylistId);
            }
        });

        binding.setViewmodel(viewModel);
    }

    private void createNewProduct(String productId) {
        Fragment fragment = CategoryFragment.newInstance(UUID.fromString(productId));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        IOnBackPressed listener = null;
        for (Fragment fragment : fm.getFragments()) {
            if (fragment instanceof IOnBackPressed) {
                listener = (IOnBackPressed) fragment;
                break;
            }
        }

        if (listener != null) {
            listener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void updateProductsList(String buylistId) {
        UUID id = UUID.fromString(buylistId);
        Fragment fragment = ListCollectionFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
