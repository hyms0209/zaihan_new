package kr.android.zaihan.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.android.zaihan.network.vo.AppUpdateData
import kr.android.zaihan.network.vo.EmergencyNoticeData
import kr.android.zaihan.repository.IninProcessRepository
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.android.zaihan.BuildConfig
import kr.android.zaihan.network.vo.MomentRegistResult
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface  SplashViewModelType {
    var input:SplashViewModelInput
    var output:SplashViewModelOutput
}

interface  SplashViewModelInput {
    fun checkEmergency(result:Boolean)
    fun checkAppUpdate(result:Boolean)
    fun checkPermission(result:Boolean)

}

interface  SplashViewModelOutput {
    val checkEmergency:LiveData<EmergencyNoticeData>
    val checkAppUpdate:LiveData<AppUpdateData>
    val checkPermission:LiveData<Boolean>
    val goMain:LiveData<Boolean>
}

enum class JobStep(  // 메인이동
    var requestCode: Int
) {

    Emergency(2000),    // 긴급공지
    Update(2001),       // 업데이트 체크
    Permission(2003),   // 권한동의 ( 백그라운드 위치 권한 필수 적용)
    Main(2006);         // 메인화면 이동

    var bundle: Bundle? = null
}

class SplashViewModel:ViewModel(),
                                    SplashViewModelType,
                                    SplashViewModelInput,
                                    SplashViewModelOutput
{
    override var input: SplashViewModelInput = this
    override var output: SplashViewModelOutput = this

    private var _checkEmergency  =  MutableLiveData<EmergencyNoticeData>()
    override val checkEmergency:LiveData<EmergencyNoticeData>
        get() = _checkEmergency

    private var _checkAppUpdate  =  MutableLiveData<AppUpdateData>()
    override val checkAppUpdate:LiveData<AppUpdateData>
        get() = _checkAppUpdate

    private var _checkPermission  =  MutableLiveData<Boolean>()
    override val checkPermission:LiveData<Boolean>
        get() = _checkPermission

    private var _goMain  =  MutableLiveData<Boolean>()
    override val goMain:LiveData<Boolean>
        get() = _goMain

    val repository = IninProcessRepository()

    var task:ArrayList<JobStep> = arrayListOf()

    init {
        task.add(JobStep.Emergency)
        task.add(JobStep.Update)
        task.add(JobStep.Permission)
        task.add(JobStep.Main)
    }

    /***
     * 타스크 단위 잡스탭 시작
     */
    fun start() {
        onNext()
    }

    /***
     *  다음 잡스탭 진행
     */
    fun onNext() {
        if (task.size > 0) {
            val step = task.removeAt(0)
            when (step) {
                JobStep.Emergency -> goEmergency()
                JobStep.Update -> goUpdate()
                JobStep.Permission -> goPermission()
                JobStep.Main -> goMain()
            }
        }
    }

    fun goEmergency() {
        repository.requestEmergencyNotice()
            .subscribeOn(Schedulers.io())
            .subscribe( {
                _checkEmergency.postValue(it)
//                if ( it.isShow == "Y") {
//                    _checkEmergency.postValue(it)
//                } else {
//                    onNext()
//                }

            }, {

            })
    }

    fun goUpdate() {
        repository.requestAppUpdate("AOS", "0.8")
            .subscribeOn(Schedulers.io())
            .subscribe({
                var currentVersion = BuildConfig.VERSION_NAME
                var compAppVersion =  currentVersion.removeSurrounding(".")
                var compLatestVersion = it.version.removeSurrounding(".")
                // 업데이트
                if ( compLatestVersion > compAppVersion) {
                    if ( it.status == "FORCE" ) {
                        it.title = "앱 업데이트"
                        it.message = "최신버젼이 ${it.version}입니다.\n재한 앱을 사용하기 위해서는 반드시 업데이트가 필요합니다."
                    } else {
                        it.title = "앱 업데이트"
                        it.message = "최신버젼이 ${it.version}입니다.\n재한 앱을 최신버젼으로 업데이트 하시겠습니까?"
                    }
                    _checkAppUpdate.postValue(it)
                } else {
                    onNext()
                }
            }, {

            })
    }

    fun goPermission() {
        _checkPermission.postValue(true)
    }

    fun goMain() {
       _goMain.postValue(true)
    }

    override fun checkEmergency(result: Boolean) {
        if ( result ) { onNext() }
    }

    override fun checkAppUpdate(result: Boolean) {
        if ( result ) { onNext() }
    }

    override fun checkPermission(result: Boolean) {
        if ( result ) { onNext() }
    }
}