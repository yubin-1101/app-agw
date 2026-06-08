package com.company.agw.domain.filter;

enum PassFilterCommandKind {
    WHITE_NUMBER("whiteNUM", 12, true),
    WHITE_PATTERN("whitePattern", 15, true),
    WHITE_ADDRESS("whiteNUMAddr", 12, false),
    BLACK_NUMBER("blackNUM", 12, true),
    BLACK_PATTERN("blackPattern", 15, true),
    BLACK_PREFIX("blackPrefix", 12, true);

    private final String passFieldName;
    private final int dataMaxBytes;
    private final boolean supportsCategoryAndMemo;

    PassFilterCommandKind(String passFieldName, int dataMaxBytes, boolean supportsCategoryAndMemo) {
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
