package com.company.lolclubsapi

data class SummonerResult(
        val id: String = "",
        val summoner: ClubSummoner = ClubSummoner(),
        val points: String = "",
        val games: String = "",
        val rank: String = "",
        val joined: String = "",
        val left: String = "",
        val stage: String = "",
        val club: String = "")