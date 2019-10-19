package ru.buylist.utils;

import android.app.Application;

import ru.buylist.data.DataRepository;
import ru.buylist.data.db.BuyListDatabase;
import ru.buylist.utils.AppExecutors;

public class BuylistApp extends Application {
    private AppExecutors executors;

    @Override
    public void onCreate() {
        super.onCreate();
        executors = new AppExecutors();
    }

    public BuyListDatabase getDatabase() {
        return BuyListDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.get(getDatabase(), executors);
    }
}
