package kr.android.zaihan.link.weblink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

abstract class WebLink {
    private var mUri: Uri? = null
    private var mBundle: Bundle? = null

    abstract fun getScheme(): String

    abstract fun getHost(): String

    abstract fun getPath(): String

    abstract fun start()
    // 구분자 모두 제외한 스키마 + 호스트 + 패스 문자열(스키마 찾기용)
    fun getCompUriString(): String? {
        return getScheme() + getHost() + getPath()
    }

    abstract fun getIntent(context: Context?): Intent?

    fun setUri(uri: Uri?) {
        mUri = uri
    }

    // getIntent시 번들로 담아야 할경우 사용
    open fun setBundle(bundle: Bundle?) {
        mBundle = bundle
    }

    open fun getUri(): Uri? {
        return mUri
    }

    open fun getBundle(): Bundle? {
        return mBundle
    }

    // 링크 이름 취득
    open fun getLinkName(): String? {
        return ""
    }

    fun clear() {
        // 번들정보가 있는 경우 번들 정보 초기화
        if (mBundle != null) {
            mBundle!!.clear()
            mBundle = null
        }
        // URI정보가 있는 경우 URI정보 초기화
        if (mUri != null) {
            mUri = null
        }
    }
}