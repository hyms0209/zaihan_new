package kr.android.zaihan.push

import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.android.zaihan.link.LinkConstant
import java.net.URLDecoder

class ManagerFCMService : FirebaseMessagingService() {

    /***
     * 토큰정보 갱신 처리 필요
     */
    override fun onNewToken(token: String) {
        Log.d("_lmh", "fcm token :" + token)
        // TODO : 서버로 FCM 토큰 정보 올리는 코드 필요
    }

    /***
     * FCM으로 부터 메시지 수신
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.let {
            val pushInfo = getPushInfoFromMap(it.data)
            pushInfo?.let {

                var title   = it.getString("title") ?: ""
                var body    = it.getString("body") ?: ""
                var image   = it.getString("image") ?: ""
                var landing = it.getString(LinkConstant.IntentLinkKey) ?: ""

                NotificationManager.sendNotification(title, body, image, landing)
            }
        }
    }

    /**
     * 푸쉬 데이터를 번들에 담아 리턴
     * @param map 푸쉬 데이터
     */
    private fun getPushInfoFromMap(map: Map<String, String>): Bundle = Bundle().apply {
        map.forEach {
            // landingurl은 url이후의 값을 받기 위해서 URLDecoding을 하지 않는다.
            var decodeValue = ""
            if (it.key != LinkConstant.IntentLinkKey) {
                decodeValue = URLDecoder.decode(it.value, "UTF-8")
            } else {
                decodeValue = it.value
            }

            Log.d( "_lmh","key: ${it.key}, value: $decodeValue")
            putString(it.key.toLowerCase(), decodeValue)
        }
    }
}