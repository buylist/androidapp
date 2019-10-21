package ru.buylist.pattern_list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.buylist.R;
import ru.buylist.databinding.FragmentPatternListBinding;

public class PatternListFragment extends Fragment {

    // ключ для передачи идентификатора шаблона
    private static final String ARG_PATTERN_ID = "pattern_id";

    private PatternListViewModel viewModel;

    public static PatternListFragment newInstance(long patternId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PATTERN_ID, patternId);

        PatternListFragment fragment = new PatternListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = PatternListActivity.obtainViewModel(getActivity());

        long patternId = getArguments() != null ? getArguments().getLong(ARG_PATTERN_ID, 0) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPatternListBinding binding = FragmentPatternListBinding
                .inflate(inflater, container, false);
        binding.setViewmodel(viewModel);
        setupFab(binding.getRoot());
        return binding.getRoot();
    }

    private void setupFab(View view) {
        FloatingActionButton addItemButton = view.findViewById(R.id.fab_new_item);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.addNewItem();
            }
        });
    }
}
