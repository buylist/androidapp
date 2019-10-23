package ru.buylist.collection_lists;

import android.content.Intent;
import android.support.v4.app.Fragment;

import ru.buylist.R;


import ru.buylist.utils.SingleFragmentActivity;
import ru.buylist.data.entity.Collection;
import ru.buylist.buy_list.BuyListFragment;
import ru.buylist.buy_list.BuyListActivity;

public class CollectionActivity extends SingleFragmentActivity implements CollectionFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CollectionFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void setupViewModel() {
        setContentView(R.layout.activity_masterdetail);
    }

    public void onCollectionSelected(Collection collection) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = BuyListActivity.newIntent(this, collection.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = BuyListFragment.newInstance(collection.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

//    @Override
//    public void onBuyListUpdated(BuyList buyList) {
//        CollectionFragment listFragment = (CollectionFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_container);
//        listFragment.updateUI();
//    }

    @Override
    public void showHome() {
        CollectionFragment fragment = new CollectionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
