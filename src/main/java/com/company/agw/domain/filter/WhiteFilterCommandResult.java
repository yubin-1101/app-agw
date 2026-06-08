package com.company.agw.domain.filter;

import java.util.ArrayList;
import java.util.List;

record WhiteFilterCommandResult(Integer cmdType, int result, String id, String date, String data) {
    static final int SUCCESS = 0;
    static final int FAILED = 1;
    static final int INVALID_REQUEST = -1;
    static final int DUPLICATED_WHITE = -2;
    static final int DUPLICATED_BLACK = -3;

    static WhiteFilterCommandResult of(WhiteFilterCommand command, int result, String id, String date) {
        return new WhiteFilterCommandResult(command.cmdType(), result, id, date, command.data());
    }

    List<Object> toPassRow() {
        List<Object> row = new ArrayList<>();
        row.add(cmdType == null ? "" : cmdType);
        row.add(result);
        row.add(defaultString(id));
        row.add(defaultString(date));
        row.add(defaultString(data));
        return row;
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
