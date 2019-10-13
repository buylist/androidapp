package ru.buylist.listpattern;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import ru.buylist.R;
import ru.buylist.SingleFragmentActivity;

public class ListPatternActivity extends SingleFragmentActivity {

    // ключ для передачи идентификатора шаблона
    private static final String EXTRA_PATTERN_ID = "pattern_id";

    private ListPatternViewModel viewModel;

    public static Intent newIntent(Context context, long patternId) {
        Intent intent = new Intent(context, ListPatternActivity.class);
        intent.putExtra(EXTRA_PATTERN_ID, patternId);
        return intent;
    }

    public static ListPatternViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(ListPatternViewModel.class);
    }

    @Override
    protected Fragment createFragment() {
        long patterId = getIntent().getLongExtra(EXTRA_PATTERN_ID, 0);
        return ListPatternFragment.newInstance(patterId);
    }

    @Override
    protected void setupViewModel() {
        setContentView(R.layout.activity_list_pattern);

        viewModel = obtainViewModel(this);

        viewModel.getProductCreated().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long productId) {
                setCategory(productId);
            }
        });

        viewModel.getCategoryAdded().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long patternId) {
                saveCategory(patternId);
            }
        });
    }

    // вызов CategoryFragment
    private void setCategory(long productId) {

    }

    // возврат к ListPatternFragment
    private void saveCategory(long patternId) {

    }

}
