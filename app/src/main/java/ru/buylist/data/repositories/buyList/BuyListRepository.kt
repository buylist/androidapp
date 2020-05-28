package ru.buylist.data.repositories.buyList

import ru.buylist.data.dao.BuyListDao
import ru.buylist.data.entity.BuyList
import ru.buylist.utils.AppExecutors

class BuyListRepository private constructor(
        private val executors: AppExecutors,
        private val buyListDao: BuyListDao
): BuyListDataSource {

    override fun saveBuyList(buyList: BuyList) {
        executors.discIO().execute { buyListDao.insertBuyList(buyList) }
    }

    override fun updateBuyList(buyList: BuyList) {
        executors.discIO().execute { buyListDao.updateBuyList(buyList) }
    }

    override fun deleteBuyList(buyList: BuyList) {
        executors.discIO().execute { buyListDao.deleteBuyList(buyList) }
    }

    override fun deleteSelectedBuyList(buyLists: List<BuyList>) {
        executors.discIO().execute { buyListDao.deleteSelectedBuyLists(buyLists) }
    }

    override fun deleteAllBuyList() {
        executors.discIO().execute { buyListDao.deleteAllBuyLists() }
    }

    override fun getBuyLists(callback: BuyListDataSource.LoadBuyListsCallback) {
        executors.discIO().execute {
            val buyLists = buyListDao.getBuyLists()
            executors.mainThread().execute{
                if (buyLists.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onBuyListsLoaded(buyLists)
                }
            }
        }
    }

    override fun getBuyList(buyListId: Long, callback: BuyListDataSource.GetBuyListCallback) {
        executors.discIO().execute {
            val buyList = buyListDao.getBuyList(buyListId)
            executors.mainThread().execute {
                if (buyList == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onBuyListsLoaded(buyList)
                }
            }
        }
    }

    companion object {
        @Volatile private var instance: BuyListRepository? = null

        fun getInstance(executors: AppExecutors, buyListDao: BuyListDao) =
                instance ?: synchronized(this) {
                    instance ?: BuyListRepository(executors, buyListDao).also { instance = it }
                }
    }
}