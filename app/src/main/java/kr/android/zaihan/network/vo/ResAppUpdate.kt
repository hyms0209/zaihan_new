package kr.android.zaihan.network.vo

import com.google.gson.annotations.SerializedName

class ResAppUpdate(
    @SerializedName("data")
    var data:AppUpdateData? = null,

): ResBase()

data class AppUpdateData(
    @SerializedName("os")
    var os:String = "",
    @SerializedName("version")
    var version:String = "",
    @SerializedName("status")
    var status:String = "",
    var title:String = "",
    var message:String = ""
)
