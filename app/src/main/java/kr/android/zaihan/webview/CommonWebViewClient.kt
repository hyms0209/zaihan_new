package kr.android.zaihan.webview

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.*
import kr.android.zaihan.link.LinkConstant


class CommonWebViewClient(var listener: WebViewFragment.WebSchemeListener?) : WebViewClient(){

    /***
     * 나중에 웹에서 앱의 인입 비동기 처리를 위해서 별도 생성해놓음
     */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let {
            var ret = true;
            if ( it.scheme == LinkConstant.WebToApp.scheme) {
                listener?.onSchemeListener(Uri.parse(it.toString()))
                return true
            } else {
                return false
            }
        }
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }

    override fun onSafeBrowsingHit(
        view: WebView?,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }

}