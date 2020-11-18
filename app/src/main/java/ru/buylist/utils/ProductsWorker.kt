package ru.buylist.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.coroutineScope
import ru.buylist.BuyListApp
import ru.buylist.data.entity.GlobalItem

class ProductsWorker(
        context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(FILE_NAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val type = object : TypeToken<List<GlobalItem>>() {}.type
                    val items: List<GlobalItem> = Gson().fromJson(jsonReader, type)

                    val database = BuyListApp.get().getDatabase()
                    database.globalItemDao().insertGlobalItems(items)
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

private const val FILE_NAME = "product_dictionary.json"