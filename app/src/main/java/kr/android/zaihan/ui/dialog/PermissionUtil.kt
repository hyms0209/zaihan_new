package kr.android.zaihan.ui.dialog

import android.Manifest
import android.os.Build
import kr.android.zaihan.R

enum class PermissionItem(val requestCode:Int) {
    WifiState(1000),
    Phone(1001),
    Alarm(1002),
    SaveStore(1003),
    Camera(1004),
    Location(1005),
    Picture(1006);

    fun getPermission():Array<String> = when(this){
        WifiState   -> arrayOf()
        Phone       -> {
            arrayOf(if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                Manifest.permission.READ_PHONE_STATE else Manifest.permission.READ_PHONE_NUMBERS)
        }
        Alarm       -> arrayOf()
        SaveStore   -> if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) else arrayOf()
        Camera      -> arrayOf(android.Manifest.permission.CAMERA)
        Location    -> arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        Picture     -> arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun getImageRecource():Int = when(this) {
        WifiState   -> R.drawable.ic_access_wifi
        Phone       -> R.drawable.ic_access_phone
        Alarm       -> R.drawable.ic_access_alarm
        SaveStore   -> R.drawable.ic_access_savestore
        Camera      -> R.drawable.ic_access_camera
        Location    -> R.drawable.ic_access_location
        Picture     -> R.drawable.ic_access_picture
    }

    fun getPermissionTitle():String = when(this) {
        WifiState   -> "Wi-Fi 연결 정보"
        Phone       -> "전화"
        Alarm       -> "알림"
        SaveStore   -> "저장 공간"
        Camera      -> "카메라"
        Location    -> "위치 정보"
        Picture     -> "사진"
    }

    fun getPermissionDesc():String = when(this) {
        WifiState   -> "앱 이용 네트워크 연결 체크"
        Phone       -> "전화번호 확인을 위해 사용"
        Alarm       -> "이벤트, 공지 알림, 상품 수신 알림을 위해 사용"
        SaveStore   -> "서비스 이용을 위한 데이터를 저장하기 위해 사용"
        Camera      -> "모멘트 서비스 이용 시 사진 및 동영상 촬영을 위해 사용"
        Location    -> "사용자 위치 정보를 확인하여 정보 제공"
        Picture     -> "재한의 서비스 이용 시 내 기기에서 사진 파일을\n전송하기 위해 사용"
    }
}

object PermissionUtil {
    fun essentialPermission():Array<PermissionItem> {
        return arrayOf(PermissionItem.WifiState, PermissionItem.Alarm)
    }

    fun choicePermission():Array<PermissionItem> {
        return arrayOf(PermissionItem.Camera,
            PermissionItem.Location,
            PermissionItem.Phone,
            PermissionItem.Picture,
            PermissionItem.SaveStore
        )
    }

    fun getPermissions(permissions:Array<PermissionItem>):Array<String> {
        var list = arrayListOf<String>()
        permissions.forEach {
            if ( it.getPermission().count() > 0 ) {
                it.getPermission().forEach { item ->
                    list.add(item)
                }
            }
        }
        return list.toTypedArray()
    }
}