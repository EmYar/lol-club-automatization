package com.company.googledrive

import com.company.googledrive.entity.User
import com.google.api.services.sheets.v4.model.ValueRange
import org.apache.commons.lang3.StringUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UserParser : EntityParser<User>(
        "1J_rjMFYuTI8FBGqBU9EwN2hQCulDuL8K3it_eFOZfTA", //todo emelyanov move to resources
        "Участники", //todo emelyanov move to resources
        "A2:G", //todo emelyanov move to resources
        2) { //todo emelyanov move to resources

    override fun parseEntity(row: List<Any>, rowNum: Int): User {
        val oldNames = (row[4] as String).split("; ")
                .filter { StringUtils.isNotBlank(it) }
                .toMutableSet()
        val joinDate =
                if (row.size == 7) parseDate(row[6] as CharSequence)
                else null

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

    private fun parseDate(charSequence: CharSequence): LocalDate? {
        for (formatter in FORMATTERS) try {
            return LocalDate.parse(charSequence, formatter)
        } catch (e: DateTimeParseException) {
        }
        return null
    }

    companion object {
        private val FORMATTERS = listOf(
                DateTimeFormatter.ofPattern("d.M.yy"),
                DateTimeFormatter.ofPattern("dd.M.yy"),
                DateTimeFormatter.ofPattern("d.MM.yy"),
                DateTimeFormatter.ofPattern("dd.MM.yy"),
                DateTimeFormatter.ofPattern("d.M.yyyy"),
                DateTimeFormatter.ofPattern("dd.M.yyyy"),
                DateTimeFormatter.ofPattern("d.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
}
