package ru.buylist.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

import ru.buylist.IOnBackPressed;
import ru.buylist.R;
import ru.buylist.fragments.CategoryFragment;
import ru.buylist.fragments.ProductFragment;
import ru.buylist.models.BuyList;
import ru.buylist.models.Product;


public class ProductActivity extends SingleFragmentActivity implements ProductFragment.Callbacks, CategoryFragment.Callbacks {

    private static final String EXTRA_BUY_LIST_ID = "buy_list_id";


    public static Intent newIntent(Context context, UUID productId) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(EXTRA_BUY_LIST_ID, productId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_BUY_LIST_ID);
        return ProductFragment.newInstance(id);
    }


    @Override
    public void onBuyListUpdated(BuyList buyList) {
    }

    @Override
    public void onProductCreated(Product product) {
        Fragment fragment = CategoryFragment.newInstance(product.getProductId());
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

    @Override
    public void updateProductsList(Product product) {
        UUID id = UUID.fromString(product.getBuylistId());
        Fragment fragment = ProductFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
