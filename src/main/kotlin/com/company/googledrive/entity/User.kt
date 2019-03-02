package com.company.googledrive.entity

import org.apache.commons.lang3.StringUtils
import java.time.LocalDate

class User(spreadsheetId: String,
           sheetName: String,
           row: Int,
           var accountId: String,
           name: String,
           var vkPage: String,
           var discord: String,
           var oldNames: MutableSet<String>?,
           role: String,
           var joinDate: LocalDate?)
    : GDriveEntity(spreadsheetId, sheetName, row) {

    var name: String = name
        set(value) {
            if (StringUtils.isNotBlank(field)) {
                if (oldNames == null)
                    oldNames = mutableSetOf()
                oldNames!! += name
            }
            field = value
        }

    val role = Role.fromCaption(role)

    fun getNames() : List<String> {
        return if (oldNames == null)
            listOf(name)
        else {
            val list = oldNames!!.toMutableList()
            list.add(name)
            list
        }
    }
}
