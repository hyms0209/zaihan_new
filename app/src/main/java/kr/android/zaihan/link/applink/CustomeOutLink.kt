package kr.android.zaihan.link.applink

import android.content.Context
import android.content.Intent
import kr.android.zaihan.link.LinkConstant

abstract class CustomeOutLink : OutLink (){

    override fun getScheme(): String {
        return LinkConstant.Custom.scheme
    }

    override fun getHost(): String {
        return "zaihan"
    }

    public override fun getPath(): String {
        return ""
    }

    override fun getLinkType(): String {
        return LinkConstant.LinkType.ZAIHAN
    }

    override fun getIntent(context: Context?): Intent? {
        val intent = Intent()
        val uri  = getUri()
        uri?.let {
            if (uri != null) {
                val params = uri.queryParameterNames
                if (params != null && params.size > 0) {
                    for (item in params) {
                        intent.putExtra(item, uri.getQueryParameter(item))
                    }
                }
            }
        }
        return intent
    }
}