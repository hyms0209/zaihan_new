package kr.android.zaihan.repository

import kr.android.zaihan.network.APIManager
import kr.android.zaihan.network.vo.AppUpdateData
import kr.android.zaihan.network.vo.EmergencyNoticeData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.android.zaihan.network.vo.MomentRegistResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MomentProcessRepository {

    fun requestMomentRegist(video: MultipartBody.Part? = null,
                            images: MultipartBody.Part? = null,
                            menuId: RequestBody?,
                            categoryId: RequestBody?,
                            subCategoryId: RequestBody?,
                            title: RequestBody?,
                            description: RequestBody?):Observable<MomentRegistResult> {
        return Observable.create{ observer ->
            APIManager.requestMomentRegist(video,
                                           images,
                                           menuId,
                                           categoryId,
                                           subCategoryId,
                                           title,
                                           description)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.code == "0000") {
                        observer.onNext(it.data)
                    }
                    observer.onComplete()
                },{
                    observer.onComplete()
                })
        }
    }
}