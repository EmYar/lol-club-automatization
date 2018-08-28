package com.company.googledrive;

import com.company.entity.Pair;
import com.company.entity.Role;
import com.company.entity.User;
import com.google.api.services.sheets.v4.Sheets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UsersParser extends GDriveParser<Map<String, User>> {
    private static final String RANGE = "A2:F";
    private static final int ID_COLUMN = 0;
    private static final int NAME_COLUMN = 1;
    private static final int VK_PAGE_COLUMN = 2;
    private static final int DISCORD_COLUMN = 3;
    private static final int OLD_NAMES_COLUMN = 4;
    private static final int ROLE_COLUMN = 5;

    private static UsersParser instance = null;

    public static UsersParser getInstance() {
        if (instance == null) {
            instance = new UsersParser();
        }
        return instance;
    }

    @Override
    protected Pair<String, String> getDocInfo() {
        return Constants.MEMBERS_LIST;
    }

    @Override
    protected String getRange() {
        return RANGE;
    }

    @Override
    protected Map<String, User> parse(List<List<Object>> rows, Sheets service) throws ParsingException {
        Map<String, Role> roles = RolesParser.getInstance().parse(service);
        Map<String, User> users = new HashMap<>(rows.size());
        for (List<Object> row : rows) {
            User user = new User(row.get(NAME_COLUMN).toString());

            user.setAccountId(row.get(ID_COLUMN).toString());
            user.setVkPage(row.get(VK_PAGE_COLUMN).toString());
            user.setDiscord(row.get(DISCORD_COLUMN).toString());
            user.setOldNames(Set.of(row.get(OLD_NAMES_COLUMN).toString()
                    .replaceAll("\\s+", "")
                    .split(";")));
            user.setRole(roles.get(row.get(ROLE_COLUMN).toString()));

            users.put(user.getName(), user);
            for (String oldName : user.getOldNames()) {
                users.put(oldName, user);
            }
        }
        return users;
    }
}
