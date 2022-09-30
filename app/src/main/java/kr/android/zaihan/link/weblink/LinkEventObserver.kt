package kr.android.zaihan.link.weblink

import io.reactivex.rxjava3.subjects.PublishSubject

class LinkEventObserver {
    companion object {
        var hashMap = HashMap<String, PublishSubject<Any>?>()
        fun sendEvent(event:String, value:Any = Unit) {
            synchronized(this) {
                if ( hashMap.contains(event) ) {
                    hashMap.get(event)?.onNext(value)
                }
            }
        }

        fun <T> receiveEvent(event: String):PublishSubject<T> {
            synchronized(this) {
                if (!hashMap.contains(event)) {
                    hashMap.set(event, PublishSubject.create())
                }
                return hashMap.get(event)!! as PublishSubject<T>
            }
        }
    }
}