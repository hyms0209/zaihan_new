package kr.android.zaihan.network


import kr.co.thecheck.android.manager.preference.UserPreference
import java.lang.System.currentTimeMillis

class SessionManager {
    companion object Auth {
        var accessToken:String
        get() {
            return UserPreference.getAccessToken() ?: ""
        }
        set(value:String) {
            UserPreference.setAccessToken(value)
        }

        var refreshToken:String
        get() {
            return UserPreference.getRefreshToken() ?: ""
        }
        set(value:String) {
            UserPreference.setRefreshToken(value)
        }

        var accessTokenExpire:Long
        get() {
            return (if (UserPreference.getAccessTokenExpire().isEmpty()) "0" else UserPreference.getAccessTokenExpire() ).toLong()
        }
        set(value: Long) {
            UserPreference.setAccessTokenExpire(value.toString())
        }

        var refreshTokenExpire:Long
        get() {
            return (if (UserPreference.getRefreshTokenExpire().isEmpty()) "0" else UserPreference.getRefreshTokenExpire() ).toLong()
        }
        set(value: Long) {
            UserPreference.setRefreshTokenExpire(value.toString())
        }

        var authCode:String
        get() {
            return UserPreference.getAuthCode()
        }
        set(value:String){
            return UserPreference.setAuthCode(value)
        }

        fun isAccessTokenExpire() : Boolean {
            return (this.accessTokenExpire <= currentTimeMillis())
        }

        fun isRefreshTokenExpire() : Boolean {
            return (this.refreshTokenExpire <= currentTimeMillis())
        }

        fun setAuthInfo(authInfo: ResAuthInfo) {
            accessToken = authInfo.data.accessToken
            refreshToken = authInfo.data.refreshToken
            accessTokenExpire = authInfo.data.expiresAt.toLong()
            refreshTokenExpire = authInfo.data.refreshTokenExpiresAt.toLong()
            authCode = authInfo.data.authCode
        }

        fun reset() {
            accessToken = ""
            refreshToken = ""
            accessTokenExpire = 0.toLong()
            refreshTokenExpire = 0.toLong()
            authCode = ""
            UserPreference.setUserPwd("")
            UserPreference.setUserId("")
        }
    }

}