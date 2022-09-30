package kr.android.zaihan.network



import com.google.gson.annotations.SerializedName
import kr.android.zaihan.network.vo.MDAuthInfo
import kr.android.zaihan.network.vo.ResBase


class ReqLogin {
    @SerializedName("username")
    var username:String = ""

    @SerializedName("password")
    var password:String = ""
}

class ResAuthInfo : ResBase(){
    @SerializedName("data")
    var data: MDAuthInfo = MDAuthInfo()
}
