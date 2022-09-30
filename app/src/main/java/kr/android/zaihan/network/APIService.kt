package kr.co.thecheck.android.manager.data.network

import io.reactivex.rxjava3.core.Single
import kr.android.zaihan.network.ResAuthInfo
import kr.android.zaihan.network.SessionManager
import kr.android.zaihan.network.vo.ResAppUpdate
import kr.android.zaihan.network.vo.ResEmergencyNotice
import kr.android.zaihan.network.vo.ResMomentRegist
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    /** 긴급공지 **/
    @Headers("accept:application/json", "content-type:application/json")
    @GET("management/emergency-system-notice")
    fun requestEmergencyNotice(): Single<ResEmergencyNotice>

    /** 앱업데이트 **/
    @Headers("accept:application/json", "content-type:application/json")
    @GET("app/versions/latest")
    fun requestAppUpdate(
        @Query("os") os:String?,
        @Query("version") version:String?
    ): Single<ResAppUpdate>

    /** 액세스 토큰 발급 **/
    @POST("/api/v1/oauth/token")
    fun asyncAccessToken(): Call<ResAuthInfo>

    /** 로그인 **/
    @Headers("accept:application/json", "content-type:application/json")
    @POST("/api/v1/oauth/authorize")
    fun requestLogin(): Single<ResAuthInfo>

    /** 파일 전송 **/
    // 모멘트 등록 + 동영상
    @Multipart
    @POST("board/posts/regist")
    open fun registMoment(
        @Part video: MultipartBody.Part?,
        @Part images: MultipartBody.Part?,
        @Part("menuId") menuId: RequestBody?,
        @Part("categoryId") categoryId: RequestBody?,
        @Part("subCategoryId") subCategoryId: RequestBody?,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
    ): Single<ResMomentRegist>

    // 모멘트 이미지 업로드
    @Multipart
    @POST("v1/moments/{moment_id}/images/")
    fun uploadMomentImages(
        @Path("moment_id") moment_id: String?,
        @Part image: Part?,
        @Part("ordering") ordering: RequestBody?
    ): Single<ResMomentRegist>

//    /** 로그인 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @POST("/api/v1/oauth/authorize")
//    fun requestLogin(@Body obj: ReqLogin): Single<ResAuthInfo>
//
//    /** 액세스 토큰 발급 **/
//    @POST("/api/v1/oauth/token")
//    fun requestAccessToken(): Single<ResAuthInfo>
//
//    /** 로그인 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @POST("/api/v1/oauth/authorize")
//    fun asyncLogin(@Body obj: ReqLogin): Call<ResAuthInfo>
//
//
//    /** 액세스 토큰 발급 **/
//    @POST("/api/v1/oauth/token")
//    fun asyncAccessToken(): Call<ResAuthInfo>
//
//    /** 맵리스트 V2 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v3/store")
//    fun requestMapList(@Query("memberType") memberType: String ,
//                       @Query("manageType") manageType: String ,
//                       @Query("ltLat") ltLat: Double ,
//                       @Query("ltLng") ltLng: Double ,
//                       @Query("rbLat") rbLat: Double ,
//                       @Query("rbLng") rbLng: Double ): Single<ResMarkerList>
//
//    /** 가맹점 상세 정보 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v3/store/{strCd}")
//    fun requestStoreDetail(@Path("strCd") strCd: String): Single<ResStoreDetail>
//
//    /** 가맹점 리스트 정보 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v3/store/page")
//    fun requestStoreList(@Query("memberType") memberType: String ,
//                         @Query("manageType") manageType: String ,
//                         @Query("lat") lat: Double ,
//                         @Query("lng") lng: Double ,
//                         @Query("ltLat") ltLat: Double ,
//                         @Query("ltLng") ltLng: Double ,
//                         @Query("rbLat") rbLat: Double ,
//                         @Query("rbLng") rbLng: Double ,
//                         @Query("page") page: Int): Single<ResStoreList>
//
//    /** 방문기록 등록 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @POST("/api/v3/vstLogs")
//    fun requestRegistVisitHistory(@Body obj: ReqRegistVisitHistory): Single<ResRegistVisitHistory>
//
//    /** 방문유형 정보 취득 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/contents/codes")
//    fun requestVisitTypes(@Query("codeGroup") codeGroup : String): Single<ResVisitTypes>
//
//    /** 지역정보 검색 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v1/map")
//    fun requestLocalSearchList(@Query("query") query: String,
//                               @Query("page") page: Int,
//                               @Query("pageSize") pageSize: Int): Single<ResLocalSearchList>
//
//    /** 좌표정보 주소정보로 변환 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v1/map/coord2Address")
//    fun requestAddress(@Query("x") x: Double,
//                       @Query("y") y: Double): Single<ResAddress>
//
//    /** 위치정보 동의 이용약관 상세 정보 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v1/terms/T00001")
//    fun requestLocationTermAgreeDetail(): Single<ResLocatinoTermAgreeDetail>
//
//    /** 출/퇴근 정보 전송 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @POST("/api/v1/attendance")
//    fun requestAttendanceUpdate(@Body obj: ReqAttendance): Single<ResAttendanceUpdate>
//
//    /** 출/퇴근 상태 정보 취득 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/api/v1/attendance/{username}")
//    fun requestAttendanceState(@Path("username") username: String): Single<ResAttendanceStatus>
//
//    /** 좌표정보 주소정보로 변환 **/
//    @Headers("accept:application/json", "content-type:application/json")
//    @GET("/contents/v1/appVersion")
//    fun requestUpdateInfo(@Query("packageName") packageName: String,
//                          @Query("osType") osType: String,
//                          @Query("version") version: String): Single<ResUpdateInfo>
}