package com.company

import com.company.lolapi.ApiRequestLimiter
import com.company.lolapi.LolApi
import com.company.lolclubsapi.LolClubsApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Closeable
import java.util.concurrent.TimeUnit

object ApiFabric : Closeable {
    private val lolApiRequestLimiter = ApiRequestLimiter.builder()
            .add(20, TimeUnit.SECONDS, 1)
            .add(100, TimeUnit.MINUTES, 2, false)
            .build()
    private val lolClubApiRequestLimiter = ApiRequestLimiter.builder()
            .add(20, TimeUnit.SECONDS, 1)
            .add(100, TimeUnit.MINUTES, 2, false)
            .build()

    //todo move url to resources
    val lolApi = getRetrofit("https://ru.api.riotgames.com/", lolApiRequestLimiter)
            .create(LolApi::class.java)

    //todo move url to resources
    val lolClubsApi = getRetrofit("https://clubs.ru.leagueoflegends.com/api/", lolClubApiRequestLimiter)
            .create(LolClubsApiInterface::class.java)

    override fun close() {
        lolApiRequestLimiter.close()
        lolClubApiRequestLimiter.close()
    }

    private fun getRetrofit(baseUrl: String, limiter: ApiRequestLimiter): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            limiter.acquire()
                            chain.proceed(chain.request().newBuilder()
                                    .addHeader("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                                    .addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3")
                                    .build())
                        }
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}