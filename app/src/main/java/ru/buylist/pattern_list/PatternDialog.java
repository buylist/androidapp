package ru.buylist.pattern_list;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import ru.buylist.data.TemporaryDataStorage;


public class PatternDialog extends DialogFragment {

    private static final String TAG = "PatternDialog";
    private static final String ARG_PATTERN_DIALOG = "arg_pattern_dialog";

    private int count = -1;
    private TemporaryDataStorage storage;
    private PatternListViewModel viewModel;

    public static PatternDialog newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_PATTERN_DIALOG, type);

        PatternDialog dialog = new PatternDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = BuylistApp.instance().getStorage();
        viewModel = PatternListActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] collectionTitle = getCollectionTitle();

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Коллекция списков")
                .setCancelable(false)
                .setPositiveButton("Готово", (dialog1, which) -> {
                    transferItems();
                    dialog1.cancel();
                });

        dialog.setSingleChoiceItems(collectionTitle, -1, (dialog12, which) -> {
            count = which;
            Log.i(TAG, "SingleChoiceItem: " + which);
        });
        return dialog.create();
    }

    private String[] getCollectionTitle() {
        List<Collection> collections = storage
                .loadCollection(getArguments().getString(ARG_PATTERN_DIALOG));

        String[] titles = new String[collections.size()];
        for (int i = 0; i < collections.size(); i++) {
            titles[i] = collections.get(i).getTitle();
        }
        return titles;
    }

    private void transferItems() {
        if (count < 0) {
            return;
        }

        List<Collection> collections = storage
                .loadCollection(getArguments().getString(ARG_PATTERN_DIALOG));
        viewModel.transferTo(collections.get(count).getId(), viewModel.loadSelectedItems());
    }

}
