package com.example.manthan.notify

import android.app.Service
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class BroadcastService : Service() {
    private var broadcastReceiver : BroadcastReceiver? = null
    var notifyDatabase : NotifyDatabase? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("Test 101","Service Running!")
        registerReceiver()
    }

    override fun onDestroy() {
        Log.d("Test 101", "Service Destroyed!")
        unregisterReceiver(broadcastReceiver)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("Test 101", "onTaskRemoved() called!")
        registerReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("Test 101", "onStartCommand() called!")
        return START_STICKY
    }

    private fun registerReceiver(){
        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val notificationData : Bundle? = intent?.extras
                val title = notificationData!!.getString("TITLE")
                val message = notificationData.getString("MESSAGE")
                val priority = notificationData.getString("PRIORITY")
                val base64 = notificationData.getString("BASE64")
                notifyDatabase = Room.databaseBuilder(context!!.applicationContext, NotifyDatabase::class.java,"notify.db")
                .enableMultiInstanceInvalidation().build()
                runOnBack(title, message, priority, base64)
                Log.d("Test 101", "Received by Service")
            }
        }
        val filter = IntentFilter("ServiceToActivity")
        registerReceiver(broadcastReceiver, filter)
    }

    private fun runOnBack(title:String, message:String, priority:String, base64:String)
    {
        AsyncTask.execute(object : Runnable{
            override fun run() {
                notifyDatabase?.notifyDao()?.insert(Notify(title, message, priority, base64))
            }
        })
    }
}