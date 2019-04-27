package com.example.manthan.notify

import android.util.Base64
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notify")
data class Notify(var title: String, var message: String, var priority: String, var imageBase64: String) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}