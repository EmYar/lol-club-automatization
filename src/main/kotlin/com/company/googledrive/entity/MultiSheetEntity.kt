package com.company.googledrive.entity

abstract class MultiSheetEntity(val spreadsheetId: String,
                                val sheetId: String,
                                val row: Int)