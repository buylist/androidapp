package ru.buylist.recipe_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.List;

import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.entity.Collection;
import ru.buylist.utils.BuylistApp;


public class RecipeDialog extends DialogFragment {

    private static final String TAG = "RecipeDialog";
    private static final String ARG_RECIPE_DIALOG = "arg_recipe_dialog";

    private int count = -1;
    private TemporaryDataStorage storage;
    private RecipeListViewModel viewModel;

    public static RecipeDialog newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_DIALOG, type);

        RecipeDialog dialog = new RecipeDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = BuylistApp.instance().getStorage();
        viewModel = RecipeListActivity.obtainViewModel(getActivity());
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
                .loadCollection(getArguments().getString(ARG_RECIPE_DIALOG));

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
                .loadCollection(getArguments().getString(ARG_RECIPE_DIALOG));
        viewModel.transferTo(collections.get(count).getId(), viewModel.loadSelectedItems());
    }

}
