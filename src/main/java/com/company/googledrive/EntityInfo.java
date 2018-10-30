package com.company.googledrive;

import com.company.googledrive.entity.GDriveEntity;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public abstract class EntityInfo<T extends GDriveEntity> {
    protected final String spreadsheetId;
    protected final String sheetId;
    protected final String range;
    protected final int firstRow;

    public EntityInfo(String spreadsheetId, String sheetId, String range, int firstRow) {
        this.spreadsheetId = spreadsheetId;
        this.sheetId = sheetId;
        this.range = range;
        this.firstRow = firstRow;
    }

    public abstract T parseEntity(List<Object> row, int rowNum);
    public abstract ValueRange toValueRange(T entity);

    public void updateEntities(List<T> entities) {

    }

    public class WriteInfo {
        private String range;

    }
}
