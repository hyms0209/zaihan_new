package kr.android.zaihan.webview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.customcameraalbum.common.enums.MultimediaTypes
import com.android.customcameraalbum.settings.MultiMediaSetting
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.android.zaihan.link.weblink.OpenAlbumLink
import kr.android.zaihan.link.weblink.OpenCameraLink
import kr.android.zaihan.link.weblink.WebLinker
import kr.android.zaihan.network.vo.MomentRegistResult
import kr.android.zaihan.repository.MomentProcessRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


interface WebViewViewModelType{
    var input:WebViewViewModelInput
    var output:WebViewViewModelOutput
}
interface WebViewViewModelOutput{
    val openCamera:LiveData<String>
    val openAlbum:LiveData<String>
    val getAlbumInfo: LiveData<String>
    val getCameraInfo: LiveData<String>
    val registMoment:LiveData<MomentRegistResult>
}
interface WebViewViewModelInput{
    fun processUri(uri:Uri)
    fun getAlbumInfo(data:Intent?)
    fun getCameraInfo(data:Intent?)
    fun registMomentVideo(file:File)
}
class WebViewViewModel() : ViewModel(),
    WebViewViewModelType,
    WebViewViewModelOutput,
    WebViewViewModelInput{

    override var input: WebViewViewModelInput = this
    override var output: WebViewViewModelOutput = this

    private var _openCamera = MutableLiveData<String>()
    override val openCamera: LiveData<String>
        get() = _openCamera

    private var _openAlbum = MutableLiveData<String>()
    override val openAlbum: LiveData<String>
        get() = _openAlbum

    private var _getAlbumInfo = MutableLiveData<String>()
    override val getAlbumInfo: LiveData<String>
                get() = _getAlbumInfo

    private var _getCameraInfo = MutableLiveData<String>()
    override val getCameraInfo: LiveData<String>
        get() = _getCameraInfo

    private var _registMoment = MutableLiveData<MomentRegistResult>()
    override val registMoment:LiveData<MomentRegistResult>
        get() = _registMoment

    lateinit var fileManager : MediaFileManager

    val repository = MomentProcessRepository()
    init {
        fileManager = MediaFileManager()
    }
    override fun processUri(uri: Uri) {
        var link = WebLinker.find(uri)
        link?.let {
            when(it) {
                is OpenCameraLink -> {              // 카메라 열기
                    _openCamera.postValue("")
                }
                is OpenAlbumLink -> {               // 앨범 열기
                    _openAlbum.postValue("")
                }
            }
        }
    }


    override fun getAlbumInfo(data: Intent?) {
        processMedia(data)?.let {
            _getAlbumInfo.postValue(encodingBase64(it))
        }
    }

    override fun getCameraInfo(data:Intent?) {
        processMedia(data)?.let {
            _getCameraInfo.postValue(encodingBase64(it))
        }
    }

    fun processMedia(data:Intent?):Bitmap? {
        data?.let {
            val path: List<Uri> = MultiMediaSetting.obtainResult(data)
            val pathList: List<String> = MultiMediaSetting.obtainPathResult(data)
            if (MultiMediaSetting.obtainMultimediaType(data) == MultimediaTypes.VIDEO) {
                try {
                    fileManager.setCameraItem("media001",pathList.get(0))
                    registMomentVideo(File(pathList.get(0)))
                    var bitmap = ThumbnailUtils.createVideoThumbnail(
                        pathList.get(0),
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                    )
                    return ThumbnailUtils.extractThumbnail(bitmap, 160, 160);
                } catch (e: Exception) {
                    return null
                }
            } else {
                // 이미지 파일일 경우, 썸네일 보여줌
                try {
                    var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ThumbnailUtils.createImageThumbnail(
                            File(pathList.get(0)),
                            Size(160, 160),
                            null)
                    } else {
                        BitmapFactory.decodeFile(pathList.get(0))
                    }
                    fileManager.setAlbumItem("media002",pathList.get(0))
                    registMomentVideo(File(pathList.get(0)))
                    return ThumbnailUtils.extractThumbnail(bitmap, 160, 160);
                } catch (e: Exception) {
                    return null
                }
            }
        }
        return null
    }

    fun encodingBase64(bitmap:Bitmap):String {
        try {
            // BytArrayOutputStream을 이용해 Bitmap 인코딩
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            // 인코딩된 ByteStream을 String으로 획득
            val image: ByteArray = byteArrayOutputStream.toByteArray()
            val byteStream: String = Base64.encodeToString(image, 0)
            return byteStream
        } catch (e:Exception ) {
            return ""
        }
    }

    override fun registMomentVideo(file: File) {

        val reqFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val video = MultipartBody.Part.createFormData("video", file.name, reqFile)

        val menuId: RequestBody = "menu01".toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryId: RequestBody = "category01".toRequestBody("text/plain".toMediaTypeOrNull())
        val subCategoryId: RequestBody = "subCategory01".toRequestBody("text/plain".toMediaTypeOrNull())
        val title: RequestBody = "타이틀테스트".toRequestBody("text/plain".toMediaTypeOrNull())
        val description: RequestBody = "설명테스트".toRequestBody("text/plain".toMediaTypeOrNull())

        repository.requestMomentRegist(video, null, menuId, categoryId, subCategoryId, title, description)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _registMoment.postValue(it)
            },{

            })
    }
}