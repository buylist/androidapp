package ru.buylist.pattern_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;



public class PatternDialog extends DialogFragment {
    private static final String ARG_PATTERN_DIALOG = "arg_pattern_dialog";

    public static PatternDialog newInstance() {
        return new PatternDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PatternListViewModel viewModel = PatternListActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] arrayExample = new String[]{"List 1", "List 2", "List 3"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Коллекция списков")
                .setCancelable(false)
                .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.setSingleChoiceItems(arrayExample, -1, null);
        return dialog.create();
    }

}
