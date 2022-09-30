package kr.android.zaihan.link.applink

import android.content.Context
import android.content.Intent
import kr.android.zaihan.link.LinkConstant

class WebUrlOutLink : CustomeOutLink() {

    companion object {
        var LOAD_URL = "url"
    }

    override fun getPath(): String {
        return "/move/link"
    }

    override fun getLinkType(): String {
        return LinkConstant.LinkType.ZAIHAN
    }

    override fun getIntent(context: Context?): Intent? {
        var intent = super.getIntent(context)
        return intent
    }
}