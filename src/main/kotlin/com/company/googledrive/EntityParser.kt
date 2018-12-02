package com.company.googledrive

import com.company.googledrive.entity.GDriveEntity
import com.google.api.services.sheets.v4.model.ValueRange

abstract class EntityParser<T: GDriveEntity>(
        val spreadsheetId: String,
        val sheetId: String,
        val range: String,
        val firstRow: Int) {

    abstract fun parseEntity(row: List<Any>, rowNum: Int): T
    abstract fun toValueRange(entity: T): ValueRange
}
