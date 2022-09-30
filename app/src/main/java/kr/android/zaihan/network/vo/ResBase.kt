package kr.android.zaihan.network.vo

import com.google.gson.annotations.SerializedName

open class ResBase{

//    @SerializedName("status")
//    var status = ""

    @SerializedName("code")
    var code = ""

    @SerializedName("message")
    var messsage = ""
//
//    @SerializedName("errorCode")
//    var errorCode = ""
//
//    @SerializedName("errorMsg")
//    var errorMsg = ""

//    fun isSuccess(): Boolean {
//        return "SUCCESS" == status
//    }
}