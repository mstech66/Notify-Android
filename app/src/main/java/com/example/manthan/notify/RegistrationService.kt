package com.example.manthan.notify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Job
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.Exception


class RegistrationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        FirebaseMessaging.getInstance().subscribeToTopic("General")
        updateTopics("General")
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        val data: Map<String, String> = p0!!.data
        val dataTitle : String = data.get("title")!!
        val dataMsg : String = data.get("body")!!
        var dataPriority : String = data.get("priority")!!
        val dataTopic : String = data.get("dataTopic")!!
        var base64 : String = "Normal" //check normal in bottom sheets
        try{
            base64 = data.get("base64")!!
        }
        catch(e: Exception){
            Log.d("base64", "NO image included!")
        }
        if(dataTopic != "Normal"){
            FirebaseMessaging.getInstance().subscribeToTopic(dataTopic)
            updateTopics(dataTopic)
        }
        else{
            if(dataPriority == "Payment"){
                val dataAmount: String = data.get("amount")!!
                if(dataAmount != "NULL"){
                    dataPriority = "$dataPriority $dataAmount"
                    Log.d("Paytm", "Amount is $dataAmount")
                }
            }
            Log.d("Test 101", "Service: $dataTitle $dataMsg $dataPriority")
            sendMessageToActivity(dataTitle,dataMsg,dataPriority, base64) //for BackgroundService
            sendMessageToMain(dataTitle, dataMsg, dataPriority, base64) //for Main
        }
        sendNotification(dataTitle,dataMsg)
    }

    private fun sendMessageToActivity(title:String, message:String, priority:String, base64: String){
        val broadCastingIntent = Intent()
        broadCastingIntent.action = "ServiceToActivity"
        broadCastingIntent.putExtra("TITLE",title)
        broadCastingIntent.putExtra("MESSAGE",message)
        broadCastingIntent.putExtra("PRIORITY",priority)
        broadCastingIntent.putExtra("BASE64", base64)
        sendBroadcast(broadCastingIntent)
        Log.d("Test 101", "Sent to Activity")
    }

    private fun sendMessageToMain(title:String, message:String, priority:String, base64:String){
        val broadCastingIntent = Intent()
        broadCastingIntent.action = "ServiceToMain"
        broadCastingIntent.putExtra("TITLE",title)
        broadCastingIntent.putExtra("MESSAGE",message)
        broadCastingIntent.putExtra("PRIORITY",priority)
        broadCastingIntent.putExtra("BASE64", base64)
        sendBroadcast(broadCastingIntent)
        Log.d("Test 101", "Sent to Main")
    }

    private fun handleJob(){
        val firebaseJobDispatcher= FirebaseJobDispatcher(GooglePlayDriver(this@RegistrationService))
        val myJob : Job = firebaseJobDispatcher.newJobBuilder().setService(JobService::class.java).setTag("com.example.manthan.notify.Notify").build()
        firebaseJobDispatcher.schedule(myJob)
    }

    private fun updateTopics(dataTopic: String){
        val sharedPreferences : SharedPreferences = getSharedPreferences("NotifyTopics", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        val lastVal = sharedPreferences.getString("TOPICS", "")
        val stringBuilder = StringBuilder()
        stringBuilder.append("$lastVal,$dataTopic") //create a list of subscribed topics in string builder
        editor.putString("TOPICS",stringBuilder.toString())
        editor.apply()
    }

    private fun sendNotification(title: String, messageBody : String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.baseline_cloud_done_black_18)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val time = Date().time
        val tempStr = time.toString()
        val last4Str = tempStr.substring(tempStr.length - 4)
        val notifyID = last4Str.toInt()

        notificationManager.notify(notifyID/* ID of notification */, notificationBuilder.build())
    }
}