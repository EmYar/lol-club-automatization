package com.company.googledrive

import com.company.googledrive.entity.MultiSheetEntity
import com.google.api.services.sheets.v4.model.ValueRange

abstract class MultiSheetsEntityParser<T : MultiSheetEntity>(val spreadsheetId: String,
                                                             val range: String,
                                                             val firstRow: Int) {

    abstract fun parseEntities(sheetId: String, rows: List<List<Any>>): List<T>
    abstract fun toValueRange(entity: T): ValueRange
}