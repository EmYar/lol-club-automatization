package com.company.googledrive;

import com.company.googledrive.entity.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class UserParseInfo extends EntityParseInfo<User> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");

    public UserParseInfo() {
        super("1J_rjMFYuTI8FBGqBU9EwN2hQCulDuL8K3it_eFOZfTA", "Участники", "A2:G");
    }

    @Override
    public User parseEntity(List<Object> row, int rowNum) {
        Set<String> oldNames = Set.of(((String) row.get(4)).split("; "));
        LocalDate joinDate = row.size() == 7
                ? LocalDate.parse((CharSequence) row.get(6), FORMATTER)
                : null;
        return new User(spreadsheetId,
                sheetId, rowNum,
                (String) row.get(0),
                (String) row.get(1),
                (String) row.get(2),
                (String) row.get(3),
                oldNames,
                joinDate);
    }
}
