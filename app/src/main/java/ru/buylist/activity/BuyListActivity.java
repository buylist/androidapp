package ru.buylist.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import ru.buylist.R;


import ru.buylist.fragments.BuyListFragment;
import ru.buylist.fragments.ProductFragment;
import ru.buylist.models.BuyList;

public class BuyListActivity extends SingleFragmentActivity implements BuyListFragment.Callbacks, ProductFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new BuyListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    public void onProductSelected(BuyList buyList) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = BuyListPagerActivity.newIntent(this, buyList.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = ProductFragment.newInstance(buyList.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onProductUpdated(BuyList buyList) {
        BuyListFragment listFragment = (BuyListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
