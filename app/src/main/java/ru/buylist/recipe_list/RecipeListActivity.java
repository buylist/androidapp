package ru.buylist.recipe_list;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import ru.buylist.R;
import ru.buylist.buy_list.BuyListActivity;
import ru.buylist.buy_list.BuyListViewModel;
import ru.buylist.buy_list.NewItemFragment;
import ru.buylist.collection_lists.CollectionActivity;
import ru.buylist.databinding.ActivityRecipeListBinding;
import ru.buylist.utils.SingleFragmentActivity;

public class RecipeListActivity extends SingleFragmentActivity {

    // ключ для передачи идентификатора шаблона
    private static final String EXTRA_RECIPE_ID = "recipe_id";
    private static final String EXTRA_RECIPE_TITLE = "recipe_title";

    private RecipeListViewModel viewModel;

    public static Intent newIntent(Context context, long recipeId, String recipeTitle) {
        Intent intent = new Intent(context, RecipeListActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(EXTRA_RECIPE_TITLE, recipeTitle);
        return intent;
    }

    public static RecipeListViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(RecipeListViewModel.class);
    }

    @Override
    protected Fragment createFragment() {
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, 0);
        return RecipeListFragment.newInstance(recipeId);
    }

    @Override
    protected void setupViewModel() {
        ActivityRecipeListBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_recipe_list);

        viewModel = obtainViewModel(this);
        binding.setViewmodel(viewModel);

        // открытие NewItemFragment
        viewModel.getNewItemEvent().observe(this, recipeId ->
                setCategory(getIntent().getLongExtra(EXTRA_RECIPE_ID, 0), recipeId));

        // открытие диалогового окна
        viewModel.getDialogEvent().observe(this, type ->
                RecipeDialog.newInstance(type).show(getSupportFragmentManager(), "custom"));

        viewModel.getReturnToBuyListEvent().observe(this, collection -> {
            Intent intent = BuyListActivity.newIntent(this, collection.getId(), collection.getTitle());
            startActivity(intent);
        });

        // временное решение
        BuyListViewModel buyViewmodel = ViewModelProviders.of(this).get(BuyListViewModel.class);
        buyViewmodel.getReturnToListEvent().observe(this, this::returnToRecipe);

        setTitle(getIntent().getStringExtra(EXTRA_RECIPE_TITLE));
    }

    // вызов NewItemFragment
    private void setCategory(long recipeId, long itemId) {
        Fragment fragment = NewItemFragment.newInstance(recipeId, itemId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // возврат к RecipeListFragment
    private void returnToRecipe(long collectionId) {
        Fragment fragment = RecipeListFragment.newInstance(collectionId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showHome() {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }
}
