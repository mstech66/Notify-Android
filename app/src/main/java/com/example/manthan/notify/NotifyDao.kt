package com.example.manthan.notify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface
NotifyDao {
    @Insert
    fun insert(notify: Notify)

    @Update
    fun update(notify: Notify)

    @Delete
    fun delete(notify: Notify)

    @Query("DELETE FROM Notify")
    fun deleteAllNotifications()

    @Query("SELECT * FROM Notify ORDER BY id DESC")
    fun getAllNotifications() : LiveData<List<Notify>>

}