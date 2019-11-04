package ru.buylist.pattern_list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import ru.buylist.R;
import ru.buylist.buy_list.BuyListViewModel;
import ru.buylist.buy_list.CategoryFragment;
import ru.buylist.collection_lists.CollectionType;
import ru.buylist.databinding.ActivityPatternListBinding;
import ru.buylist.utils.SingleFragmentActivity;

public class PatternListActivity extends SingleFragmentActivity {

    // ключ для передачи идентификатора шаблона
    private static final String EXTRA_PATTERN_ID = "pattern_id";

    private PatternListViewModel viewModel;

    public static Intent newIntent(Context context, long patternId) {
        Intent intent = new Intent(context, PatternListActivity.class);
        intent.putExtra(EXTRA_PATTERN_ID, patternId);
        return intent;
    }

    public static PatternListViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(PatternListViewModel.class);
    }

    @Override
    protected Fragment createFragment() {
        long patternId = getIntent().getLongExtra(EXTRA_PATTERN_ID, 0);
        return PatternListFragment.newInstance(patternId);
    }

    @Override
    protected void setupViewModel() {
        ActivityPatternListBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_pattern_list);

        viewModel = obtainViewModel(this);

        // открытие CategoryFragment
        viewModel.getItemCreated().observe(this, itemId -> setCategory(itemId));

        viewModel.getDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                PatternDialog.newInstance().show(getSupportFragmentManager(), "custom");
            }
        });

        // временное решение
        BuyListViewModel buyViewmodel = ViewModelProviders.of(this).get(BuyListViewModel.class);
        buyViewmodel.getCategoryUpdated().observe(this, collectionId -> saveCategory(collectionId));
        binding.setViewmodel(viewModel);
    }

    // вызов CategoryFragment
    private void setCategory(long itemId) {
        Fragment fragment = CategoryFragment.newInstance(itemId, CollectionType.PATTERN);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        viewModel.bottomShow.set(false);
    }

    // возврат к PatternListFragment
    private void saveCategory(long collectionId) {
        Fragment fragment = PatternListFragment.newInstance(collectionId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
