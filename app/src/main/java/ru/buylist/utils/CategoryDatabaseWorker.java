package ru.buylist.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Category;

// Для заполнения базы данных стандартными категориями
public class CategoryDatabaseWorker {

    public static List<Category> getStandartCategories(Context context) {
        List<Category> categories = new ArrayList<>();
        String[] standardCategories = context.getResources().getStringArray(R.array.category);
        for (int i = 0; i < standardCategories.length; i++) {
            categories.add(getCategory(i, context));
        }
        return categories;
    }

    private static Category getCategory(int i, Context context) {
        String[] standardCategories = context.getResources().getStringArray(R.array.category);
        String[] standardColor = context.getResources().getStringArray(R.array.category_color);
        return new Category(i, standardCategories[i], standardColor[i]);
    }
}
