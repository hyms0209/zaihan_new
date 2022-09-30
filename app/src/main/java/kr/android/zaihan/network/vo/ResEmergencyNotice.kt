package kr.android.zaihan.network.vo

import com.google.gson.annotations.SerializedName

class ResEmergencyNotice(
    @SerializedName("data")
    var data:EmergencyNoticeData? = null,

): ResBase()

data class EmergencyNoticeData(
    @SerializedName("message")
    var messsage:String = "",
    @SerializedName("title")
    var title:String = "",
    @SerializedName("description")
    var description:String = "",
    @SerializedName("createdAt")
    var createdAt:String = "",
    @SerializedName("isShow")
    var isShow:String = "",
)
