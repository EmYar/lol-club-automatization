package com.company;

import com.company.lolapi.ApiFabric;
import com.company.lolapi.Summoner;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
//        com.company.ScoreChecker scoreChecker = new com.company.ScoreChecker();
//        scoreChecker.getActiveUsers();
//        try {
//            System.out.println(ScoresParser.getInstance().parse());
//        } catch (ParsingException e) {
//            e.printStackTrace();
//        }

//        int i = 1;
//        ApiRequestLimiter limiter = ApiRequestLimiter.builder()
//                .add(1, TimeUnit.SECONDS, 1)
//                .add(4, TimeUnit.SECONDS, 5, false)
//                .build();
//        while (true) {
//            limiter.acquire();
//            System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + String.valueOf(i++));
//        }

        try {
            Summoner summoner = ApiFabric.getInstance().getSummonerApi().getSummonerByName("Elesey").execute().body();
            summoner.getAccountId();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
