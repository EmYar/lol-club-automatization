package com.company.googledrive;

import com.company.entity.Pair;
import com.google.api.services.sheets.v4.Sheets;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ScoresParser extends GDriveParser<Map<String, List<Integer>>> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yy");
    private static final int NAME_COLUMN = 0;
    private static final int SCORE_COLUMNS_START = 1;

    private static ScoresParser instance;

    public static ScoresParser getInstance() {
        if (instance == null) {
            instance = new ScoresParser();
        }
        return instance;
    }

    @Override
    protected Pair<String, String> getDocInfo() {
        return new Pair<>("1L4sBldQg2gUHAtjMvLMI3dSLJlPnhzoEksO6PWKXj0k", LocalDate.now().format(formatter));
    }

    @Override
    protected String getRange() {
        return "A2:D";
    }

    @Override
    protected Map<String, List<Integer>> parse(List<List<Object>> rows, Sheets service) throws ParsingException {
        return StreamEx.of(rows)
                .toMap(row -> row.get(NAME_COLUMN).toString(),
                       row -> StreamEx.of(row.subList(SCORE_COLUMNS_START, SCORE_COLUMNS_START + 3))
                               .map(val -> Integer.valueOf(val.toString()))
                               .toList());
    }
}
