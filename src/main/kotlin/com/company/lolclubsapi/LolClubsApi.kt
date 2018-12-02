package com.company.lolclubsapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface LolClubsApi {

    @GET("contest/season/{season}/stages/{stage}/summoners/?per_page=100&page=1")
    fun getStageScores(@Path("season") season: Int,
                       @Path("stage") stage: Int,
                       @Header("Cookie") pvpNetTokenRu: String): Call<StageResult>

}