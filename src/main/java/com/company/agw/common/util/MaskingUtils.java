package com.company.agw.common.util;

public final class MaskingUtils {

    private MaskingUtils() {
    }

    public static String maskMdn(String mdn) {
        if (mdn == null || mdn.length() < 7) {
            return "****";
        }
        return mdn.substring(0, 3) + "****" + mdn.substring(mdn.length() - 4);
    }

    public static String maskUserId(String userId) {
        if (userId == null || userId.length() < 4) {
            return "****";
        }
        return userId.substring(0, 2) + "****" + userId.substring(userId.length() - 2);
    }

    public static String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}
