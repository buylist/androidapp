package ru.buylist.buy_list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import ru.buylist.collection_lists.CollectionType;
import ru.buylist.databinding.ActivityBuyListBinding;
import ru.buylist.utils.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.utils.SingleFragmentActivity;


public class BuyListActivity extends SingleFragmentActivity {
    private static final String TAG = "TAG";

    private static final String EXTRA_COLLECTION_ID = "buy_list_id";
    private static final String EXTRA_COLLECTION_TITLE = "buy_list_title";

    private BuyListViewModel viewModel;

    public static Intent newIntent(Context context, long collectionId, String collectioTitle) {
        Intent intent = new Intent(context, BuyListActivity.class);
        intent.putExtra(EXTRA_COLLECTION_ID, collectionId);
        intent.putExtra(EXTRA_COLLECTION_TITLE, collectioTitle);
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
        binding.setViewmodel(viewModel);

        // открытие CategoryFragment
        viewModel.getNewCategoryEvent().observe(this, itemId -> {
            Log.i(TAG, "ShoppingActivity: new category event");
            setCategory(itemId);
        });

        // Возврат к BuyList
        viewModel.getReturnToListEvent().observe(this, collectionId -> {
            Log.i(TAG, "ShoppingActivity: update product list event");
            returnToBuyList(collectionId);
        });

        // открытие диалогового окна "по шаблонам"
        viewModel.getPatternDialogEvent().observe(this, collectionId ->
                BuyListDialog.newInstance(CollectionType.PATTERN, collectionId)
                        .show(BuyListActivity.this.getSupportFragmentManager(), "dialog"));

        // открытие диалогового окна "по рецептам"
        viewModel.getRecipeDialogEvent().observe(this, collectionId ->
                BuyListDialog.newInstance(CollectionType.RECIPE, collectionId)
                        .show(BuyListActivity.this.getSupportFragmentManager(), "dialog"));

        setTitle(getIntent().getStringExtra(EXTRA_COLLECTION_TITLE));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setCategory(long itemId) {
        Fragment fragment = CategoryFragment.newInstance(itemId, CollectionType.BuyList);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void returnToBuyList(long collectionId) {
        Fragment fragment = BuyListFragment.newInstance(collectionId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
