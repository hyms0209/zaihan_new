package kr.co.thecheck.android.manager.preference

import kr.android.zaihan.AppApplication


class UserPreference {
    companion object {

        val KEY_MANAGER_USER_ID:String                  = "KEY_MANAGER_USERID"
        val KEY_MANAGER_USER_PWD:String                 = "KEY_MANAGER_USER_PWD"

        val KEY_MANAGER_ACCESS_TOKEN:String             = "KEY_MANAGER_ACCESS_TOKEN"
        val KEY_MANAGER_ACCESS_TOKEN_EXPIRE:String      = "KEY_MANAGER_ACCESS_TOKEN_EXPIRE"

        val KEY_MANAGER_REFRESH_TOKEN:String            = "KEY_MANAGER_REFRESH_TOKEN"
        val KEY_MANAGER_REFRESH_TOKEN_EXPIRE:String     = "KEY_MANAGER_REFRESH_TOKEN_EXPIRE"

        val KEY_MANAGER_AUTH_CODE:String                = "KEY_MANAGER_AUTH_CODE"

        val KEY_MANAGER_ATTENDANCE_DAY:String           = "KEY_MANAGER_ATTENDANCE_DAY"

        /***
         * 사용자 아이디
         */
        fun setUserId(id:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_USER_ID, id)
        fun getUserId() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_USER_ID)

        /***
         * 사용자 비밀번호
         */
        fun setUserPwd(pwd:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_USER_PWD, pwd)
        fun getUserPwd() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_USER_PWD)


        /***
         * access Token 정보
         */
        fun setAccessToken(token:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_ACCESS_TOKEN, token)
        fun getAccessToken() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_ACCESS_TOKEN)

        /***
         * access Token Expire정보
         */
        fun setAccessTokenExpire(tokenExpire:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_ACCESS_TOKEN_EXPIRE, tokenExpire)
        fun getAccessTokenExpire() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_ACCESS_TOKEN_EXPIRE)

        /***
         * refresh Token 정보
         */
        fun setRefreshToken(token:String) = kr.android.zaihan.common.preference.PreferenceManager().setString(
            AppApplication.ctx!!, this.KEY_MANAGER_REFRESH_TOKEN, token)
        fun getRefreshToken() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_REFRESH_TOKEN)

        /***
         * refresh Token Expire정보
         */
        fun setRefreshTokenExpire(tokenExpire:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_REFRESH_TOKEN_EXPIRE, tokenExpire)
        fun getRefreshTokenExpire() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_REFRESH_TOKEN_EXPIRE)

        /***
         * AuthCode Expire정보
         */
        fun setAuthCode(authCode:String) = kr.android.zaihan.common.preference.PreferenceManager()
            .setString(AppApplication.ctx!!, this.KEY_MANAGER_AUTH_CODE, authCode)
        fun getAuthCode() : String = kr.android.zaihan.common.preference.PreferenceManager()
            .getString(AppApplication.ctx!!, UserPreference.KEY_MANAGER_AUTH_CODE)


    }
}