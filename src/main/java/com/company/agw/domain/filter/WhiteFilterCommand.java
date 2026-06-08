package com.company.agw.domain.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;

record WhiteFilterCommand(
        String custNum,
        Integer cmdType,
        String id,
        String data,
        String category,
        String memo
) {
    static final int CMD_CREATE = 1;
    static final int CMD_DELETE = 2;
    static final int CMD_UPDATE = 3;

    private static final int REQUEST_ROW_SIZE = 5;
    private static final int ID_MAX_BYTES = 20;

    static WhiteFilterCommand fromPassRow(String custNum, List<Object> row) {
        if (row == null || row.size() < REQUEST_ROW_SIZE) {
            return new WhiteFilterCommand(custNum, null, "", "", "", "");
        }

        return new WhiteFilterCommand(
                custNum,
                toInteger(row.get(0)),
                toStringValue(row.get(1)),
                toStringValue(row.get(2)),
                toStringValue(row.get(3)),
                toStringValue(row.get(4))
        );
    }

    boolean isCreate() {
        return cmdType != null && cmdType == CMD_CREATE;
    }

    boolean isDelete() {
        return cmdType != null && cmdType == CMD_DELETE;
    }

    boolean isUpdate() {
        return cmdType != null && cmdType == CMD_UPDATE;
    }

    boolean needsStoredData() {
        return isCreate() || isUpdate();
    }

    boolean isValidFor(WhiteFilterKind filterKind) {
        if (!isCreate() && !isDelete() && !isUpdate()) {
            return false;
        }

        if ((isDelete() || isUpdate()) && (!hasText(id) || byteLength(id) > ID_MAX_BYTES)) {
            return false;
        }

        if (isCreate() && hasText(id) && byteLength(id) > ID_MAX_BYTES) {
            return false;
        }

        if (needsStoredData() && !hasText(data)) {
            return false;
        }

        if (!filterKind.supportsCategoryAndMemo() && (hasText(category) || hasText(memo))) {
            return false;
        }

        return byteLength(data) <= filterKind.dataMaxBytes();
    }

    UserWhiteFilterCommandEntity toEntity(String saveDt) {
        UserWhiteFilterCommandEntity entity = new UserWhiteFilterCommandEntity();
        entity.setId(id);
        entity.setCustNum(custNum);
        entity.setData(data);
        entity.setCategory(category);
        entity.setMemo(memo);
        entity.setSaveDt(saveDt);
        return entity;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String toStringValue(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value).trim();
    }

    private static Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(toStringValue(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int byteLength(String value) {
        return (value == null ? "" : value).getBytes(StandardCharsets.UTF_8).length;
    }
}
