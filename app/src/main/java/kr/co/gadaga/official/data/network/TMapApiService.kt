package kr.co.gadaga.official.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import kr.co.gadaga.official.data.network.response.TMapResponse
import kr.co.gadaga.official.internal.API_KEY
import kr.co.gadaga.official.internal.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TMapApiService {

    @GET("pedestrian")
    fun getPedestrianPath(
        @Query("appKey") appKey: String = API_KEY,
        @Query("version") version: Int = 1,
        @Query("startX") startX: Double = 126.973851,
        @Query("startY") startY: Double = 37.565092,
        @Query("endX") endX: Double = 126.972771,
        @Query("endY") endY: Double = 37.559343,
        @Query("startName") startName: String = "%22%22", // empty
        @Query("endName") endName: String = "%22%22",
        @Query("passList") passList: String = "126.984941,37.562218" // 경유지 126.92774822,37.55395475_126.92577620,37.55337145_
    ): Deferred<TMapResponse>

    companion object {

        operator fun invoke(): TMapApiService {
            val okHttpClient = OkHttpClient.Builder().build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TMapApiService::class.java)
        }
    }
}