package ru.buylist.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.buylist.data.dao.*
import ru.buylist.data.entity.*
import ru.buylist.utils.AppExecutors

@Database(entities = [BuyList::class, Pattern::class, Recipe::class, GlobalItem::class],
        version = 1, exportSchema = false)
abstract class BuyListDatabase : RoomDatabase() {
    abstract fun buyListDao(): BuyListDao
    abstract fun patternDao(): PatternDao
    abstract fun recipeDao(): RecipeDao
    abstract fun globalItemDao(): GlobalItemDao

    companion object {
        private const val DATABASE_NAME = "buyListBase"

        @Volatile private var instance: BuyListDatabase? = null

        fun getInstance(context: Context, executors: AppExecutors): BuyListDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context, executors).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, executors: AppExecutors): BuyListDatabase {
            return Room.databaseBuilder(context, BuyListDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executors.discIO().execute {
                                // prepopulate database here
                            }
                        }
                    })
                    .build()
        }
    }
}