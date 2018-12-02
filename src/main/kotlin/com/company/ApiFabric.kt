package com.company

import com.company.lolapi.ApiRequestLimiter
import com.company.lolapi.LolApi
import com.company.lolclubsapi.LolClubsApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFabric : AutoCloseable {

    private val lolApiRequestLimiter = ApiRequestLimiter.builder()
            .add(20, TimeUnit.SECONDS, 1)
            .add(100, TimeUnit.MINUTES, 2, false)
            .build()
    private val lolClubApiRequestLimiter = ApiRequestLimiter.builder()
            .add(20, TimeUnit.SECONDS, 1)
            .add(100, TimeUnit.MINUTES, 2, false)
            .build()

    @Suppress("UnstableApiUsage")
    fun getLolApi(): LolApi {
        //todo emelyanov url to resources
        return getRetrofit("https://ru.api.riotgames.com/", lolApiRequestLimiter)
                .create(LolApi::class.java)
    }

    fun getLolClubApi(): LolClubsApi {
        return getRetrofit("https://clubs.ru.leagueoflegends.com/api/", lolClubApiRequestLimiter)
                .create(LolClubsApi::class.java)
    }

    override fun close() {
        lolClubApiRequestLimiter.close()
    }

    private fun getRetrofit(baseUrl: String, limiter: ApiRequestLimiter): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val requestBuilder = chain.request().newBuilder()
                            requestBuilder.addHeader("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                            requestBuilder.addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3")
                            limiter.acquire()
                            chain.proceed(requestBuilder.build())
                        }
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}