package kr.android.zaihan.link.weblink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import kr.android.zaihan.link.LinkConstant

class OpenCameraLink : WebLink() {
    private var mUri: Uri? = null
    private var mBundle: Bundle? = null

    override fun getScheme(): String = LinkConstant.WebToApp.scheme

    override fun getHost(): String = "open"

    override fun getPath():String = "/camera"

    override fun start() {
    }

    override fun getIntent(context: Context?): Intent? {
        return Intent()
    }

    // getIntent시 번들로 담아야 할경우 사용
    override fun setBundle(bundle: Bundle?) {
        mBundle = bundle
    }
}