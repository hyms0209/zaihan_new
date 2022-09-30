package kr.android.zaihan.link.applink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import kr.android.zaihan.MainActivity
import kr.android.zaihan.link.LinkConstant

import kr.android.zaihan.ui.BaseActivity
import kr.android.zaihan.ui.activity.SplashActivity

class AppToAppActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if ( adjustLink() ) {
            processDeepLink()
        }
        moveMain()
        finish()
    }

    /***
     * 링크 유효성 체크
     */
    fun adjustLink():Boolean {
        var ret = false
         intent.data?.let {
             ret = (it.scheme == LinkConstant.Custom.scheme)
         }
         intent.getStringExtra(LinkConstant.IntentLinkKey)?.let {
             var uri = Uri.parse(it)
             uri?.let {
                 ret = (uri.scheme == LinkConstant.Custom.scheme)
             }
         }
        return ret
    }

    /**
     * 딥링크 처리
     */
    private fun processDeepLink() {

        // 딥링크 정보 초기화
        OutLinker.removeCurrentLink()
        intent?.let {

            var uri:Uri? = intent.data
            val landingurl = intent.getStringExtra(LinkConstant.IntentLinkKey)
            if ( !TextUtils.isEmpty(landingurl) ) {
                uri = Uri.parse(landingurl)
            }
            uri?.let {
                if (it.scheme == LinkConstant.Custom.scheme) {
                    val link: OutLink? = OutLinker.find(uri)
                    link?.let {
                        if ("gobrowser" == link.getPath()) {
                            this.startActivity(link.getIntent(this))
                            OutLinker.removeCurrentLink()
                            finish()
                            return
                        }
                    }
                } else {
                    Log.d("_lmh", "not support scheme ")
                }
            }
        }
    }

    /***
     * 메인화면 이동
     */
    private fun moveMain() {
        // 메인이 백그라운드에 있는 경우, 계정정보가 있는 경우 메인으로 바로 진입
        if ( MainActivity.instance != null ) {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
            })
            overridePendingTransition(0,0)
        }
        // 메인이 백그라운드에 없는 경우 스플래시부터 이동
        else {
            startActivity(Intent(this, SplashActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
            })
            overridePendingTransition(0,0)
        }

    }
}