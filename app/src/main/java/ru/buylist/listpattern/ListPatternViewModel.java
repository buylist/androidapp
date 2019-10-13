package ru.buylist.listpattern;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import ru.buylist.data.db.ProductLab;

public class ListPatternViewModel extends AndroidViewModel {

    // Флаги для отображения/скрытия элементов разметки
    public final ObservableBoolean buttonMoveVisibility = new ObservableBoolean(false);
    public final ObservableBoolean layoutNewProductVisibility = new ObservableBoolean(false);

    // Поля для ввода нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private final Context context;
    private ProductLab productLab;

    public ListPatternViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        productLab = ProductLab.get(context);
    }
}
