package ru.buylist.buy_list;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.databinding.FragmentNewItemBinding;

public class NewItemFragment extends Fragment {
    private static final String TAG = "TAG";

    private static final String ARG_COLLECTION_ID = "args_collection_id";
    private static final String ARG_ITEM_ID = "args_item_id";

    private FragmentNewItemBinding binding;
    private NewItemViewModel viewModel;
    private CirclesAdapter circlesAdapter;

    // itemId == 0 - новый товар, != 0 - редактируемый товар
    public static NewItemFragment newInstance(long collectionId, long itemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_COLLECTION_ID, collectionId);
        args.putLong(ARG_ITEM_ID, itemId);
        NewItemFragment fragment = new NewItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(NewItemViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_item, container, false);
        binding.setViewmodel(viewModel);
        binding.setCallback(callback);

        viewModel.showHideKeyboard(true);

        setupCircleAdapter();
        setupKeyboardListener();
        selectCircle(getArguments().getLong(ARG_ITEM_ID));
        return binding.getRoot();
    }

    private void setupCircleAdapter() {
        if (circlesAdapter == null) {
            circlesAdapter = new CirclesAdapter(getColors());
        } else {
            circlesAdapter.setColors(getColors());
        }
        binding.recyclerCircles.setAdapter(circlesAdapter);

        binding.recyclerCircles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.showHideCirclesButton(isFirstPositionVisible(), isLastPositionVisible());
            }
        });
    }

    private void setupKeyboardListener() {
        binding.fieldUnit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveItem(
                        getArguments().getLong(ARG_COLLECTION_ID),
                        getArguments().getLong(ARG_ITEM_ID),
                        circlesAdapter.getColor());

                binding.fieldName.requestFocus();
                return true;
            }
            return false;
        });
    }

    private List<Category> getColors() {
        String[] standardColors = getResources().getStringArray(R.array.category_color);
        List<Category> colors = new ArrayList<>();

        for (String standardColor : standardColors) {
            Category color = new Category();
            color.setColor(standardColor);
            color.setSelected(false);
            colors.add(color);
        }
        return colors;
    }

    private boolean isFirstPositionVisible() {
        LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
        int pos = layoutManager.findFirstVisibleItemPosition();
        return (pos > 0);
    }

    private boolean isLastPositionVisible() {
        LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
        int position = layoutManager.findLastVisibleItemPosition();
        return (position >= circlesAdapter.getItemCount() - 1);
    }

    private void selectCircle(long itemId) {
        if (itemId == 0) {
            return;
        }

        Item item = viewModel.getItem(itemId);

        if (item.getCategoryColor() == null) {
            return;
        }

        List<Category> categories = getColors();
        for (Category category : categories) {
            if (category.getColor().equals(item.getCategoryColor())) {
                category.setSelected(true);
                circlesAdapter.setColors(categories);
                return;
            }
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private final CategoryCallback callback = new CategoryCallback() {

        @Override
        public void onCreateButtonClick() {
            viewModel.saveItem(
                    getArguments().getLong(ARG_COLLECTION_ID),
                    getArguments().getLong(ARG_ITEM_ID),
                    circlesAdapter.getColor());

            binding.fieldName.requestFocus();
        }

        @Override
        public void onPrevCircleButtonClick() {
            LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());

            int firstVisibleItemIndex = layoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemIndex > 0) {
                layoutManager.smoothScrollToPosition(binding.recyclerCircles, null, firstVisibleItemIndex-1);
            }
        }

        @Override
        public void onNextCircleButtonClick() {
            LinearLayoutManager layoutManager =((LinearLayoutManager) binding.recyclerCircles.getLayoutManager());
            int totalItemCount = circlesAdapter.getItemCount();

            if (totalItemCount <= 0) return;

            int lastVisibleItemIndex = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemIndex >= totalItemCount) return;

            layoutManager.smoothScrollToPosition(binding.recyclerCircles, null, lastVisibleItemIndex+1);
        }
    };

}
