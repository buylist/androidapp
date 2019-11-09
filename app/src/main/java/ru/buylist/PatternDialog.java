package ru.buylist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.List;

import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.entity.Collection;
import ru.buylist.pattern_list.PatternListActivity;
import ru.buylist.pattern_list.PatternListViewModel;
import ru.buylist.utils.BuylistApp;


public class PatternDialog extends DialogFragment {

    private static final String TAG = "PatternDialog";
    private static final String ARG_PATTERN_DIALOG = "arg_pattern_dialog";

    private int count;
    private TemporaryDataStorage storage;

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
        PatternListViewModel viewModel = PatternListActivity.obtainViewModel(getActivity());
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
                        dialog.cancel();
                    }
                });

        dialog.setSingleChoiceItems(collectionTitle, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count = which;
                Log.i(TAG, "SingleChoiceItem: " + which);
            }
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

    }

}
