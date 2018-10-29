package com.company.googledrive.entity

import java.time.LocalDate

class User(spreadsheetId: String,
           sheetName: String,
           row: Int,
           var accountId: String?,
           var name: String?,
           var vkPage: String?,
           var discord: String?,
           var oldNames: Set<String>?,
           var joinDate: LocalDate?)
    : GDriveEntity(spreadsheetId, sheetName, row) {

    fun getRole(): Role {
        throw UnsupportedOperationException();
    }

    fun getAbsences(): List<Absence> {
        throw UnsupportedOperationException();
    }
}
