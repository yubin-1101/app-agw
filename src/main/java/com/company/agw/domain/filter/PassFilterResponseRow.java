package com.company.agw.domain.filter;

import java.util.ArrayList;
import java.util.List;

record PassFilterResponseRow(String id, String data, String category, String memo, String date) {
    static PassFilterResponseRow fromEntity(PassFilterRowEntity entity) {
        return new PassFilterResponseRow(
                defaultString(entity.getId()),
                defaultString(entity.getData()),
                defaultString(entity.getCategory()),
                defaultString(entity.getMemo()),
                defaultString(entity.getDate())
        );
    }

    List<Object> toPassRow() {
        List<Object> row = new ArrayList<>();
        row.add(id);
        row.add(data);
        row.add(category);
        row.add(memo);
        row.add(date);
        return row;
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
