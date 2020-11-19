package ru.buylist.data.repositories.buyList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.Error
import ru.buylist.data.Result.Success
import ru.buylist.data.dao.BuyListDao
import ru.buylist.data.dao.GlobalItemDao
import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.wrappers.ItemWrapper

class BuyListsRepository private constructor(
        private val buyListDao: BuyListDao,
        private val globalItemDao: GlobalItemDao,
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

    override fun observeBuyList(buyListId: Long?): LiveData<Result<String>> {
        return if (buyListId == null) {
            MutableLiveData<ItemWrapper>().map { Error(Exception("Buy list id is null")) }
        } else {
            buyListDao.observeBuyListById(buyListId).map {
                Success(it)
            }
        }
    }

    override suspend fun updateProducts(buyListId: Long?, products: String) = withContext(ioDispatcher) {
        if (buyListId == null) return@withContext
        buyListDao.updateProducts(buyListId, products)
    }

    override suspend fun getProducts(buyListId: Long?): Result<String> = withContext(ioDispatcher) {
        if (buyListId == null) return@withContext Error(Exception("Products not found"))

        try {
            val items = buyListDao.getProducts(buyListId)
            if (items == null || items.isEmpty()) {
                return@withContext Error(Exception("Products not found"))
            } else {
                return@withContext Success(items)
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun getTags(): Result<List<GlobalItem>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(globalItemDao.getGlobalItems())
        } catch (e: Exception) {
            Error(e)
        }
    }

    companion object {
        @Volatile private var instance: BuyListsRepository? = null

        fun getInstance(buyListDao: BuyListDao, globalItemDao: GlobalItemDao) =
                instance ?: synchronized(this) {
                    instance ?: BuyListsRepository(buyListDao, globalItemDao).also { instance = it }
                }
    }
}