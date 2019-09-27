package ru.buylist.listpattern;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import ru.buylist.R;
import ru.buylist.SingleFragmentActivity;

public class ListPatternActivity extends SingleFragmentActivity {

    // ключ для передачи идентификатора шаблона
    private static final String EXTRA_PATTERN_ID = "pattern_id";

    public static Intent newIntent(Context context, int patternId) {
        Intent intent = new Intent(context, ListPatternActivity.class);
        intent.putExtra(EXTRA_PATTERN_ID, patternId);
        return intent;
    }

    public static ListPatternViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(ListPatternViewModel.class);
    }

    @Override
    protected Fragment createFragment() {
        int patterId = getIntent().getIntExtra(EXTRA_PATTERN_ID, 0);
        return ListPatternFragment.newInstance(patterId);
    }

    @Override
    protected void setupViewModel() {
        setContentView(R.layout.activity_list_pattern);
    }

}
