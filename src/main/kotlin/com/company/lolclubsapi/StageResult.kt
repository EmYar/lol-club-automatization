package com.company.lolclubsapi

import netscape.javascript.JSObject

data class StageResult(val next: JSObject,
                       val page: Int,
                       val per_page: Int,
                       val results: List<SummonerResult>)