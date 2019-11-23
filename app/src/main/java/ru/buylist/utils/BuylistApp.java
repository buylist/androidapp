package ru.buylist.utils;

import android.app.Application;

import ru.buylist.data.DataRepository;
import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.db.BuyListDatabase;

public class BuylistApp extends Application {
    private static BuylistApp instance;
    private AppExecutors executors;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        executors = new AppExecutors();
    }

    public static BuylistApp instance() {
        return instance;
    }

    public BuyListDatabase getDatabase() {
        return BuyListDatabase.getInstance(this, executors);
    }

    public DataRepository getRepository() {
        return DataRepository.get(getDatabase(), executors);
    }

    public TemporaryDataStorage getStorage() {
        return TemporaryDataStorage.instance(this);
    }
}
