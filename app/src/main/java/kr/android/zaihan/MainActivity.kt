package kr.android.zaihan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import kr.android.zaihan.databinding.ActivityMainBinding
import kr.android.zaihan.network.vo.EmergencyNoticeData
import kr.android.zaihan.ui.dialog.ConfirmDialog
import kr.android.zaihan.ui.dialog.OnDialogClickListener
import com.android.customcameraalbum.settings.*
import com.android.customcameraalbum.common.entity.SaveStrategy
import com.android.customcameraalbum.common.enums.MimeType
import com.android.customcameraalbum.videoedit.VideoEditManager

import kr.android.zaihan.link.applink.OutLinker
import kr.android.zaihan.link.applink.WebUrlOutLink
import kr.android.zaihan.ui.BaseActivity
import kr.android.zaihan.ui.SplashViewModel
import kr.android.zaihan.webview.WebViewFragment


class MainActivity : BaseActivity() {

    companion object {
        var instance: MainActivity? = null
    }

    val REQUEST_CODE_CHOOSE = 2003

    private fun initSetting() {
        mCameraSetting.useImgFlash(false)
        mCameraSetting.isSectionRecord(true)
        mCameraSetting.videoEdit(VideoEditManager())
        mCameraSetting.mimeTypeSet(MimeType.ofAll())
//        mCameraSetting.duration(10000)
        mCameraSetting.minDuration(1000)
        mAlbumSetting.spanCount(4)
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
            .forResult(REQUEST_CODE_CHOOSE)
    }
    private var mGlobalSetting: GlobalSetting =
        MultiMediaSetting.from(this).choose(MimeType.ofAll())

    private val mCameraSetting: CameraSetting = CameraSetting()
    private val mAlbumSetting: AlbumSetting = AlbumSetting(false)

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<SplashViewModel>()

    lateinit var webViewFragment:WebViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        webViewFragment = WebViewFragment.newInstance("http://zaihan.devgrr.kr/")
        supportFragmentManager.beginTransaction().add(binding.flContainer.id, webViewFragment, "WebView").commitNow()
        initSetting()

        setContentView(binding.root)

        bindInitViewModel()
        Log.d("MainActivity","onResume")
    }

    override fun onResume() {
        Log.d("MainActivity","onResume")
        super.onResume()
        consume()
    }

    fun bindInitViewModel() {

    }

    fun consume() {
        if ( OutLinker.hasOutLink() ) {
            var link = OutLinker.getCurrent()
            when(link) {

                is WebUrlOutLink -> {
                    link.getIntent(this)?.let { linkIntent ->
                        linkIntent.getStringExtra(WebUrlOutLink.LOAD_URL)?.let {
                            webViewFragment.loadUrl(it)
                        }
                    }
                }
            }

            OutLinker.removeCurrentLink()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.last()?.let {
            if ( it is WebViewFragment ) {
                it.goBack()
            }
        }

        //forceFinish()
    }
    fun openAlertDialog(data:EmergencyNoticeData) {
        var dialog = ConfirmDialog()

        dialog.setOnClickListener(object: OnDialogClickListener{
            override fun onCancelClickListener() {

            }

            override fun onConfirmClickListener() {
                Log.d("EmergencyDialog", "Confirm Click")
                //dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, "AlertDialog")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var lastFragment = supportFragmentManager.fragments.last()
        lastFragment?.let {
            if ( it is WebViewFragment ) {
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}