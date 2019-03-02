package com.company.lolclubsapi

data class SummonerResult(//val id: String,
                          val summoner: ClubSummoner,
                          val points: Int,
//                          val games: Int,
//                          val rank: String,
                          val joined: String,
                          val left: String,
                          val stage: Int,
                          val club: Int)