package kr.android.zaihan.network.vo

import com.google.gson.annotations.SerializedName

/***
 * 인증 정보
 */
data class MDAuthInfo(
    @SerializedName("authCode")
    var authCode:String = "",

    @SerializedName("accessToken")
    var accessToken:String = "",

    @SerializedName("expiresAt")
    var expiresAt:String = "",

    @SerializedName("refreshToken")
    var refreshToken:String = "",

    @SerializedName("refreshTokenExpiresAt")
    var refreshTokenExpiresAt:String = "",

    @SerializedName("issuedAt")
    var issuedAt:String = ""
)