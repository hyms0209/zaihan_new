package kr.android.zaihan.webview

import android.util.Log
import com.google.gson.Gson
import kr.android.zaihan.common.preference.PreferenceManager
import kr.android.zaihan.manager.MediaPreference
import java.io.File

class  CaptureMediaType{
    companion object{
        val Camera    = "1"
        val Album     = "2"
    }
}
data class MediaFile(
    var key         :String = "",       // 키값
    var filePath    :String = "",       // 파일패스
    var mediaType   :String = CaptureMediaType.Camera ,        // 미디어 종류(앨범, 카메라)
    var makeTime    :String = ""
)

data class MediaList (
    var data:ArrayList<MediaFile> = arrayListOf()
)
class MediaFileManager {
    val TAG = MediaFileManager::class.simpleName
    var preference = PreferenceManager()

    var cameraItems:ArrayList<MediaFile> = arrayListOf()
    var albumItems:ArrayList<MediaFile> = arrayListOf()

    init {
        loadMediaFile()
    }

    /***
     * 미디어 파일 취득
     */
    private fun loadMediaFile() {
        var loadString = MediaPreference.getMediaFile()
        if ( loadString.count() > 5 ) {
            try {
                var mediaArray = Gson().fromJson(loadString, MediaList::class.java)

                mediaArray.data.filter { it.mediaType == CaptureMediaType.Album }?.let {
                    albumItems.addAll(it)
                }
                mediaArray.data.filter { it.mediaType == CaptureMediaType.Camera }?.let {
                    cameraItems.addAll(it)
                }
            } catch (e:Exception) {
                Log.d(TAG, "${e.message}")
            }
        }
    }

    /***
     * 미디어 파일 저장
     */
    private fun setMediaFile() {
        var saveItems = MediaList(((albumItems + cameraItems) as ArrayList<MediaFile>))

        try{
            var saveString = Gson().toJson(saveItems, MediaList::class.java)
            if ( saveString.count() > 5 ) {
                MediaPreference.setMediaFile(saveString)
            }
        } catch (e:Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    /***
     * 카메라 캡쳐 파일 패스 저장
     */
    fun setCameraItem(key:String, filePath:String) {
        this.cameraItems.add(MediaFile(key, filePath, CaptureMediaType.Camera, System.currentTimeMillis().toString()))
        setMediaFile()
    }

    /***
     * 카메라 캡쳐 미디어 파일 패스 취득
     */
    fun getCameraItem(key:String):String {
        return this.cameraItems.find { it.key == key }?.let { it.filePath } ?: ""
    }

    /***
     * 카메라 캡쳐 미디어 파일 삭제
     */
    fun removeCamera(key:String) {
        var findIndex = 0
        this.cameraItems.forEachIndexed {index, item ->
            if ( item.key == key ) {
                findIndex = index
                return@forEachIndexed
            }
        }
        this.cameraItems.removeAt(findIndex)
    }

    /***
     * 카메라 미디어 파일 전체 삭제
     */
    fun removeAllCamera() {
        this.cameraItems.clear()
    }

    /***
     * 앨범 캡쳐 파일 패스 저장
     */
    fun setAlbumItem(key:String, filePath:String) {
        this.albumItems.add(MediaFile(key, filePath, CaptureMediaType.Album, System.currentTimeMillis().toString()))
        setMediaFile()
    }

    /***
     * 앨범 캡쳐 미디어 파일 패스 취득
     */
    fun getAlbumItem(key:String):String {
        return this.albumItems.find { it.key == key }?.let { it.filePath } ?: ""
    }

    /***
     * 앨범 캡쳐 미디어 파일 삭제
     */
    fun removeAlbum(key:String) {
        var findIndex = 0
        this.albumItems.forEachIndexed {index, item ->
            if ( item.key == key ) {
                findIndex = index
                return@forEachIndexed
            }
        }
        this.albumItems.removeAt(findIndex)
    }

    /***
     * 앨범 미디어 파일 전체 삭제
     */
    fun removeAllAlbum() {
        this.albumItems.clear()
    }

    /***
     * 카메라 미디어 앨범 파일 전체 삭제
     */
    fun removeAllMedia() {
        this.albumItems.clear()
        this.cameraItems.clear()
    }

    /***
     * 폴더내 파일 삭제
     */
    fun removeFolder(filePath:String) {
       var dir    =  File(filePath);
       var childFileList = dir.listFiles()

        if (dir.exists()) {
            childFileList.forEach{
                if (it.isDirectory()) {
                    removeFolder(it.getAbsolutePath());    //하위 디렉토리
                } else {
                    it.delete()
                }
            }
            dir.delete();
        }
    }
}