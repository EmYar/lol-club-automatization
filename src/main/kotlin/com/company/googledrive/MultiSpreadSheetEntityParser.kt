package com.company.googledrive

import com.company.googledrive.entity.MultiSpreadSheetEntity
import com.google.api.services.sheets.v4.model.ValueRange

abstract class MultiSpreadSheetEntityParser<T : MultiSpreadSheetEntity>(
        val range: String,
        val firstRow: Int) {

    abstract fun parseEntities(spreadsheetId: String, sheetId: String, rows: List<List<Any>>): List<T>

    fun toValueRange(entities: Collection<T>): Map<String, List<ValueRange>> {
        return entities.groupBy(MultiSpreadSheetEntity::spreadsheetId, this::toValueRange)
    }

    protected abstract fun toValueRange(entity: T) : ValueRange;
}