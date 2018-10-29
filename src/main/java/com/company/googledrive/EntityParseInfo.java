package com.company.googledrive;

import com.company.googledrive.entity.GDriveEntity;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Collection;
import java.util.List;

public abstract class EntityParseInfo <T extends GDriveEntity> {
    protected final String clientSecretFile = "client_secret.json";
    protected final String credentialsFolder = "credentials";
    protected final Collection<String> scopes = List.of(SheetsScopes.SPREADSHEETS);

    protected final String spreadsheetId;
    protected final String sheetId;
    protected final String range;

    public EntityParseInfo(String spreadsheetId, String sheetId, String range) {
        this.spreadsheetId = spreadsheetId;
        this.sheetId = sheetId;
        this.range = range;
    }

    public abstract T parseEntity(List<Object> row, int rowNum);
}
