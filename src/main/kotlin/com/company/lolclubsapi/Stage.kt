package com.company.lolclubsapi

data class Stage(val id: Int,
                 val season: Int,
                 val start_date: String,
                 val end_date: String,
                 val is_open: Boolean,
                 val is_closed: Boolean)