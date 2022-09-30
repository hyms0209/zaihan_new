package kr.android.zaihan.network.vo

import com.google.gson.annotations.SerializedName

class ResMomentRegist(
    @SerializedName("data")
    var data:MomentRegistResult? = null,

    ): ResBase()

data class MomentRegistResult(
    @SerializedName("id")
    var id:String = "",
    @SerializedName("createdAt")
    var createdAt:String = "",
)
