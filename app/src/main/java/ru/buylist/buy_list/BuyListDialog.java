package ru.buylist.buy_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.List;

import ru.buylist.collection_lists.CollectionType;
import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.utils.BuylistApp;


public class BuyListDialog extends DialogFragment {

    private static final String TAG = "BuyListDialog";
    private static final String ARG_COLLECTION_TYPE = "arg_buy_list_dialog_type";
    private static final String ARG_COLLECTION_ID = "arg_buy_list_dialog_id";

    private TemporaryDataStorage storage;
    private BuyListViewModel viewModel;

    private int count = -1;
    private List<Item> items;

    public static BuyListDialog newInstance(String type, long collectionId) {
        Bundle args = new Bundle();
        args.putString(ARG_COLLECTION_TYPE, type);
        args.putLong(ARG_COLLECTION_ID, collectionId);

        BuyListDialog dialog = new BuyListDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = BuylistApp.instance().getStorage();
        viewModel = BuyListActivity.obtainViewModel(getActivity());
        items = viewModel.getAllItems();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] collectionTitle = getCollectionTitle();

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getDialogTitle())
                .setCancelable(false)
                .setPositiveButton("Готово", (dialog12, which) -> {
//                        transferItems();
                    chooseItemsFrom();
                    dialog12.cancel();
                });

        dialog.setSingleChoiceItems(collectionTitle, -1, (dialog1, which) -> {
            count = which;
            Log.i(TAG, "SingleChoiceItem: " + which);
        });
        return dialog.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        storage.saveSelectedCollection(getArguments().getLong(ARG_COLLECTION_ID));
    }

    private String getDialogTitle() {
        switch (getArguments().getString(ARG_COLLECTION_TYPE)) {
            case CollectionType.PATTERN:
                return "Шаблоны";
            case CollectionType.RECIPE:
                return "Рецепты";
            default:
                return "Коллекция списков";
        }
    }

    private String[] getCollectionTitle() {
        List<Collection> collection = storage
                .loadCollection(getArguments().getString(ARG_COLLECTION_TYPE));

        String[] titles = new String[collection.size()];
        for (int i = 0; i < collection.size(); i++) {
            titles[i] = collection.get(i).getTitle();
        }
        return titles;
    }

    private void chooseItemsFrom() {
        if (count < 0) {
            return;
        }

        List<Collection> collection = storage
                .loadCollection(getArguments().getString(ARG_COLLECTION_TYPE));

        viewModel.chooseItemsFrom(collection.get(count));
    }
}
