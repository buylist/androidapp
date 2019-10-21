package ru.buylist.buy_list;

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

import ru.buylist.databinding.ActivityBuyListBinding;
import ru.buylist.utils.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.utils.SingleFragmentActivity;


public class BuyListActivity extends SingleFragmentActivity {
    private static final String TAG = "TAG";

    private static final String EXTRA_COLLECTION_ID = "buy_list_id";

    private BuyListViewModel viewModel;

    public static Intent newIntent(Context context, long collectionId) {
        Intent intent = new Intent(context, BuyListActivity.class);
        intent.putExtra(EXTRA_COLLECTION_ID, collectionId);
        Log.i("TAG", "Put to collection intent ID: " + collectionId);
        return intent;
    }

    public static BuyListViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(BuyListViewModel.class);
    }

    @Override
    protected Fragment createFragment() {
        long collectionId = getIntent().getLongExtra(EXTRA_COLLECTION_ID, 0);
        Log.i("TAG", "collection intent get ID: " + collectionId);
        return BuyListFragment.newInstance(collectionId);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_buy_list;
    }

    @Override
    protected void setupViewModel() {
        ActivityBuyListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_list);
        viewModel = obtainViewModel(this);

        viewModel.getNewCategoryEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long itemId) {
                Log.i(TAG, "ShoppingActivity: new category event");
                createNewItem(itemId);
            }
        });

        viewModel.getAddProductEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long collectionId) {
                Log.i(TAG, "ShoppingActivity: update product list event");
                updateProductsList(collectionId);
            }
        });

        binding.setViewmodel(viewModel);
    }

    private void createNewItem(long itemId) {
        Fragment fragment = CategoryFragment.newInstance(itemId);
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

    public void updateProductsList(long collectionId) {
        Fragment fragment = BuyListFragment.newInstance(collectionId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
