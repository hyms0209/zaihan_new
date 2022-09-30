package kr.android.zaihan.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import io.reactivex.rxjava3.core.Observable
import kr.android.zaihan.MainActivity
import kr.android.zaihan.databinding.ActivitySplashBinding
import kr.android.zaihan.network.vo.AppUpdateData
import kr.android.zaihan.network.vo.EmergencyNoticeData
import kr.android.zaihan.ui.BaseActivity
import kr.android.zaihan.ui.SplashViewModel
import kr.android.zaihan.ui.dialog.ConfirmDialog
import kr.android.zaihan.ui.dialog.EmergencyDialog
import kr.android.zaihan.ui.dialog.OnDialogClickListener
import kr.android.zaihan.ui.dialog.PermissionDialog
import java.util.concurrent.TimeUnit

class SplashActivity:BaseActivity() {

    val viewModel by viewModels<SplashViewModel>()

    lateinit var binding : ActivitySplashBinding

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(LayoutInflater.from(this))
        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        }

        setContentView(binding.root)

        bindInitViewModel()

        getScreenSize()

        Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .subscribe {
                        viewModel.start()
                    }
    }

    fun getScreenSize() {
        val outMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = this@SplashActivity.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this@SplashActivity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        setImage(outMetrics.widthPixels, outMetrics.heightPixels)
    }

    fun setImage(width:Int, height:Int) {
        var resId = resources.getIdentifier("@drawable/splash_1440x${height}", "drwable", "kr.android.zaihan")
        if ( resId == 0 ) {
            if ( height.toFloat()/width.toFloat() > 2.2  ) {
                resId = resources.getIdentifier("@drawable/splash_1440x3200", "drwable", "kr.android.zaihan")
                binding.bgImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            if ( height.toFloat()/width.toFloat() > 1.5 ) {
                resId = resources.getIdentifier("@drawable/splash_1440x2560", "drwable", "kr.android.zaihan")
                binding.bgImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            else {
                resId = resources.getIdentifier("@drawable/splash_1440x2560", "drwable", "kr.android.zaihan")
                binding.bgImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
        binding.bgImage.setImageResource(resId)
    }

    fun bindInitViewModel() {
        // 긴급공지 체크 팝업
        viewModel.output.checkEmergency.observe(this) {
            openEmergencyDialog(it)
        }

        // 앱업데이트 체크 팝업
        viewModel.output.checkAppUpdate.observe(this) {
            openAppUpdateDialog(it)
        }

        // 퍼미션 체크 팝업
        viewModel.output.checkPermission.observe(this) {
            openPermissionDialog()
        }

        // 메인화면 이동
        viewModel.output.goMain.observe(this) {
            this.startActivity(Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    fun openEmergencyDialog(data: EmergencyNoticeData) {
        var dialog = EmergencyDialog()
        dialog.setData(data)
        dialog.setOnClickListener(object: OnDialogClickListener {
            override fun onCancelClickListener() {
                viewModel.input.checkEmergency(false)
                dialog.dismiss()
            }

            override fun onConfirmClickListener() {
                Log.d("EmergencyDialog", "Confirm Click")
                viewModel.input.checkEmergency(true)
                dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, "Emergency")
    }

    fun openAppUpdateDialog(data: AppUpdateData) {
        var dialog = ConfirmDialog()
        dialog.title = data.title
        dialog.message = data.message
        dialog.cancelTitle = if (data.status == "FORCE") "종료" else "취소"
        dialog.confirmTitle = if (data.status == "FORCE") "업데이트" else "업데이트"

        dialog.setOnClickListener(object: OnDialogClickListener {
            override fun onCancelClickListener() {
                if ( data.status == "FORCE" ) {
                    forceFinish()
                } else {
                    viewModel.input.checkAppUpdate(false)
                }
                dialog.dismiss()
            }

            override fun onConfirmClickListener() {
                Log.d("EmergencyDialog", "Confirm Click")
                // 앱업데이트 이동
                viewModel.input.checkAppUpdate(true)
                dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, "AppUpdateDialog")
    }

    fun openPermissionDialog() {
        var dialog = PermissionDialog()
        dialog.setOnClickListener(object: OnDialogClickListener{
            override fun onCancelClickListener() {

            }
            override fun onConfirmClickListener() {
                Log.d("EmergencyDialog", "Confirm Click")
                viewModel.input.checkPermission(true)
                supportFragmentManager.beginTransaction().remove(dialog).commitNow()
            }
        })
        supportFragmentManager.beginTransaction().add(binding.flContainer.id, dialog).commitNow()
    }
}