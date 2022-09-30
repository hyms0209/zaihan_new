package kr.android.zaihan.network

import android.os.Looper
import android.util.Log
import kr.android.zaihan.network.vo.ResAppUpdate
import kr.android.zaihan.network.vo.ResEmergencyNotice
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kr.android.zaihan.network.vo.ResMomentRegist
import kr.co.thecheck.android.manager.data.network.APIService
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part


object APIManager {
    //=============== 상용서버 ===============//

    /** 인증서버 **/
    var URL        = ""
    get() {
      return "http://3.35.18.10:4000/"
    }

    var createRetrofit:APIService = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create() )
        .client(
            OkHttpClient.Builder()
                .addInterceptor(ManagerInterceptor())
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build().create(APIService::class.java)

    var createAuthRetrofit:APIService = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor (AccessTokenInterceptor())

                .build()
        )
        .build().create(APIService::class.java)


    class ManagerInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {

            Log.d("_lmh", "인터셉터 시작")
            var request = chain.request().newBuilder().
                                addHeader("auth_code", SessionManager.authCode)
                                .addHeader("Bearer", SessionManager.accessToken)
                                .build()

            var response = chain.proceed(request)

            val originalRequest = chain.request()
            when(response.code) {
                401 -> {
                    //var authInfo = createAuthRetrofit.asyncAccessToken().execute().body() as ResAuthInfo
                    //SessionManager.setAuthInfo(authInfo)
                }
                403 -> {
                    var authInfo = createAuthRetrofit.asyncAccessToken().execute().body() as ResAuthInfo
                    SessionManager.setAuthInfo(authInfo)
                }
                409 -> {
                    runBlocking {
                       var data = GlobalScope.async {

                       }
                    }
                }
                410 -> {
                    runBlocking {
                        android.os.Handler(Looper.getMainLooper()).post {

                        }
                    }
                }
                else -> {
                    return response
                }
            }

            var newRequest =  originalRequest.newBuilder()
                                        //.addHeader("auth_code", SessionManager.authCode)
                                        .addHeader("content-type","multipart/form-data")
                                        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhbGljZSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNjYyMjkwOTE0LjUxMiwiZXhwIjoxNjYyMjk0NTE0fQ.aJzQTv09K_XJHyMrjtqdp-xj0zG2cJRarT5nOIoIT00")
                                        .build()

            var finalResponse = chain.proceed(newRequest)
            return finalResponse
        }
    }

    class AccessTokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            var newRequest =  chain.request().newBuilder()
                .addHeader("grant_type", "refresh_token")
                .addHeader("auth_code", SessionManager.authCode)
                .addHeader("refresh_token", SessionManager.refreshToken)
                .build()

            Log.d("_lmh", "AccessTokenInterceptor ")
            return chain.proceed(newRequest)
        }
    }

    fun requestEmergency() : Single<ResEmergencyNotice> {
        return createRetrofit.requestEmergencyNotice()
    }

    fun requestAppUpdate(os:String, version:String) : Single<ResAppUpdate> {
        return createRetrofit.requestAppUpdate(os, version)
    }

    fun requestMomentRegist(
                            video: MultipartBody.Part?,
                            images: MultipartBody.Part? = null,
                            menuId: RequestBody?,
                            categoryId: RequestBody?,
                            subCategoryId: RequestBody?,
                            title: RequestBody?,
                            description: RequestBody?
    ) : Single<ResMomentRegist> {
        return createRetrofit.registMoment( video,
                                            images,
                                            menuId,
                                            categoryId,
                                            subCategoryId,
                                            title,
                                            description
                                           )
    }

    fun requestToken() : Single<ResAuthInfo> {
        return createRetrofit.requestLogin()
    }
}

