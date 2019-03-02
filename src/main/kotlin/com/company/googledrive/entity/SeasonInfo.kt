package com.company.googledrive.entity

import java.time.Month
import java.time.Year

class SeasonInfo(spreadSheetId: String,
                 sheetId: String,
                 row: Int,
                 val year: Year,
                 val month: Month,
                 val id: Int,
                 val stages: List<Int>)
    : MultiSheetEntity(spreadSheetId, sheetId, row)