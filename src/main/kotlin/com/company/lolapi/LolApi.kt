package com.company.lolapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface LolApi {

    @GET("/lol/summoner/v3/summoners/by-account/{accountId}")
    fun getSummonerByAccountId(@Path("accountId") accountId: String,
                               @Header("X-Riot-Token") apiKey: String): Call<Summoner>

    @GET("/lol/summoner/v3/summoners/by-name/{summonerName}")
    fun getSummonerByName(@Path("summonerName") summonerName: String,
                          @Header("X-Riot-Token") apiKey: String): Call<Summoner>

    @GET("/lol/summoner/v3/summoners/{summonerId}")
    fun getSummonerById(@Path("summonerId") summonerId: String,
                        @Header("X-Riot-Token") apiKey: String): Call<Summoner>
}