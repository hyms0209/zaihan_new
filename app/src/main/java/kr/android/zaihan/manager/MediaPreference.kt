package kr.android.zaihan.manager

import kr.android.zaihan.MainActivity
import kr.android.zaihan.common.preference.PreferenceManager


object MediaPreference {
    var preference = PreferenceManager()
    val KEY_MEDIA_FILE:String                  = "KEY_MEDIA_FILE"

    // 미디어 사용정보
    /***
     * 미디어 파일 정보
     */
    fun setMediaFile(mediaFileData:String) = preference.setString(MainActivity.instance!!, KEY_MEDIA_FILE, mediaFileData)
    fun getMediaFile() : String = preference.getString(MainActivity.instance!!, KEY_MEDIA_FILE)
}