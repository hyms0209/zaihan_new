package kr.android.zaihan.link.applink

import android.net.Uri
import android.util.Log
import java.util.*

object OutLinker {

    private val mList: MutableList<OutLink> = ArrayList<OutLink>()
    private var mCurrentLink: OutLink? = null

    /***
     * 초기화 처리
     */
    init {
        //링크 규격 추가
        add(WebUrlOutLink())       // 웹 화면 이동
    }

    /***
     * 아웃링크 규격 추가
     */
    fun add(linker: OutLink) {
        mList.add(linker)
    }

    /***
     * Uri로 부터 일치하는 아웃링크 취득(일치하는 아웃링크가 있는 경우 커런트 아웃링크에 저장)
     *
     * @param uri : 외부로 부터 받은 아웃링크 정보
     * @return 일치하는 아웃링크 정보
     *
     */
    fun find(uri: Uri): OutLink? {
        try {
            val scheme = if (uri.scheme == null) "" else uri.scheme!!
            val host = if (uri.host == null) "" else uri.host!!
            val path = if (uri.path == null) "" else uri.path!!
            val command = scheme + host + path
            for (link in mList) {
                val compString: String = link.getCompUriString()
                Log.d("OutLinker", "link : $compString")
                Log.d("OutLinker", "command : $command")
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

    /***
     * Uri로 부터 일치하는 아웃링크 취득(단, 현재 아웃링크는 저장하지 않음)
     */
    fun getOutLinkFromUri(uri: Uri): OutLink? {
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

    /***
     * 현재 아웃링크
     */
    fun getCurrent(): OutLink? {
        return mCurrentLink
    }

    /***
     * 현재 아웃링크 저장
     */
    fun setCurrent(link: OutLink?) {
        mCurrentLink = link
    }

    /***
     * 현재 아웃링크의 데이터 정보 삭제
     */
    fun removeCurrentLink() {
        mCurrentLink?.let {
            it.clear()
            mCurrentLink = null
        }
    }

    /***
     * 현재 아웃링링크 보유 여부
     */
    fun hasOutLink(): Boolean {
        return mCurrentLink != null
    }
}