package com.company.lolclubsapi

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrentSeason(val id: Int,
                    val start_date: String,
                    val end_date: String,
                    val is_open: Boolean,
                    val is_closed: Boolean,
                    val stages: List<Stage>) {

    companion object {
        val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    fun getLocaleStartDate() : LocalDate {
        return LocalDate.parse(start_date, pattern)
    }

    fun getLocaleEndDate() : LocalDate {
        return LocalDate.parse(end_date, pattern)
    }
}