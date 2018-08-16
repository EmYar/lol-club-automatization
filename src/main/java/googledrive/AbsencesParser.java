package googledrive;

import com.google.api.services.sheets.v4.Sheets;
import entity.Pair;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AbsencesParser extends GDriveParser<Map<String, List<Pair<LocalDate, LocalDate>>>> {

    private static final String RANGE = "A8:Z";
    private static final int NAME_COLUMN = 0;
    private static final String DATE_FORMAT = "dd.MM.yy";

    private static AbsencesParser instance = null;

    public static AbsencesParser getInstance() {
        if (instance == null) {
            instance = new AbsencesParser();
        }
        return instance;
    }

    @Override
    protected Pair<String, String> getDocInfo() {
        return Constants.ABSENCES_LIST;
    }

    @Override
    protected String getRange() {
        return RANGE;
    }

    @Override
    protected Map<String, List<Pair<LocalDate, LocalDate>>> parse(List<List<Object>> rows, Sheets service)
            throws ParsingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return StreamEx.of(rows)
                .toMap(row -> row.get(NAME_COLUMN).toString(),
                        row -> StreamEx.of(row.subList(1, row.size()))
                                .map(obj -> {
                                    String[] dates = ((String) obj)
                                            .replaceAll("\\s+", "")
                                            .split("-");
                                    return new Pair<>(LocalDate.parse(dates[0], formatter),
                                            dates[1].equals("0") ? null : LocalDate.parse(dates[1], formatter));
                                })
                                .toList());
    }


}
