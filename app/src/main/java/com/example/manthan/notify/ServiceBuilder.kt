package com.example.manthan.notify

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val URL = "http://192.168.1.3:3000"

    private val okHttpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
}