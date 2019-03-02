package com.company.googledrive

import com.company.googledrive.entity.FarmResult
import com.google.api.services.sheets.v4.model.ValueRange

object FarmResultParser : MultiSpreadSheetEntityParser<FarmResult>("A2:E", 2) {

    override fun parseEntities(spreadsheetId: String, sheetId: String, rows: List<List<Any>>): List<FarmResult> {
        return rows.mapIndexed { rowNumber, row -> parseRow(spreadsheetId, sheetId, rowNumber + firstRow, row) }
    }

    override fun toValueRange(entity: FarmResult): ValueRange {
        return ValueRange()
                .setRange("${entity.sheetId}!A${entity.rowNumber}:E${entity.rowNumber}")
                .setValues(listOf(
                        listOf(entity.userId, entity.userName, entity.stages[0], entity.stages[1], entity.stages[2])))
    }

    private fun parseRow(spreadsheetId: String, sheetId: String, inDocRowNumber: Int, row: List<Any>): FarmResult {
        @Suppress("UNCHECKED_CAST") val stringRow = row as List<String>
        return FarmResult(spreadsheetId, sheetId, inDocRowNumber,
                stringRow[0],
                stringRow[1],
                listOf(stringRow[2].toInt(), stringRow[3].toInt(), stringRow[4].toInt()))
    }

    fun createEntity(entities: Collection<FarmResult>,
                     spreadsheetId: String,
                     sheetName: String,
                     userId: String,
                     userName: String,
                     stages: List<Int>) : FarmResult{
        return FarmResult(
                spreadsheetId,
                sheetName,
                entities.size + firstRow,
                userId,
                userName,
                stages)
    }
}