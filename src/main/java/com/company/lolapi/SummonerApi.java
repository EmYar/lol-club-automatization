package com.company.lolapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SummonerApi {
    @GET("/lol/summoner/v3/summoners/by-account/{accountId}")
    Call<Summoner> getSummonerByAccountId(@Path("accountId") long accountId);

    @GET("/lol/summoner/v3/summoners/by-name/{summonerName}")
    Call<Summoner> getSummonerByName(@Path("summonerName") String summonerName);

    @GET("/lol/summoner/v3/summoners/{summonerId}")
    Call<Summoner> getSummonerById(@Path("summonerId") long summonerId);
}
