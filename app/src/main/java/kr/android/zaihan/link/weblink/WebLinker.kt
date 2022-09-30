package kr.android.zaihan.link.weblink

import android.net.Uri

object WebLinker {
    private val mList: MutableList<WebLink> = ArrayList<WebLink>()
    private var mCurrentLink: WebLink? = null

    init {
        //링크 작성
        add(OpenCameraLink()) // 앱스플라이어 별도 딥링크
        add(OpenAlbumLink()) // 앱스플라이어 별도 딥링크
    }

    fun add(linker: WebLink) {
        mList.add(linker)
    }

    fun find(uri: Uri): WebLink? {
        try {
            val scheme = if (uri.scheme == null) "" else uri.scheme!!
            val host = if (uri.host == null) "" else uri.host!!
            val path = if (uri.path == null) "" else uri.path!!
            val command = scheme + host + path
            for (link in mList) {
                val compString: String? = link.getCompUriString()
                if (compString == command) {
                    link.setUri(uri)
                    mCurrentLink = link
                    return link
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getWebLinkFromUri(uri: Uri): WebLink? {
        try {
            val scheme = if (uri.scheme == null) "" else uri.scheme!!
            val host = if (uri.host == null) "" else uri.host!!
            val path = if (uri.path == null) "" else uri.path!!
            val command = scheme + host + path
            for (link in mList) {
                if (link.getCompUriString().equals(command)) {
                    link.setUri(uri)
                    return link
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getCurrent(): WebLink? {
        return mCurrentLink
    }

    fun setCurrent(link: WebLink?) {
        mCurrentLink = link
    }

    fun removeCurrentLink() {
        if (mCurrentLink != null) {
            mCurrentLink!!.clear()
            mCurrentLink = null
        }
    }

    fun hasWebLink(): Boolean {
        return mCurrentLink != null
    }
}