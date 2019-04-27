package com.example.manthan.notify

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NotifyViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()

    private val coroutineContext : CoroutineContext

        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repo: NotifyRepo //reference to the repository class
    val allNotifications : LiveData<List<Notify>>

    //initializer to get a reference from NotifyDatabase to NotifyDao and which constructs NotifyRepo
    init {
        val notifyDao = NotifyDatabase.getInstance(application, scope).notifyDao()
        repo = NotifyRepo(notifyDao)
        allNotifications = repo.allNotifications
    }

    fun insert(notify: Notify) = scope.launch(Dispatchers.IO) {
        repo.insert(notify)
    }

    fun delete(notify: Notify) = scope.launch(Dispatchers.IO) {
        repo.delete(notify)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}