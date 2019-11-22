package ru.buylist.collection_lists;


import android.content.Intent;

import androidx.fragment.app.Fragment;

import ru.buylist.R;


import ru.buylist.pattern_list.PatternListActivity;
import ru.buylist.pattern_list.PatternListFragment;
import ru.buylist.recipe_list.RecipeListActivity;
import ru.buylist.recipe_list.RecipeListFragment;
import ru.buylist.utils.SingleFragmentActivity;
import ru.buylist.data.entity.Collection;
import ru.buylist.buy_list.BuyListFragment;
import ru.buylist.buy_list.BuyListActivity;

public class CollectionActivity extends SingleFragmentActivity implements CollectionFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CollectionFragment();
    }

    // ничего не делает, надо удалить или откорректировать
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    // устанавливает макет
    @Override
    protected void setupViewModel() {
        setContentView(R.layout.activity_masterdetail);
    }

    // открывает соответствующие activity/fragments в зависимости от типа
    public void onCollectionSelected(Collection collection) {
        switch (collection.getType()) {
            case CollectionType.BuyList:
//                if (findViewById(R.id.detail_fragment_container) == null) {
                    Intent buyIntent = BuyListActivity.newIntent(this, collection.getId(), collection.getTitle());
                    startActivity(buyIntent);
//                } else {
//                    Fragment newDetail = BuyListFragment.newInstance(collection.getId());
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.detail_fragment_container, newDetail)
//                            .commit();
//                }
                break;
            case CollectionType.PATTERN:
//                if (findViewById(R.id.detail_fragment_container) == null) {
                    Intent patternIntent = PatternListActivity.newIntent(this, collection.getId(), collection.getTitle());
                    startActivity(patternIntent);
//                } else {
//                    Fragment newDetail = PatternListFragment.newInstance(collection.getId());
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.detail_fragment_container, newDetail)
//                            .commit();
//                }
                break;
            case CollectionType.RECIPE:
//                if (findViewById(R.id.detail_fragment_container) == null) {
                    Intent recipeIntent = RecipeListActivity.newIntent(this, collection.getId(), collection.getTitle());
                    startActivity(recipeIntent);
//                } else {
//                    Fragment newDetail = RecipeListFragment.newInstance(collection.getId());
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.detail_fragment_container, newDetail)
//                            .commit();
//                }
                break;
        }
    }

    // возврат на главный экран (клик в bottomNavigation)
    @Override
    public void showHome() {
        CollectionFragment fragment = new CollectionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
