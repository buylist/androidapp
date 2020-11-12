package ru.buylist.data.repositories.buyList

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.Error
import ru.buylist.data.Result.Success
import ru.buylist.data.dao.BuyListDao
import ru.buylist.data.entity.BuyList

class BuyListsRepository private constructor(
        private val buyListDao: BuyListDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BuyListsDataSource {

    override suspend fun saveBuyList(buyList: BuyList) = withContext(ioDispatcher) {
        buyListDao.insertBuyList(buyList)
    }

    override suspend fun updateBuyList(buyList: BuyList) = withContext(ioDispatcher) {
        buyListDao.updateBuyList(buyList)
    }

    override suspend fun deleteBuyList(buyList: BuyList) = withContext(ioDispatcher) {
        buyListDao.deleteBuyList(buyList)
    }

    override suspend fun deleteSelectedBuyList(buyLists: List<BuyList>) = withContext(ioDispatcher) {
        buyListDao.deleteSelectedBuyLists(buyLists)
    }

    override suspend fun deleteAllBuyList() = withContext(ioDispatcher) {
        buyListDao.deleteAllBuyLists()
    }

    override suspend fun getBuyLists(): Result<List<BuyList>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(buyListDao.getBuyLists())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getBuyList(buyListId: Long): Result<BuyList> = withContext(ioDispatcher) {
        try {
            val buyList = buyListDao.getBuyList(buyListId)
            if (buyList == null) {
                return@withContext Error(Exception("Список не найден"))
            } else {
                return@withContext Success(buyList)
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override fun observeBuyLists(): LiveData<Result<List<BuyList>>> {
        return buyListDao.observeBuyLists().map {
            Success(it)
        }
    }

    override fun observeBuyList(buyListId: Long): LiveData<Result<BuyList>> {
        return buyListDao.observeBuyListById(buyListId).map {
            Success(it)
        }
    }

    companion object {
        @Volatile private var instance: BuyListsRepository? = null

        fun getInstance(buyListDao: BuyListDao) =
                instance ?: synchronized(this) {
                    instance ?: BuyListsRepository(buyListDao).also { instance = it }
                }
    }
}