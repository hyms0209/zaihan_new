package kr.android.zaihan

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

class AppApplication:Application() {

    companion object {
        var ctx: AppApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        Firebase.messaging.token.addOnCompleteListener {
            if ( it.isSuccessful ) {
                Log.d("FBToken ", "${it.result}")
                Toast.makeText(baseContext, it.result, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}