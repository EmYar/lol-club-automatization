package com.company.googledrive.entity

abstract class MultiSpreadSheetEntity(val spreadsheetId: String,
                                      val sheetId: String,
                                      val rowNumber: Int)