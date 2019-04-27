package com.example.manthan.notify

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Notify::class], version = 1)
abstract class NotifyDatabase : RoomDatabase(){

    abstract fun notifyDao() : NotifyDao

    companion object {
        @Volatile
        private var INSTANCE : NotifyDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope) : NotifyDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, NotifyDatabase::class.java,"notify.db")
                        .addCallback(NotifyCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class NotifyCallback(private val scope: CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.notifyDao())
                }
            }
        }
        fun populateDatabase(notifyDao: NotifyDao){
            notifyDao.deleteAllNotifications()
            var notify = Notify("Intro", "Welcome to Notify!","Intro", "")
            notifyDao.insert(notify)
            notify = Notify("Title","This is where the message will appear", "Priority", "")
            notifyDao.insert(notify)
        }
    }
}
