package ru.buylist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import ru.buylist.R;
import ru.buylist.fragments.ProductFragment;
import ru.buylist.models.BuyList;
import ru.buylist.models.ProductLab;

public class BuyListPagerActivity extends AppCompatActivity implements ProductFragment.Callbacks {

    private static final String EXTRA_BUY_LIST_ID = "buy_list_id";

    private ViewPager viewPager;
    private List<BuyList> lists;

    public static Intent newIntent(Context context, UUID productId) {
        Intent intent = new Intent(context, BuyListPagerActivity.class);
        intent.putExtra(EXTRA_BUY_LIST_ID, productId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_list_pager);

        initUI();
    }

    private void initUI() {
        viewPager = findViewById(R.id.buy_list_view_pager);
        setPagerAdapter();
    }

    private void setPagerAdapter() {
        UUID productId = (UUID) getIntent().getSerializableExtra(EXTRA_BUY_LIST_ID);
        lists = ProductLab.get(this).getBuyLists();
        FragmentManager fm = getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                BuyList buyList = lists.get(position);
                return ProductFragment.newInstance(buyList.getId());
            }

            @Override
            public int getCount() {
                return lists.size();
            }
        });

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(productId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }


    @Override
    public void onProductUpdated(BuyList buyList) {

    }
}
