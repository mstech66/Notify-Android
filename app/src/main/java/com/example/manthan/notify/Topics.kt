package com.example.manthan.notify

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_topics.*

class Topics : AppCompatActivity() {

    var chip : Chip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.title = "Subscribed Topics"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_topics)
        val sharedPreferences = getSharedPreferences("NotifyTopics", Context.MODE_PRIVATE)
        val list = sharedPreferences.getString("TOPICS", "")
        val arrayOfTopics : List<String> = list!!.split(",")
        arrayOfTopics.forEach {
            chip = Chip(chip_group.context)
            if(it != "") {
                chip?.text = it
                val unsubscribedTopic = it
                chip?.isClickable = true
                chip?.isCloseIconVisible = true
                chip_group.addView(chip as View)
                chip?.setOnCloseIconClickListener {
                    chip_group.removeView(it)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(unsubscribedTopic)
                    removeFromShared(unsubscribedTopic)
                    Toast.makeText(this, "Successfully Unsubscribed from $unsubscribedTopic", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFromShared(deleteTopic: String){
        val sharedPreferences = getSharedPreferences("NotifyTopics", Context.MODE_PRIVATE)
        val list = sharedPreferences.getString("TOPICS", "")
        val listSize = list?.length
        val searchSize = deleteTopic.length
        val lastIndex = list?.lastIndexOf(deleteTopic)
        val whereStringIs = lastIndex!! +searchSize
        var newList = "Null"
        Log.d("Size", "$listSize and $whereStringIs and $list")
        newList = if(listSize == whereStringIs && listSize != searchSize){
            list.replace(",$deleteTopic", "")
        }
        else if(listSize == searchSize){
            list.replace(deleteTopic, "")
        }
        else{
            list.replace("$deleteTopic,", "")
        }
        Log.d("Size", "Result: $newList")
        sharedPreferences.edit().putString("TOPICS", newList).apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferenceData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val darkMode = sharedPreferenceData.getBoolean("darkMode",false)
        if(darkMode){
            setTheme(R.style.AppTheme_Dark)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                topicsMain.setBackgroundColor(resources.getColor(R.color.darkMode, theme))
            }
            else{
                topicsMain.setBackgroundColor(resources.getColor(R.color.darkMode))
            }
        }
        else{
            setTheme(R.style.AppTheme)
            topicsMain.setBackgroundColor(Color.WHITE)
        }
    }
}
