package com.company.lolclubsapi

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JClass(date: String) {
    var date: LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    fun setDate(date: String) {
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
