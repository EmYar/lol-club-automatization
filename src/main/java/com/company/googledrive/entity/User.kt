package com.company.googledrive.entity

import java.time.LocalDate

class User(spreadsheetId: String,
           sheetName: String,
           row: Int,
           var accountId: String,
           name: String,
           var vkPage: String,
           var discord: String,
           var oldNames: MutableSet<String>?,
           var joinDate: LocalDate?)
    : GDriveEntity(spreadsheetId, sheetName, row) {

    var name: String = name
        set(value) {
            if (oldNames == null)
                oldNames = mutableSetOf()
            oldNames!! += name
            field = value
        }

    fun getRole(): Role {
        throw UnsupportedOperationException();
    }

    fun getAbsences(): List<Absence> {
        throw UnsupportedOperationException();
    }
}
