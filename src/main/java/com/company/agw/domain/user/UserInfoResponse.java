package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private List<Integer> userState;
    private String emailAddress;
    private Integer referenceFilterValue;
    private String userInfo;
    private String lastVisitDt;

    public static UserInfoResponse success(String userID, PassUserInfoEntity userInfo, String lastVisitDt) {
        return UserInfoResponse.builder()
                .userID(userID)
                .retCode(1000)
                .retMsg("요청 처리 성공 하였습니다.")
                .userState(toUserState(userInfo))
                .emailAddress(defaultString(userInfo.getEmailAddr1()))
                .referenceFilterValue(defaultNumber(userInfo.getFilterKind()))
                .userInfo(userInfo.getCustNum())
                .lastVisitDt(defaultString(lastVisitDt))
                .build();
    }

    public static UserInfoResponse fail(String userID, Integer retCode, String retMsg) {
        return UserInfoResponse.builder()
                .userID(defaultString(userID))
                .retCode(retCode)
                .retMsg(retMsg)
                .build();
    }

    public static UserInfoResponse notJoined() {
        return fail("", 1610, "스팸필터링 서비스 가입 후에 사용 가능합니다.");
    }

    private static List<Integer> toUserState(PassUserInfoEntity userInfo) {
        return List.of(
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

    private static Integer defaultNumber(Integer value) {
        return value == null ? 0 : value;
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
