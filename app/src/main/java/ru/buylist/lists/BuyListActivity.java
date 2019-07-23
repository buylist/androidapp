package ru.buylist.lists;

import android.content.Intent;
import android.support.v4.app.Fragment;

import ru.buylist.R;


import ru.buylist.SingleFragmentActivity;
import ru.buylist.listcollection.ListCollectionFragment;
import ru.buylist.listcollection.ListCollectionActivity;
import ru.buylist.data.BuyList;
import ru.buylist.data.Product;

public class BuyListActivity extends SingleFragmentActivity implements BuyListFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new BuyListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void setupViewModel() {
        setContentView(R.layout.activity_masterdetail);
    }

    public void onBuyListSelected(BuyList buyList) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ListCollectionActivity.newIntent(this, buyList.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = ListCollectionFragment.newInstance(buyList.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

//    @Override
//    public void onBuyListUpdated(BuyList buyList) {
//        BuyListFragment listFragment = (BuyListFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_container);
//        listFragment.updateUI();
//    }

    @Override
    public void showHome() {
        BuyListFragment fragment = new BuyListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
