package com.company.lolapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SummonerApi {
    @GET("/lol/summoner/v3/summoners/by-account/{accountId}")
    fun getSummonerByAccountId(@Path("accountId") accountId: String): Call<Summoner>

    @GET("/lol/summoner/v3/summoners/by-name/{summonerName}")
    fun getSummonerByName(@Path("summonerName") summonerName: String): Call<Summoner>

    @GET("/lol/summoner/v3/summoners/{summonerId}")
    fun getSummonerById(@Path("summonerId") summonerId: String): Call<Summoner>
}
