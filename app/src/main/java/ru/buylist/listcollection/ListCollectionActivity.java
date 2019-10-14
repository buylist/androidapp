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
import android.util.Log;

import ru.buylist.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.SingleFragmentActivity;
import ru.buylist.databinding.ActivityListCollectionBinding;


public class ListCollectionActivity extends SingleFragmentActivity {

    private static final String EXTRA_BUY_LIST_ID = "buy_list_id";

    private ListCollectionViewModel viewModel;

    public static Intent newIntent(Context context, long productId) {
        Intent intent = new Intent(context, ListCollectionActivity.class);
        intent.putExtra(EXTRA_BUY_LIST_ID, productId);
        Log.i("TAG", "Put to collection intent ID: " + productId);
        return intent;
    }

    public static ListCollectionViewModel obtainViewModel(FragmentActivity activity) {
        ListCollectionViewModel viewModel = ViewModelProviders.of(activity).get(ListCollectionViewModel.class);
        return viewModel;
    }

    @Override
    protected Fragment createFragment() {
        long id = getIntent().getLongExtra(EXTRA_BUY_LIST_ID, 0);
        Log.i("TAG", "collection intent get ID: " + id);
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

        viewModel.getNewCategoryEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long productId) {
                createNewProduct(productId);
            }
        });

        viewModel.getAddProductEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long buylistId) {
                updateProductsList(buylistId);
            }
        });

        binding.setViewmodel(viewModel);
    }

    private void createNewProduct(long productId) {
        Fragment fragment = CategoryFragment.newInstance(productId);
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

    public void updateProductsList(long buylistId) {
        Fragment fragment = ListCollectionFragment.newInstance(buylistId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
