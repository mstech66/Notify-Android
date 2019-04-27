package com.example.manthan.notify

import com.google.gson.annotations.SerializedName

class PayTM {
    @SerializedName("MID")
    val mId: String? = "1234" //merchant id
    @SerializedName("ORDER_ID")
    var orderId: String? = null
    @SerializedName("CUST_ID")
    var custId: String? = null
    @SerializedName("INDUSTRY_TYPE_ID")
    val industryType: String? = "Retail" //industry type id
    @SerializedName("CHANNEL_ID")
    val channelId: String? = "WAP" //channel id
    @SerializedName("TXN_AMOUNT")
    var txnAmnt: String? = null
    @SerializedName("WEBSITE")
    val website: String? = "WEBSTAGING" //website
    @SerializedName("CALLBACK_URL")
    var callBackUrl: String? = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$orderId" //calback url
    @SerializedName("EMAIL")
    var email: String? = null
    @SerializedName("MOBILE_NO")
    var mobileNo: String? = null

    constructor(orderId: String?, txnAmnt: String?, custId: String?, callBackUrl: String?, email: String?, mobileNo: String?)
    {
        this.orderId = orderId
        this.txnAmnt = txnAmnt
        this.custId = custId
        this.callBackUrl = callBackUrl
        this.email = email
        this.mobileNo = mobileNo
    }
}