package kr.android.zaihan.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.android.zaihan.network.vo.AppUpdateData
import kr.android.zaihan.network.vo.EmergencyNoticeData
import kr.android.zaihan.repository.IninProcessRepository
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.android.zaihan.network.vo.MomentRegistResult
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface  MainViewModelType {
    var input:MainViewModelInput
    var output:MainViewModelOutput
}
interface  MainViewModelInput {


}
interface  MainViewModelOutput {

}


class MainViewModel:ViewModel(),
                            MainViewModelType,
                            MainViewModelInput,
                            MainViewModelOutput
{
    override var input: MainViewModelInput = this
    override var output: MainViewModelOutput = this


}