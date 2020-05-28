package ru.buylist.data.repositories.items

import ru.buylist.data.dao.GlobalItemDao
import ru.buylist.data.entity.GlobalItem
import ru.buylist.utils.AppExecutors

class GlobalItemsRepository(
        private val executors: AppExecutors,
        private val globalItemDao: GlobalItemDao
) : GlobalItemsDataSource {

    override fun saveGlobalItem(globalItem: GlobalItem) {
        executors.discIO().execute { globalItemDao.insertGlobalItem(globalItem) }
    }

    override fun updateGlobalItem(globalItem: GlobalItem) {
        executors.discIO().execute { globalItemDao.updateGlobalItem(globalItem) }
    }

    override fun updateGlobalItemCategory(category: String) {
        executors.discIO().execute { globalItemDao.updateGlobalItemCategory(category) }
    }

    override fun updateGlobalItemColor(color: String) {
        executors.discIO().execute { globalItemDao.updateGlobalItemColor(color) }
    }

    override fun getGlobalItems(callback: GlobalItemsDataSource.LoadGlobalItemsCallback) {
        executors.discIO().execute {
            val items = globalItemDao.getGlobalItems()
            executors.mainThread().execute {
                if (items.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onGlobalItemsLoaded(items)
                }
            }
        }
    }

    override fun getGlobalItem(globalItemId: Long, callback: GlobalItemsDataSource.GetGlobalItemCallback) {
        executors.discIO().execute {
            val item = globalItemDao.getGlobalItem(globalItemId)
            executors.mainThread().execute {
                if (item == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onGlobalItemLoaded(item)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: GlobalItemsRepository? = null

        fun getInstance(executors: AppExecutors, globalItemDao: GlobalItemDao) =
                instance ?: synchronized(this) {
                    instance ?: GlobalItemsRepository(executors, globalItemDao)
                            .also { instance = it }
                }
    }
}