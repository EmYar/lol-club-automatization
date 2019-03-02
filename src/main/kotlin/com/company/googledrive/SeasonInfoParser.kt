package com.company.googledrive

import com.company.googledrive.entity.SeasonInfo
import com.google.api.services.sheets.v4.model.ValueRange
import java.time.LocalDate
import java.time.Month
import java.time.Year

object SeasonInfoParser : MultiSheetsEntityParser<SeasonInfo>(
        "1k4d8_iuE61D4DJjwRD-_TUsQ-gutxSYxu47ADgjBjvk",
        "A2:C",
        2) {

    override fun parseEntities(sheetId: String, rows: List<List<Any>>): List<SeasonInfo> {
        val seasons = ArrayList<SeasonInfo>(rows.size / 3)
        @Suppress("UNCHECKED_CAST") val strRows = rows as List<List<String>>
        for (seasonRow in firstRow..rows.size step 3) {
            val year = Year.of(LocalDate.ofYearDay(sheetId.toInt(), 1).year)
            val month = Month.valueOf(strRows[seasonRow - firstRow][0].toUpperCase())
            val stages = ArrayList<Int>(3)
            seasons += SeasonInfo(spreadsheetId, sheetId, seasonRow, year, month, strRows[seasonRow - firstRow][1].toInt(), stages)
            for (stageRow in seasonRow..seasonRow + 2) {
                stages += strRows[stageRow - firstRow][2].toInt()
            }
        }
        return seasons
    }

    override fun toValueRange(entity: SeasonInfo): ValueRange {
        return ValueRange()
                .setRange(entity.sheetId + "!A" + entity.row + ":C" + (entity.row + 2))
                .setValues(listOf(listOf(entity.month.name, entity.id, entity.stages[0]),
                        listOf("", "", entity.stages[1]),
                        listOf("", "", entity.stages[2])))
    }
}