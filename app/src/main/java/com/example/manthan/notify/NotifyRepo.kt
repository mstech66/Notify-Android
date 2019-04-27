package com.example.manthan.notify

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread

class NotifyRepo(private val notifyDao: NotifyDao) {
    val allNotifications = notifyDao.getAllNotifications()

    init {
        allNotifications.observeForever {  }
    }

    @WorkerThread
    suspend fun insert(notify: Notify){
        notifyDao.insert(notify)
    }
    @WorkerThread
    suspend fun update(notify: Notify){
        notifyDao.update(notify)
    }

    @WorkerThread
    fun delete(notify: Notify){
        notifyDao.delete(notify)
    }
    @WorkerThread
    suspend fun deleteAllNotifications(){
        notifyDao.deleteAllNotifications()
    }
    @WorkerThread
    suspend fun getAllNotifications(): LiveData<List<Notify>> {
        return allNotifications
    }
}