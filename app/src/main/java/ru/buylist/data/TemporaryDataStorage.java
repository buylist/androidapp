package ru.buylist.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;

public class TemporaryDataStorage {

    private final String PREF_FILE_NAME = "pref_buy_list";
    private final String SELECTED_ITEMS = "selected_items";

    private static TemporaryDataStorage storage;
    private SharedPreferences preferences;

    private TemporaryDataStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static TemporaryDataStorage instance(Context context) {
        if (storage == null) {
            synchronized (TemporaryDataStorage.class) {
                if (storage == null) {
                    storage = new TemporaryDataStorage(context);
                }
            }
        }
        return storage;
    }

    public void saveCollection(List<Collection> collection, String type) {
        Gson gson = new Gson();
        String jsonCollection = gson.toJson(collection);

        preferences
                .edit()
                .remove(type)
                .putString(type, jsonCollection)
                .apply();
    }

    public List<Collection> loadCollection(String type) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Collection>>(){}.getType();

        String jsonCollection = preferences.getString(type, null);
        return gson.fromJson(jsonCollection, listType);
    }

    public void saveItems(List<Item> items) {
        Gson gson = new Gson();
        String jsonItems = gson.toJson(items);

        preferences
                .edit()
                .putString(SELECTED_ITEMS, jsonItems)
                .apply();
    }

    public List<Item> loadItems() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Item>>(){}.getType();

        String jsonItems = preferences.getString(SELECTED_ITEMS, null);
        return gson.fromJson(jsonItems, listType);
    }

    public void clearStorage() {
        preferences.edit().clear().apply();
    }

}
