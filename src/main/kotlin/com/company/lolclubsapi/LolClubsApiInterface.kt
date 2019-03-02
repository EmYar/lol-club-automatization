package com.company.lolclubsapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface LolClubsApiInterface {

    @GET("contest/season/{season}/stages/{stage}/summoners/?per_page=100&page=1")
    fun getStageScores(@Path("season") season: Int,
                       @Path("stage") stage: Int,
                       @Header("Cookie") cookies: String): Call<StageResult>

    @GET("contest/season/current")
    fun getCurrentSeason(@Header("Cookie") cookies: String) : Call<CurrentSeason>
}