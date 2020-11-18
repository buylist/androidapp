package ru.buylist.data.repositories.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.Error
import ru.buylist.data.Result.Success
import ru.buylist.data.dao.GlobalItemDao
import ru.buylist.data.entity.GlobalItem

class GlobalItemsRepository(
        private val globalItemDao: GlobalItemDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GlobalItemsDataSource {

    override suspend fun saveGlobalItem(globalItem: GlobalItem) = withContext(ioDispatcher) {
        globalItemDao.insertGlobalItem(globalItem)
    }

    override suspend fun updateGlobalItem(globalItem: GlobalItem) = withContext(ioDispatcher) {
        globalItemDao.updateGlobalItem(globalItem)
    }

    override suspend fun updateColor(color: String) = withContext(ioDispatcher) {
        globalItemDao.updateColor(color)
    }

    override suspend fun getGlobalItems(): Result<List<GlobalItem>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(globalItemDao.getGlobalItems())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getGlobalItem(globalItemId: Long): Result<GlobalItem> = withContext(ioDispatcher) {
        try {
            val item = globalItemDao.getGlobalItem(globalItemId)
            if (item == null) {
                return@withContext Error(Exception("Товар не найден"))
            } else {
                return@withContext Success(item)
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override fun observeGlobalItems(): LiveData<Result<List<GlobalItem>>> {
        return globalItemDao.observeGlobalItems().map {
            Success(it)
        }
    }



    companion object {
        @Volatile
        private var instance: GlobalItemsRepository? = null

        fun getInstance(globalItemDao: GlobalItemDao) =
                instance ?: synchronized(this) {
                    instance ?: GlobalItemsRepository(globalItemDao)
                            .also { instance = it }
                }
    }
}