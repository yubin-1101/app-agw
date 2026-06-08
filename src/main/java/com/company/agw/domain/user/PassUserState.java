package com.company.agw.domain.user;

import java.util.List;
import java.util.Set;

public record PassUserState(
        int emailForwarding,
        int operatorSpam,
        int addressBook,
        int kisaReportAgree,
        int urlSmishingHoldoff,
        int pushNotification,
        int fsecShareAgree,
        int impersonateAlert,
        int blockInternational,
        int blockRoaming
) {
    private static final int REQUIRED_STATE_SIZE = 10;
    private static final Set<Integer> FILTER_VALUES = Set.of(0, 2, 3, 4);

    public static boolean isValidRequest(List<Integer> userState, Integer referenceFilterValue) {
        return userState != null
                && userState.size() == REQUIRED_STATE_SIZE
                && userState.stream().allMatch(value -> value != null)
                && isBinary(userState.get(0))
                && isBinary(userState.get(1))
                && isBinary(userState.get(2))
                && isKisaAgree(userState.get(3))
                && isBinary(userState.get(4))
                && isBinary(userState.get(5))
                && isBinary(userState.get(6))
                && isBinary(userState.get(7))
                && isBinary(userState.get(8))
                && isBinary(userState.get(9))
                && referenceFilterValue != null
                && FILTER_VALUES.contains(referenceFilterValue);
    }

    public static PassUserState fromRequest(List<Integer> userState) {
        return new PassUserState(
                userState.get(0),
                userState.get(1),
                userState.get(2),
                userState.get(3),
                userState.get(4),
                userState.get(5),
                userState.get(6),
                userState.get(7),
                userState.get(8),
                userState.get(9)
        );
    }

    public static PassUserState fromEntity(PassUserInfoEntity userInfo) {
        return new PassUserState(
                defaultNumber(userInfo.getEmailKind()),
                defaultNumber(userInfo.getAuthKind()),
                defaultNumber(userInfo.getAddrFlag()),
                defaultNumber(userInfo.getKisaAgree()),
                defaultNumber(userInfo.getUrlHoldoff()),
                defaultNumber(userInfo.getPushFlag()),
                defaultNumber(userInfo.getFsecAgree()),
                defaultNumber(userInfo.getImpersonateAgree()),
                defaultNumber(userInfo.getBlockInternational()),
                defaultNumber(userInfo.getBlockRoaming())
        );
    }

    public List<Integer> toSpecArray() {
        return List.of(
                emailForwarding,
                operatorSpam,
                addressBook,
                kisaReportAgree,
                urlSmishingHoldoff,
                pushNotification,
                fsecShareAgree,
                impersonateAlert,
                blockInternational,
                blockRoaming
        );
    }

    public PassUserInfoEntity toEntity(String custNum, Integer referenceFilterValue) {
        PassUserInfoEntity userInfo = new PassUserInfoEntity();
        userInfo.setCustNum(custNum);
        userInfo.setEmailKind(String.valueOf(emailForwarding));
        userInfo.setAuthKind(String.valueOf(operatorSpam));
        userInfo.setAddrFlag(String.valueOf(addressBook));
        userInfo.setKisaAgree(String.valueOf(kisaReportAgree));
        userInfo.setUrlHoldoff(String.valueOf(urlSmishingHoldoff));
        userInfo.setPushFlag(pushNotification);
        userInfo.setFsecAgree(String.valueOf(fsecShareAgree));
        userInfo.setImpersonateAgree(String.valueOf(impersonateAlert));
        userInfo.setBlockInternational(String.valueOf(blockInternational));
        userInfo.setBlockRoaming(String.valueOf(blockRoaming));
        userInfo.setFilterKind(String.valueOf(referenceFilterValue));
        return userInfo;
    }

    private static boolean isBinary(Integer value) {
        return value == 0 || value == 1;
    }

    private static boolean isKisaAgree(Integer value) {
        return value == 0 || value == 1 || value == 2;
    }

    private static int defaultNumber(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int defaultNumber(Integer value) {
        return value == null ? 0 : value;
    }
}
