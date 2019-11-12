package ru.buylist.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ru.buylist.data.entity.Collection;

public class TemporaryDataStorage {

    private final String PREF_FILE_ALL_COLLECTION = "pref_buy_list_collection";
    private final String SELECTED_COLLECTION = "selected_collection";

    private static TemporaryDataStorage storage;

    private SharedPreferences preferences;

    private TemporaryDataStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_ALL_COLLECTION, Context.MODE_PRIVATE);
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

    public void saveSelectedCollection(long id) {
        preferences
                .edit()
                .remove(SELECTED_COLLECTION)
                .putLong(SELECTED_COLLECTION, id)
                .apply();
    }

    public long loadSelectedCollection() {
        return preferences.getLong(SELECTED_COLLECTION, 0);
    }

    public void deleteSelectedCollection() {
        preferences
                .edit()
                .remove(SELECTED_COLLECTION)
                .apply();
    }

    public void clearStorage() {
        preferences.edit().clear().apply();
    }

}
