package kr.android.zaihan.link

class LinkConstant {
    companion object{
        var IntentLinkKey = "landingurl"
    }

    object LinkType {
        var ZAIHAN = "zaihan"
        var WEBTOAPP = "webtoapp"
    }

    // 커스텀 스키마
    object Custom {
        const val scheme = "zaihan"
        val url: String
            get() = scheme
    }

    // 웹투앱
    object WebToApp {
        const val scheme = "zaihanif"
        val url: String
            get() = scheme
    }
}