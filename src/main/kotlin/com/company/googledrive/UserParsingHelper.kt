package com.company.googledrive

import com.company.googledrive.entity.User
import com.google.api.services.sheets.v4.model.ValueRange
import org.apache.commons.lang3.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UserParsingHelper : EntityParsingHelper<User>(
        "1J_rjMFYuTI8FBGqBU9EwN2hQCulDuL8K3it_eFOZfTA",
        "Участники",
        "A2:G",
        2) {

    override fun parseEntity(row: List<Any>, rowNum: Int): User {
        val oldNames = (row[4] as String).split("; ")
                .filter { StringUtils.isNotBlank(it) }
                .toMutableSet()
        val joinDate =
                if (row.size == 7)
                    try {
                        LocalDate.parse(row[6] as CharSequence, FORMATTER)
                    } catch (e: DateTimeParseException) {
                        LocalDate.parse(row[6] as CharSequence, SECOND_FORMATTER)
                    }
                else
                    null

        return User(spreadsheetId,
                sheetId,
                rowNum,
                row[0] as String,
                row[1] as String,
                row[2] as String,
                row[3] as String,
                oldNames,
                joinDate)
    }

    override fun toValueRange(entity: User): ValueRange {
        return ValueRange()
                .setRange(sheetId + "!A" + entity.row + ":E" + entity.row)
                .setValues(listOf(listOf(entity.accountId, entity.name, entity.vkPage, entity.discord,
                        entity.oldNames?.joinToString("; ") ?: "")))
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy")
        private val SECOND_FORMATTER = DateTimeFormatter.ofPattern("d.MM.yy")
    }
}
