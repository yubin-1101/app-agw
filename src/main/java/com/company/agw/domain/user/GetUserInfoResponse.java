package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.company.agw.common.response.PassResponseCode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg",
        "userState",
        "emailAddress",
        "referenceFilterValue",
        "userInfo",
        "lastVisitDt"
})
public class GetUserInfoResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private List<Integer> userState;
    private String emailAddress;
    private Integer referenceFilterValue;
    private String userInfo;
    private String lastVisitDt;

    public static GetUserInfoResponse success(String userID, PassUserInfoEntity userInfo, String lastVisitDt) {
        return GetUserInfoResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공 하였습니다.")
                .userState(PassUserState.fromEntity(userInfo).toSpecArray())
                .emailAddress(defaultString(userInfo.getEmailAddr1()))
                .referenceFilterValue(defaultNumber(userInfo.getFilterKind()))
                .userInfo(userInfo.getCustNum())
                .lastVisitDt(defaultString(lastVisitDt))
                .build();
    }

    public static GetUserInfoResponse fail(String userID, Integer retCode, String retMsg) {
        return GetUserInfoResponse.builder()
                .userID(defaultString(userID))
                .retCode(retCode)
                .retMsg(retMsg)
                .build();
    }

    public static GetUserInfoResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }

    private static Integer defaultNumber(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static Integer defaultNumber(Integer value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
