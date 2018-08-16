package googledrive;

import com.google.api.services.sheets.v4.Sheets;
import entity.Pair;
import entity.Role;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Map;

public class RolesParser extends GDriveParser<Map<String, Role>> {

    private static final String RANGE = "A2:C";
    private static final int NAME_COLUMN = 0;
    private static final int ID_COLUMN = 2;

    private static RolesParser instance;

    public static RolesParser getInstance() {
        if (instance == null) {
            instance = new RolesParser();
        }
        return instance;
    }


    @Override
    protected Pair<String, String> getDocInfo() {
        return Constants.ROLES_SHEET;
    }

    @Override
    protected String getRange() {
        return RANGE;
    }

    @Override
    protected Map<String, Role> parse(List<List<Object>> rows, Sheets service) {
        return StreamEx.of(rows)
                .map(row -> new Role(row.get(ID_COLUMN).toString(), row.get(NAME_COLUMN).toString()))
                .toMap(Role::getName, role -> role);
    }
}
