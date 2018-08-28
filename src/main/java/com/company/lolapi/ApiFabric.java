package com.company.lolapi;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiFabric {
    private static final String BASE_URL = "https://ru.api.riotgames.com/";

    private static ApiFabric ourInstance;
    private final ApiRequestLimiter limiter;
    private SummonerApi summonerApi;

    private ApiFabric() {
        limiter = ApiRequestLimiter.builder()
                .add(20, TimeUnit.SECONDS, 1)
                .add(100, TimeUnit.MINUTES, 2, false)
                .build();
    }

    public static ApiFabric getInstance() {
        if (ourInstance == null) {
            ourInstance = new ApiFabric();
        }
        return ourInstance;
    }

    public SummonerApi getSummonerApi() {
        if (summonerApi == null) {
            summonerApi = getRetrofit().create(SummonerApi.class);
        }
        return summonerApi;
    }

    private Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                            .addHeader("X-Riot-Token", Resources.toString(Resources.getResource("lol_api_key"), Charsets.UTF_8))
                            .addHeader("Accept-Language", "ru,en-US;q=0.7,en;q=0.3")
                            .build();

                    limiter.acquire();

                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
