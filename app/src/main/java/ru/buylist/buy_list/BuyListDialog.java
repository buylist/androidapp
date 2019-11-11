package ru.buylist.buy_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;
import ru.buylist.utils.BuylistApp;


public class BuyListDialog extends DialogFragment {

    private static final String TAG = "BuyListDialog";
    private static final String ARG_BUY_LIST_DIALOG_TYPE = "arg_buy_list_dialog_type";
    private static final String ARG_BUY_LIST_DIALOG_ID = "arg_buy_list_dialog_id";

    private TemporaryDataStorage storage;
    private BuyListViewModel viewModel;

    private int count = -1;
    private List<Item> items;

    public static BuyListDialog newInstance(String type, long collectionId) {
        Bundle args = new Bundle();
        args.putString(ARG_BUY_LIST_DIALOG_TYPE, type);
        args.putLong(ARG_BUY_LIST_DIALOG_ID, collectionId);

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
        dialog.setTitle("Коллекция списков")
                .setCancelable(false)
                .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transferItems();
                        dialog.cancel();
                    }
                });

        dialog.setSingleChoiceItems(collectionTitle, -1, (dialog1, which) -> {
            count = which;
            Log.i(TAG, "SingleChoiceItem: " + which);
        });
        return dialog.create();
    }

    private String[] getCollectionTitle() {
        List<Collection> collection = storage
                .loadCollection(getArguments().getString(ARG_BUY_LIST_DIALOG_TYPE));

        String[] titles = new String[collection.size()];
        for (int i = 0; i < collection.size(); i++) {
            titles[i] = collection.get(i).getTitle();
        }
        return titles;
    }

    private void transferItems() {
        if (count < 0) {
            return;
        }

        List<Collection> collection = storage
                .loadCollection(getArguments().getString(ARG_BUY_LIST_DIALOG_TYPE));
        long collectionId = getArguments().getLong(ARG_BUY_LIST_DIALOG_ID);

        List<Item> itemsToTransfer = new ArrayList<>();
        for (Item item : items) {
            if (item.getCollectionId() == collection.get(count).getId()) {
                itemsToTransfer.add(item);
            }
        }
        viewModel.transferItems(itemsToTransfer, collectionId);
    }
}
