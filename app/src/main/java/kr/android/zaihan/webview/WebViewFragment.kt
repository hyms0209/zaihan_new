package kr.android.zaihan.webview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.customcameraalbum.album.model.SelectedItemCollection
import com.android.customcameraalbum.common.entity.SaveStrategy
import com.android.customcameraalbum.common.enums.MimeType
import com.android.customcameraalbum.listener.OnMainListener
import com.android.customcameraalbum.settings.AlbumSetting
import com.android.customcameraalbum.settings.CameraSetting
import com.android.customcameraalbum.settings.GlobalSetting
import com.android.customcameraalbum.settings.MultiMediaSetting
import com.android.customcameraalbum.videoedit.VideoEditManager
import io.reactivex.rxjava3.core.Observable
import kr.android.zaihan.Glide4Engine
import kr.android.zaihan.R
import kr.android.zaihan.databinding.FragmentWebViewBinding
import kr.android.zaihan.link.applink.WebUrlOutLink.Companion.LOAD_URL
import java.util.concurrent.TimeUnit


class WebViewFragment : Fragment() {

    private lateinit var databinding : FragmentWebViewBinding
    private lateinit var webView:WebView

    private val vm:WebViewViewModel by viewModels()

    val REQUEST_CODE_CAMERA = 2001
    val REQUEST_CODE_ALBUM  = 2002

    lateinit var mGlobalSetting: GlobalSetting

    private val mCameraSetting: CameraSetting = CameraSetting()
    private val mAlbumSetting: AlbumSetting = AlbumSetting(false)

    var url = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentWebViewBinding.inflate(inflater, container, false)
        databinding.lifecycleOwner = this

        // 웹뷰 설정
        initSetting()

        mGlobalSetting = MultiMediaSetting.from(requireActivity()).choose(MimeType.ofAll())

        bindViewModel()
        // 앨범카메라 라이브러리 초기값 설정
        cameraAlbumSetting()

        arguments?.let {
            url =  it.getString(LOAD_URL, "")
        }

        // url 로딩
        loadUrl(this.url)

        return databinding.root

    }

    private fun cameraAlbumSetting() {
        mCameraSetting.useImgFlash(false)
        mCameraSetting.isSectionRecord(true)
        mCameraSetting.videoEdit(VideoEditManager())
        mCameraSetting.mimeTypeSet(MimeType.ofAll())
        mCameraSetting.duration(10)
        mCameraSetting.minDuration(1000)
        mAlbumSetting.spanCount(4)
    }

    /***
     * 웹뷰 설정 값 셋팅
     */
    fun initSetting() {
        webView = databinding.webView.apply {
            with(settings) {
                javaScriptEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                setNetworkAvailable(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
            }
            isHorizontalScrollBarEnabled = false
            webViewClient = CommonWebViewClient(webSchemeProcess)
        }
    }

    interface WebSchemeListener {
        fun onSchemeListener(uri: Uri?)
    }

    /***
     * 웹뷰에서 넘어오는 웹 스키마 처리
     */
    var webSchemeProcess:WebSchemeListener? = object:  WebSchemeListener{
        override fun onSchemeListener(uri: Uri?) {
            uri?.let {
                vm.processUri(it)
            }
        }
    }

    /***
     * url 로딩
     */
    fun loadUrl(url:String) {

        databinding.webView.post {
            databinding.webView.loadUrl(url)
        }
    }

    fun bindViewModel() {
        vm.output.openCamera.observe(viewLifecycleOwner, Observer {
            openCamera()
        })

        vm.output.openAlbum.observe(viewLifecycleOwner, Observer {
            openAlbum()
        })

        vm.output.getAlbumInfo.observe(viewLifecycleOwner, Observer {
            setJavaScript(it)
        })

        vm.output.getCameraInfo.observe(viewLifecycleOwner, Observer {
            setJavaScript(it)
        })
    }

    fun setJavaScript(setData:String) {
        webView.loadUrl("javascript:setImageByteCode('3','data:image/png;base64," + setData + "')")
    }

    fun openAlbum() {
        mAlbumSetting.collectionType(SelectedItemCollection.COLLECTION_UNDEFINED)
        mGlobalSetting.cameraSetting(null)
        mGlobalSetting.albumSetting(mAlbumSetting)
        mGlobalSetting.theme(com.android.customcameraalbum.R.style.AppTheme_Zaihan)
        mGlobalSetting
            .setOnMainListener(OnMainListener { errorMessage: String? ->
                errorMessage?.let { Log.d("A.lee", it) }
                Toast.makeText(
                    requireActivity(),
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            })
            .allStrategy(
                SaveStrategy(
                    true,
                    "kr.android.zaihan.fileprovider",
                    "zaihan"
                )
            ) // for glide-V4
            .imageEngine(Glide4Engine())
            .maxSelectablePerMediaType(
                null,
                9,
                1,
                0,
                0,
                0,
                0
            )
            .forResult(REQUEST_CODE_ALBUM)
    }

    fun openCamera() {
        mCameraSetting.mimeTypeSet(MimeType.ofAll())

        mGlobalSetting.cameraSetting(mCameraSetting)
        mGlobalSetting.albumSetting(null)
        mGlobalSetting.theme(R.style.AppTheme_CameraView)
        mGlobalSetting
            .setOnMainListener { errorMessage: String? ->
                errorMessage?.let { Log.d("A.lee", it) }
            }
            .allStrategy(
                SaveStrategy(
                    true,
                    "kr.android.zaihan.fileprovider",
                    "zaihan"
                )
            ) // for glide-V4
            .imageEngine(Glide4Engine())
            .maxSelectablePerMediaType(
                null,
                9,
                1,
                0,
                2,
                0,
                0
            )
            .forResult(REQUEST_CODE_CAMERA)
    }

    fun goBack() {
        if ( webView.canGoBack() ) {
            webView.goBack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ALBUM -> { vm.input.getAlbumInfo(data)}
            REQUEST_CODE_CAMERA -> {vm.input.getCameraInfo(data)}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object {

        var LOAD_URL = "https://hyms0209.github.io"

        @JvmStatic
        fun newInstance(url: String) =
        WebViewFragment().apply {
            arguments = Bundle().apply {
               this.putString(LOAD_URL, url)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        webSchemeProcess = null
    }
}