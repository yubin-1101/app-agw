package com.company.agw.domain.filter;

enum WhiteFilterKind {
    NUMBER("whiteNUM", 12, true),
    PATTERN("whitePattern", 15, true),
    ADDRESS("whiteNUMAddr", 12, false);

    private final String passFieldName;
    private final int dataMaxBytes;
    private final boolean supportsCategoryAndMemo;

    WhiteFilterKind(String passFieldName, int dataMaxBytes, boolean supportsCategoryAndMemo) {
        this.passFieldName = passFieldName;
        this.dataMaxBytes = dataMaxBytes;
        this.supportsCategoryAndMemo = supportsCategoryAndMemo;
    }

    String passFieldName() {
        return passFieldName;
    }

    int dataMaxBytes() {
        return dataMaxBytes;
    }

    boolean supportsCategoryAndMemo() {
        return supportsCategoryAndMemo;
    }
}
