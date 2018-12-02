package com.company.lolclubsapi

data class StageResult(
        val next: String = "",
        val page: String = "",
        val perPage: String = "",
        val results: List<SummonerResult> = emptyList())