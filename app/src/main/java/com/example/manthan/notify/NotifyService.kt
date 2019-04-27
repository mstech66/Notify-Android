package com.example.manthan.notify

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NotifyService {
    @POST("topic/login")
    fun logIn(@Body newUser: NotifyLogin): retrofit2.Call<NotifyLogin>
    @FormUrlEncoded
    @POST("paytm/generate_checksum")
    fun generateChecksum(
            @Field("MID") mId : String?,
            @Field("ORDER_ID") orderId: String?,
            @Field("CUST_ID") custId: String?,
            @Field("MOBILE_NO") mobileNo: String?,
            @Field("EMAIL") email: String?,
            @Field("CHANNEL_ID") channelId: String?,
            @Field("TXN_AMOUNT") txnAmnt: String?,
            @Field("WEBSITE") website: String?,
            @Field("CALLBACK_URL") callbackUrl: String?,
            @Field("INDUSTRY_TYPE_ID") industryId: String?
    ) : Call<Checksum>
}