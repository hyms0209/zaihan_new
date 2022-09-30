package kr.android.zaihan.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import kr.android.zaihan.AppApplication
import kr.android.zaihan.MainActivity
import kr.android.zaihan.R
import java.net.URL

class NotificationManager {

    companion object {
        fun sendNotification(title: String, message: String, image: String, landing: String) {
            // landing이 있는 경우 landingurl 작성
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
                data = Uri.parse(landing)
            }

            AppApplication.ctx?.let {
                val pendingIntent = PendingIntent.getActivity(it,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val channelId = "kr.android.zaihanpush"
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(it, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                if ( !TextUtils.isEmpty(image) ) {
                    val mBitmap = BitmapFactory.decodeStream(URL(image).openConnection().getInputStream())
                    notificationBuilder.setLargeIcon(mBitmap)
                    notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(mBitmap))
                }
                var noti = AppApplication.ctx!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                // 오레오버젼이상은 알림 채널 생성
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, it.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
                    channel.description = it.getString(R.string.app_name)
                    channel.setShowBadge(true)
                    channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                    noti.createNotificationChannel(channel)
                }
                noti.notify(0, notificationBuilder.build())
            }
        }
    }
}