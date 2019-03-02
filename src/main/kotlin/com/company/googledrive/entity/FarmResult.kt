package com.company.googledrive.entity

class FarmResult(spreadsheetId: String,
                 sheetName: String,
                 rowNumber: Int,
                 val userId: String,
                 val userName: String,
                 var stages: List<Int>) : MultiSpreadSheetEntity(spreadsheetId, sheetName, rowNumber)