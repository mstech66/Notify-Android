package com.example.manthan.notify

import com.google.gson.annotations.SerializedName

class Checksum {

    @SerializedName("CHECKSUMHASH")
    var checksumHash: String? = null

    @SerializedName("ORDER_ID")
    var orderId: String? = null

    @SerializedName("payt_STATUS")
    var paytStatus: String? = null

    constructor(checksumHash: String?, orderId: String?, paytStatus: String?){
        this.checksumHash = checksumHash
        this.orderId = orderId
        this.paytStatus = paytStatus
    }
}