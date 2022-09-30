package kr.android.zaihan.common.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

object CommonUtil {

    /*
     *  getKmFromDistance(distance:String)  거리를 소수점 이하 두자리 km로 변경
     *
     */
    fun getKmFromDistance(distance: String) : String {
        if ( distance == "0" ) {
            return "위치정보 알수 없음"
        }
        var ret = ""
        var dist = distance.toDouble()
        if ( dist > 100) {

            ret = "${"%.1f".format(dist / 1000f)}km"
        } else {
            ret = "${dist.toInt()}m"
        }
        return ret!!
    }

    fun isGrantedForegroundLocationPermission(context: Context?): Boolean {
        return (
            (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED))
    }

    /***
     * 밀리세컨드시간을 yyyy.MM.dd 포맷으로 변경
     */
    fun miliToDate(milisec:Long) :String  {
        if ( milisec == 0L ) {
            return ""
        }
        val calendar = Calendar.getInstance()
        try {
            val registDate = Date(milisec)
            val formatter = SimpleDateFormat("yyyy.MM.dd")
            return formatter.format(registDate)
        } catch (e: Exception) {
            return ""
        }
    }

    /***
     * 밀리세컨드시간을 yyyy년 MM년 dd일 HH:mm:ss 포맷으로 변경
     */
    fun miliToDateHangul(milisec:Long) :String  {
        if ( milisec == 0L ) {
            return ""
        }
        val calendar = Calendar.getInstance()
        try {
            val registDate = Date(milisec)
            val formatter = SimpleDateFormat("yyyy년 M월 d일 H:m:s")
            return formatter.format(registDate)
        } catch (e: Exception) {
            return ""
        }
    }

    /***
     * 밀리세컨드시간을 yyyy년 MM년 dd일 포맷으로 변경
     */
    fun miliToDateHangulNotTime(milisec:Long) :String  {
        if ( milisec == 0L ) {
            return ""
        }
        val calendar = Calendar.getInstance()
        try {
            val registDate = Date(milisec)
            val formatter = SimpleDateFormat("yyyy년 M월 d일 H:m:s")
            return formatter.format(registDate)
        } catch (e: Exception) {
            return ""
        }
    }

    fun getCurrentTime() : Date? {
        var formatter = SimpleDateFormat("yyyyMMdd")
        // 현재 날짜 구함
        var currentStr = formatter.format(Date())
        return formatter.parse(currentStr)
    }

    fun getCurrentTimeToString() : String {
        var formatter = SimpleDateFormat("yyyyMMdd")
        // 현재 날짜 구함
        var currentStr = formatter.format(Date())
        return currentStr
    }

    /***
     * 입력 날짜와 현재 날짜 일수차 구하기
     */
    fun moreThenSmallCurrentDay(date : String) : Boolean  {
        if ( date.isNullOrEmpty() ) {
            return true
        }
        var formatter = SimpleDateFormat("yyyyMMdd")
        // 비교할 날짜 구함
        val compareDate = formatter.parse(date)
        // 현재 날짜 구함
        val currentDate = getCurrentTime()
        return currentDate!! > compareDate
    }


    fun isConnected(): Boolean {
        val connectivityManager =
            kr.android.zaihan.AppApplication.ctx!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ret = with(connectivityManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = activeNetwork ?: return false
                val actNw = getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->  true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }
        return ret
    }
}